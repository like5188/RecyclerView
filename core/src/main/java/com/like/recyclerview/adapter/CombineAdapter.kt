package com.like.recyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.Logger
import com.like.paging.PagingResult
import com.like.paging.RequestType
import com.like.recyclerview.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * 组合[BaseListAdapter]和[BaseLoadStateAdapter]。并绑定[PagingResult]或者[Flow]类型的数据。
 * 功能：
 * 1、支持添加 Header 加载状态、Footer 加载状态。
 * 2、支持初始化、刷新时进度条的显示隐藏。
 * 3、支持成功失败回调。
 * 4、封装了初始化、刷新、往后加载更多、往前加载更多操作。并对这些操作做了并发处理，并发处理规则如下：
 * ①、初始化、刷新：如果有操作正在执行，则取消正在执行的操作，执行新操作。
 * ②、往后加载更多、往前加载更多：如果有操作正在执行，则放弃新操作，否则执行新操作。
 */
open class CombineAdapter<ValueInList> {
    private lateinit var recyclerView: RecyclerView
    internal val adapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    private lateinit var listAdapter: BaseListAdapter<*, ValueInList>
    private var loadStateAdapter: BaseLoadStateAdapter<*>? = null
    private lateinit var pagingResult: PagingResult<List<ValueInList>?>
    private val concurrencyHelper = ConcurrencyHelper()

    /**
     * 初始化或者刷新开始时显示进度条
     */
    open var show: (() -> Unit)? = null

    /**
     * 初始化或者刷新完成时隐藏进度条
     */
    open var hide: (() -> Unit)? = null

    /**
     * 请求失败时回调
     */
    open var onError: (suspend (RequestType, Throwable) -> Unit)? = null

    /**
     * 请求成功时回调
     */
    open var onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null

    internal fun attachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    fun itemCount() = listAdapter.itemCount

    /**
     * 设置加载状态视图到 Header，固定于 [RecyclerView] 顶部，用于往前加载更多
     */
    fun withLoadStateHeader(adapter: BaseLoadStateAdapter<*>) {
        adapter.onLoadMore = ::before
        adapter.isAfter = false
        this.loadStateAdapter = adapter
    }

    /**
     * 设置加载状态视图到 Footer，固定于 [RecyclerView] 底部，用于往后加载更多
     */
    fun withLoadStateFooter(adapter: BaseLoadStateAdapter<*>) {
        adapter.onLoadMore = ::after
        adapter.isAfter = true
        this.loadStateAdapter = adapter
    }

    /**
     * 设置（不分页）列表数据，固定于 [RecyclerView] 中部。
     * 注意：[withListAdapter]、[withPagingListAdapter]这两个方法必须调用一个
     */
    fun withListAdapter(adapter: BaseListAdapter<*, ValueInList>, flow: Flow<List<ValueInList>?>) {
        this.listAdapter = adapter
        this.pagingResult = PagingResult(flow) {}
    }

    /**
     * 设置（分页）列表数据，固定于 [RecyclerView] 中部，并且加载更多的数据是添加到其中。
     * 注意：[withListAdapter]、[withPagingListAdapter]这两个方法必须调用一个
     * @param pagingResult  列表需要的数据。使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
     */
    fun withPagingListAdapter(adapter: BaseListAdapter<*, ValueInList>, pagingResult: PagingResult<List<ValueInList>?>) {
        this.listAdapter = adapter
        this.pagingResult = pagingResult
    }

    /**
     * 初始化操作（线程安全）
     */
    suspend fun initial() {
        val requestType = RequestType.Initial
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult.setRequestType.invoke(requestType)
            collect(requestType)
        }
    }

    /**
     * 刷新操作（线程安全）
     */
    suspend fun refresh() {
        val requestType = RequestType.Refresh
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult.setRequestType.invoke(requestType)
            collect(requestType)
        }
    }

    /**
     * 往后加载更多操作（线程安全）
     */
    private suspend fun after() {
        val requestType = RequestType.After
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult.setRequestType.invoke(requestType)
            collect(requestType)
        }
    }

    /**
     * 往前加载更多操作（线程安全）
     */
    private suspend fun before() {
        val requestType = RequestType.Before
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult.setRequestType.invoke(requestType)
            collect(requestType)
        }
    }

    private suspend fun collect(requestType: RequestType) {
        pagingResult.flow.flowOn(Dispatchers.IO)
            .onStart {
                if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
                    show?.invoke()
                }
            }.onCompletion {
                if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
                    hide?.invoke()
                }
            }.catch {
                if (requestType is RequestType.After || requestType is RequestType.Before) {
                    // 加载更多失败时，直接更新[loadMoreAdapter]
                    loadStateAdapter?.error(it)
                }
                onError?.invoke(requestType, it)
            }.flowOn(Dispatchers.Main)
            .collect { items ->
                Logger.e(items)
                // 是否往前加载更多。处理逻辑分为两种：1、往前加载更多；2、往后加载更多或者不分页；
                val loadMoreBefore = loadStateAdapter?.isAfter == false
                val hasMore = hasMore(items)
                if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
                    if (!items.isNullOrEmpty()) {
                        // 添加列表数据
                        // 添加 adapter
                        if (loadMoreBefore) {
                            if (hasMore) {
                                adapter.addIfAbsent(loadStateAdapter)
                            }
                            adapter.addIfAbsent(listAdapter)
                        } else {
                            adapter.addIfAbsent(listAdapter)
                            if (hasMore) {
                                adapter.addIfAbsent(loadStateAdapter)
                            }
                        }
                        listAdapter.submitList(items) {
                            // RecyclerView 界面位置处理
                            if (loadMoreBefore) {
                                recyclerView.scrollToBottom()
                            } else {
                                recyclerView.scrollToTop()
                            }
                            // 更新 loadStateAdapter 的状态
                            if (hasMore) {
                                // 此处必须放在 submitList 的回调里面，并且使用 postDelayed 来提交，达到双重保障。当然也可以监听数据的插入来处理，但是比较麻烦。
                                // 否则会由于调用本方法时界面还没有真正收到新的数据，
                                // 导致 loadStateAdapter 还显示于界面中（实际上插入新的数据后，它有可能会处于界面外了，此时不应该触发加载更多），
                                // 导致错误的调用加载更多。
                                recyclerView.postDelayed({ loadStateAdapter?.hasMore(true) }, 100)
                            }
                        }
                    }
                } else {// 加载更多
                    if (!items.isNullOrEmpty()) {
                        // 添加列表数据
                        val newItems = listAdapter.currentList.toMutableList()
                        if (loadMoreBefore) {
                            newItems.addAll(0, items)
                        } else {
                            newItems.addAll(items)
                        }
                        listAdapter.submitList(newItems) {
                            // RecyclerView 界面位置处理
                            if (loadMoreBefore) {
                                recyclerView.keepPosition(items.size, 1)
                            }
                            // 更新 loadStateAdapter 的状态
                            if (hasMore) {
                                recyclerView.postDelayed({ loadStateAdapter?.hasMore(false) }, 100)
                            }
                        }
                    } else {
                        loadStateAdapter?.end()
                    }
                }
                onSuccess?.invoke(requestType, items)
            }
    }

    /**
     * 是否有更多数据
     * 判断由使用者提供。因为 listAdapter 中有可能包含 header（比如 banner） 和 item。
     * 我们一般需要根据 item 来判断。如果全部是 header 数据的话，就不应该有更多数据。
     */
    open fun hasMore(list: List<ValueInList>?): Boolean {
        return !list.isNullOrEmpty()
    }

}
