package com.like.recyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.PagingResult
import com.like.paging.RequestType
import com.like.recyclerview.utils.*
import kotlinx.coroutines.flow.Flow

/**
 * 组合[BaseListAdapter]和[BaseLoadStateAdapter]。并绑定[PagingResult]或者[Flow]类型的数据。
 * 如果不需要加载状态视图，可以直接使用[BaseListAdapter]
 * 功能：
 * 1、支持添加 Header 加载状态、Footer 加载状态。
 * 2、[PagingResultCollector]支持的功能。
 */
open class CombineAdapter<ValueInList>(
    val concatAdapter: ConcatAdapter = ConcatAdapter(
        ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build()
    )
) {
    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: BaseListAdapter<*, ValueInList>
    private var loadStateAdapter: BaseLoadStateAdapter<*>? = null
    private val pagingResultCollector = PagingResultCollector<ValueInList>()

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

    private val callback = object : PagingResultCollector.Callback<ValueInList> {
        override fun onShow() {
            this@CombineAdapter.show?.invoke()
        }

        override fun onHide() {
            this@CombineAdapter.hide?.invoke()
        }

        override suspend fun onError(requestType: RequestType, throwable: Throwable) {
            // 初始化或者刷新失败时保持界面原样，就算加载状态视图显示加载中也不管。
            if (requestType is RequestType.After || requestType is RequestType.Before) {
                // 加载更多失败时，直接更新[loadMoreAdapter]
                loadStateAdapter?.error(throwable)
            }
            this@CombineAdapter.onError?.invoke(requestType, throwable)
        }

        override suspend fun onSuccess(requestType: RequestType, list: List<ValueInList>?) {
            // 是否往前加载更多。处理逻辑分为两种：1、往前加载更多；2、往后加载更多或者不分页；
            val loadMoreBefore = loadStateAdapter?.isAfter == false
            val items = getItems(list)// list 中可能包含 header（比如 banner） 和 items。
            if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
                if (!list.isNullOrEmpty()) {
                    // 添加列表 adapter
                    concatAdapter.addIfAbsent(listAdapter)
                    // 添加列表数据
                    listAdapter.submitList(list) {
                        if (!items.isNullOrEmpty() && loadStateAdapter != null) {// 如果全部是 header 数据的话，就不应该有更多数据。也就是说，只要有列表数据，就显示加载状态视图。
                            if (loadMoreBefore) {
                                concatAdapter.addIfAbsent(0, loadStateAdapter)
                            } else {
                                concatAdapter.addIfAbsent(loadStateAdapter)
                            }
                        }
                        // RecyclerView 界面位置处理
                        if (loadMoreBefore) {
                            recyclerView.scrollToBottom()
                        } else {
                            recyclerView.scrollToTop()
                        }
                        // 更新 loadStateAdapter 的状态
                        if (!items.isNullOrEmpty() && loadStateAdapter != null) {
                            loadStateAdapter?.hasMore()
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
                        loadStateAdapter?.hasMore()
                    }
                } else {
                    loadStateAdapter?.end()
                }
            }
            this@CombineAdapter.onSuccess?.invoke(requestType, list)
        }

    }

    protected fun itemCount() = listAdapter.itemCount

    fun attachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    /**
     * 设置加载状态视图到 Header，固定于 [RecyclerView] 顶部，用于往前加载更多
     */
    fun withLoadStateHeader(adapter: BaseLoadStateAdapter<*>) {
        adapter.onLoadMore = pagingResultCollector::before
        adapter.isAfter = false
        this.loadStateAdapter = adapter
    }

    /**
     * 设置加载状态视图到 Footer，固定于 [RecyclerView] 底部，用于往后加载更多
     */
    fun withLoadStateFooter(adapter: BaseLoadStateAdapter<*>) {
        adapter.onLoadMore = pagingResultCollector::after
        adapter.isAfter = true
        this.loadStateAdapter = adapter
    }

    /**
     * 设置列表 adapter
     */
    fun withListAdapter(adapter: BaseListAdapter<*, ValueInList>) {
        this.listAdapter = adapter
    }

    /**
     * 提交列表数据（不分页）。最后必须调用一个[collectFrom]方法去能触发初始化操作。
     */
    suspend fun collectFrom(flow: Flow<List<ValueInList>?>) {
        collectFrom(PagingResult(flow) {})
    }

    /**
     * 提交列表数据（分页）。最后必须调用一个[collectFrom]方法去能触发初始化操作。
     * @param pagingResult  列表需要的数据。使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
     */
    open suspend fun collectFrom(pagingResult: PagingResult<List<ValueInList>?>) {
        pagingResultCollector.collectFrom(pagingResult, callback)
    }

    /**
     * 刷新操作（线程安全）
     */
    suspend fun refresh() {
        pagingResultCollector.refresh()
    }

    /**
     * 从[list]中获取列表数据 items（列表数据可能包含 header（比如 banner） 和 items）。
     * 需要根据 items 来判断是否还有更多，以及加载更多时需要添加 items 到列表中。
     */
    open fun getItems(list: List<ValueInList>?): List<ValueInList>? {
        return list
    }

}
