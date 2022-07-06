package com.like.recyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.Logger
import com.like.paging.PagingResult
import com.like.paging.RequestType
import com.like.recyclerview.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

open class ConcatAdapterWrapper<ResultType, ValueInList>(
    private val recyclerView: RecyclerView,
    private val itemAdapter: BaseAdapter<*, ValueInList>
) {
    private val adapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    private var headerAdapter: BaseAdapter<*, ValueInList>? = null
    private var loadMoreAdapter: BaseLoadMoreAdapter<*, *>? = null
    private val mConcurrencyHelper = ConcurrencyHelper()
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

    fun bindData(pagingResult: PagingResult<ResultType>) {
        this.pagingResult = pagingResult
    }

    fun bindData(flow: Flow<ResultType>) {
        this.flow = flow
    }

    fun withHeader(header: BaseAdapter<*, ValueInList>) {
        this.headerAdapter = header
    }

    fun withFooter(footer: BaseLoadMoreAdapter<*, *>, isAfter: Boolean = true) {
        footer.onLoadMore = if (isAfter) {
            ::after
        } else {
            ::before
        }
        this.isAfter = isAfter
        this.loadMoreAdapter = footer
    }

    suspend fun initial() {
        mConcurrencyHelper.cancelPreviousThenRun {
            pagingResult?.setRequestType?.invoke(RequestType.Initial)
            collect(RequestType.Initial, show, hide, onError, onSuccess)
        }
    }

    suspend fun refresh() {
        mConcurrencyHelper.cancelPreviousThenRun {
            pagingResult?.setRequestType?.invoke(RequestType.Refresh)
            collect(RequestType.Refresh, show, hide, onError, onSuccess)
        }
    }

    private suspend fun after() {
        mConcurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.After)
            collect(RequestType.After, show, hide, onError, onSuccess)
        }
    }

    private suspend fun before() {
        mConcurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.Before)
            collect(RequestType.Before, show, hide, onError, onSuccess)
        }
    }

    private suspend fun collect(
        requestType: RequestType,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
        onError: (suspend (RequestType, Throwable) -> Unit)? = null,
        onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
    ) {
        val flow = pagingResult?.flow ?: flow ?: return
        flow.flowOn(Dispatchers.IO)
            .onStart {
                Logger.d("RecyclerView requestType=$requestType")
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
                val res = transformer(requestType, resultType)
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
                                itemAdapter.clear()
                                if (isAfter == null || isAfter == true) {
                                    itemAdapter.addAllToEnd(items)
                                    adapter.addAll(itemAdapter, loadMoreAdapter)
                                } else {
                                    itemAdapter.addAllToStart(items)
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
                                itemAdapter.addAllToEnd(items)
                            } else if (isAfter == false) {
                                itemAdapter.addAllToStart(items)
                                recyclerView.keepPosition(items.size, 1)
                            }
                            loadMoreAdapter?.hasMore()
                        }
                    }
                }
                onSuccess?.invoke(requestType, resultType)
            }
    }

    @Suppress("UNCHECKED_CAST")
    open suspend fun transformer(requestType: RequestType, resultType: ResultType): List<List<ValueInList>?>? {
        return if (resultType !is List<*> || resultType.isNullOrEmpty()) {
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
