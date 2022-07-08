package com.like.recyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.PagingResult
import com.like.paging.RequestType
import com.like.recyclerview.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * 组合[BaseListAdapter]和[BaseLoadStateAdapter]。并绑定[PagingResult]或者[Flow]类型的数据。
 * 功能：
 * 1、支持添加加载状态：Header、Footer。
 * 2、支持初始化、刷新时进度条的显示隐藏。
 * 3、支持成功失败回调。
 * 4、封装了初始化、刷新、往后加载更多、往前加载更多操作。并对这些操作做了并发处理，并发处理规则如下：
 * ①、初始化、刷新：如果有操作正在执行，则取消正在执行的操作，执行新操作。
 * ②、往后加载更多、往前加载更多：如果有操作正在执行，则放弃新操作，否则执行新操作。
 */
open class CombineAdapter<ValueInList>(private val recyclerView: RecyclerView) {
    private val adapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    private var listAdapter: BaseListAdapter<*, ValueInList>? = null
    private var loadStateAdapter: BaseLoadStateAdapter<*>? = null
    private var pagingResult: PagingResult<List<ValueInList>?>? = null
    private val concurrencyHelper = ConcurrencyHelper()

    init {
        recyclerView.adapter = adapter
    }

    /**
     * 初始化或者刷新开始时显示进度条
     */
    var show: (() -> Unit)? = null

    /**
     * 初始化或者刷新完成时隐藏进度条
     */
    var hide: (() -> Unit)? = null

    /**
     * 请求失败时回调
     */
    var onError: (suspend (RequestType, Throwable) -> Unit)? = null

    /**
     * 请求成功时回调
     */
    var onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null

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
     */
    fun withListAdapter(adapter: BaseListAdapter<*, ValueInList>, flow: Flow<List<ValueInList>?>) {
        this.listAdapter = adapter
        this.pagingResult = PagingResult(flow) {}
    }

    /**
     * 设置（分页）列表数据，固定于 [RecyclerView] 中部，并且加载更多的数据是添加到其中。
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
        val f = pagingResult?.flow ?: return
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult?.setRequestType?.invoke(RequestType.Initial)
            collect(RequestType.Initial, f, show, hide, onError, onSuccess)
        }
    }

    /**
     * 刷新操作（线程安全）
     */
    suspend fun refresh() {
        val f = pagingResult?.flow ?: return
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult?.setRequestType?.invoke(RequestType.Refresh)
            collect(RequestType.Refresh, f, show, hide, onError, onSuccess)
        }
    }

    /**
     * 往后加载更多操作（线程安全）
     */
    private suspend fun after() {
        val f = pagingResult?.flow ?: return
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.After)
            collect(RequestType.After, f, show, hide, onError, onSuccess)
        }
    }

    /**
     * 往前加载更多操作（线程安全）
     */
    private suspend fun before() {
        val f = pagingResult?.flow ?: return
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.Before)
            collect(RequestType.Before, f, show, hide, onError, onSuccess)
        }
    }

    private suspend fun collect(
        requestType: RequestType,
        flow: Flow<List<ValueInList>?>,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
        onError: (suspend (RequestType, Throwable) -> Unit)? = null,
        onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null,
    ) {
        flow.flowOn(Dispatchers.IO)
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
                if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
                    if (!items.isNullOrEmpty()) {
                        listAdapter?.apply {
                            submitList(items)
                            if (loadStateAdapter?.isAfter == false) {// 往前加载更多
                                adapter.addIfAbsent(loadStateAdapter)
                                adapter.addIfAbsent(this)
                            } else {// 不分页或者往后加载更多
                                adapter.addIfAbsent(this)
                                adapter.addIfAbsent(loadStateAdapter)
                            }
                        }

                        if (loadStateAdapter?.isAfter == false) {// 往前加载更多
                            recyclerView.scrollToBottom()
                        } else {
                            recyclerView.scrollToTop()
                        }

                        loadStateAdapter?.hasMore()
                    }
                } else {
                    if (!items.isNullOrEmpty()) {
                        listAdapter?.apply {
                            val newItems = currentList.toMutableList()
                            if (loadStateAdapter?.isAfter == false) {// 往前加载更多
                                newItems.addAll(0, items)
                                submitList(newItems)
                                recyclerView.keepPosition(items.size, 1)
                            } else {
                                newItems.addAll(items)
                                submitList(newItems)
                            }
                        }
                        // 还有更多数据需要加载
                        loadStateAdapter?.hasMore()
                    } else {
                        // 没有更多数据需要加载
                        loadStateAdapter?.end()
                    }
                }
                onSuccess?.invoke(requestType, items)
            }
    }

}
