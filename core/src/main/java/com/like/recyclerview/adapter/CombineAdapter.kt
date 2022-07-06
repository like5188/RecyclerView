package com.like.recyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.PagingResult
import com.like.paging.RequestType
import com.like.recyclerview.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * 对 Header、Footer、Item 三种 Adapter 进行组合。并绑定[PagingResult]或者[Flow]类型的数据。
 * 功能：
 * 1、支持添加 Header、Footer。
 * 2、支持初始化、刷新时进度条的显示隐藏。
 * 3、支持成功失败回调。
 * 4、封装了初始化、刷新、往后加载更多、往前加载更多操作。并对这些操作做了并发处理，并发处理规则如下：
 * ①、初始化、刷新：如果有操作正在执行，则取消正在执行的操作，执行新操作。
 * ②、往后加载更多、往前加载更多：如果有操作正在执行，则放弃新操作，否则执行新操作。
 */
open class CombineAdapter<ResultType, ValueInList>(private val recyclerView: RecyclerView) {
    private val adapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    private var headerAdapter: BaseAdapter<*, ValueInList>? = null
    private var itemAdapter: BaseAdapter<*, ValueInList>? = null
    private var loadMoreAdapter: BaseLoadMoreAdapter<*, *>? = null
    private val concurrencyHelper = ConcurrencyHelper()
    private var pagingResult: PagingResult<ResultType>? = null
    private var flow: Flow<ResultType>? = null
    private var isAfter: Boolean? = null

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
    var onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null

    /**
     * 绑定数据源
     * 注意：两个 [bindData] 方法至少调用一个
     *
     * @param pagingResult  使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
     */
    fun bindData(pagingResult: PagingResult<ResultType>) {
        this.pagingResult = pagingResult
    }

    /**
     * 绑定数据源
     */
    fun bindData(flow: Flow<ResultType>) {
        this.flow = flow
    }

    /**
     * 设置 Header
     */
    fun withHeaderAdapter(adapter: BaseAdapter<*, ValueInList>) {
        this.headerAdapter = adapter
    }

    /**
     * 设置 列表
     */
    fun withItemAdapter(adapter: BaseAdapter<*, ValueInList>) {
        this.itemAdapter = adapter
    }

    /**
     * 设置 Footer
     * @param adapter    加载更多视图 的 adapter
     * @param isAfter   是否是往后加载更多。true：往后加载更多；false：往前加载更多；默认为 null，表示不分页。
     */
    fun withFooterAdapter(adapter: BaseLoadMoreAdapter<*, *>, isAfter: Boolean = true) {
        adapter.onLoadMore = if (isAfter) {
            ::after
        } else {
            ::before
        }
        this.isAfter = isAfter
        this.loadMoreAdapter = adapter
    }

    /**
     * 初始化操作（线程安全）
     */
    suspend fun initial() {
        val realFlow = pagingResult?.flow ?: flow ?: return
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult?.setRequestType?.invoke(RequestType.Initial)
            collect(RequestType.Initial, realFlow, show, hide, onError, onSuccess)
        }
    }

    /**
     * 刷新操作（线程安全）
     */
    suspend fun refresh() {
        val realFlow = pagingResult?.flow ?: flow ?: return
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult?.setRequestType?.invoke(RequestType.Refresh)
            collect(RequestType.Refresh, realFlow, show, hide, onError, onSuccess)
        }
    }

    /**
     * 往后加载更多操作（线程安全）
     */
    private suspend fun after() {
        val realFlow = pagingResult?.flow ?: flow ?: return
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.After)
            collect(RequestType.After, realFlow, show, hide, onError, onSuccess)
        }
    }

    /**
     * 往前加载更多操作（线程安全）
     */
    private suspend fun before() {
        val realFlow = pagingResult?.flow ?: flow ?: return
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.Before)
            collect(RequestType.Before, realFlow, show, hide, onError, onSuccess)
        }
    }

    private suspend fun collect(
        requestType: RequestType,
        flow: Flow<ResultType>,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
        onError: (suspend (RequestType, Throwable) -> Unit)? = null,
        onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
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
                    loadMoreAdapter?.error(it)
                }
                onError?.invoke(requestType, it)
            }.flowOn(Dispatchers.Main)
            .collect { resultType ->
                val res = transform(requestType, resultType)
                when (requestType) {
                    is RequestType.Initial, is RequestType.Refresh -> {
                        adapter.clear()
                        if (!res.isNullOrEmpty()) {
                            val headers = res.getOrNull(0)
                            val items = res.getOrNull(1)
                            // 不分页或者往后加载更多时，才添加 header
                            if ((isAfter == null || isAfter == true) && !headers.isNullOrEmpty() && headerAdapter != null) {
                                headerAdapter!!.clear()
                                headerAdapter!!.addAllToEnd(headers)
                                adapter.add(headerAdapter)
                            }
                            if (!items.isNullOrEmpty()) {
                                itemAdapter?.clear()
                                if (isAfter == null || isAfter == true) {
                                    itemAdapter?.addAllToEnd(items)
                                    adapter.addAll(itemAdapter, loadMoreAdapter)
                                } else {
                                    itemAdapter?.addAllToStart(items)
                                    adapter.addAll(loadMoreAdapter, itemAdapter)
                                }
                            }
                            if (isAfter == null || isAfter == true) {
                                recyclerView.scrollToTop()
                            } else {
                                recyclerView.scrollToBottom()
                            }
                            if (!items.isNullOrEmpty()) {
                                loadMoreAdapter?.hasMore()
                            }
                        }
                    }
                    is RequestType.After, is RequestType.Before -> {
                        val items = res?.getOrNull(1)
                        if (items.isNullOrEmpty()) {
                            // 没有更多数据需要加载
                            loadMoreAdapter?.end()
                        } else {
                            // 还有更多数据需要加载
                            if (isAfter == true) {
                                itemAdapter?.addAllToEnd(items)
                            } else if (isAfter == false) {
                                itemAdapter?.addAllToStart(items)
                                recyclerView.keepPosition(items.size, 1)
                            }
                            loadMoreAdapter?.hasMore()
                        }
                    }
                }
                onSuccess?.invoke(requestType, resultType)
            }
    }

    /**
     * 在这里进行数据转换，返回值为一个集合 List<List<ValueInList>?>?，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
     */
    @Suppress("UNCHECKED_CAST")
    protected open suspend fun transform(requestType: RequestType, resultType: ResultType): List<List<ValueInList>?>? {
        // resultType !is List<*> 的情况需要开发者自己处理，比如需要从 ResultType 中提取出 List 来使用。
        return if (resultType !is List<*> || resultType.isNullOrEmpty()
        ) {
            null
        } else {
            var r = true
            for (i in 0 until resultType.size) {
                if (resultType[i] !is List<*>) {
                    r = false
                    break
                }
            }
            if (r) {// 返回值[ResultType]为 List<List<ValueInList>?>? 类型
                resultType as? List<List<ValueInList>?>
            } else {// 返回值[ResultType]为 List<ValueInList>? 类型
                if (headerAdapter == null) {
                    listOf(null, resultType as? List<ValueInList>)
                } else {
                    listOf(resultType as? List<ValueInList>)
                }
            }
        }
    }

}
