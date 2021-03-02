package com.like.recyclerview.ui.util

import com.like.paging.RequestState
import com.like.paging.RequestType
import com.like.paging.Result
import com.like.paging.ResultReport
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.model.*
import com.like.recyclerview.ui.DefaultEmptyItem
import com.like.recyclerview.ui.DefaultErrorItem
import com.like.recyclerview.ui.DefaultLoadMoreFooter
import com.like.recyclerview.ui.DefaultLoadMoreHeader
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

/**
 * [com.like.paging.Result] 扩展功能。
 */

/**
 * 开始搜集数据。
 *
 * @param onFailed      失败回调，如果需要做其它错误处理，可以从这里获取。
 * @param onSuccess     成功回调，如果需要结果，可以从这里获取。
 */
suspend fun <ResultType> Result<ResultType>.collect(
    onFailed: (suspend (RequestType, Throwable) -> Unit)? = null,
    onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
) {
    resultReportFlow.collect { resultReport ->
        val state = resultReport.state
        val type = resultReport.type
        when (state) {
            is RequestState.Failed -> {
                onFailed?.invoke(type, state.throwable)
            }
            is RequestState.Success<ResultType> -> {
                onSuccess?.invoke(type, state.data)
            }
        }
    }
}

/**
 * 绑定进度条。
 * 初始化或者刷新时控制进度条的显示隐藏。
 *
 * @param show          初始化或者刷新开始时显示进度条
 * @param hide          初始化或者刷新成功或者失败时隐藏进度条
 */
fun <ResultType> Result<ResultType>.bindProgress(
    show: () -> Unit,
    hide: () -> Unit,
): Result<ResultType> {
    val newResultReportFlow = resultReportFlow.onEach { resultReport ->
        val state = resultReport.state
        val type = resultReport.type
        if (type is RequestType.Initial || type is RequestType.Refresh) {
            when (state) {
                is RequestState.Running -> {
                    show()
                }
                else -> {
                    hide()
                }
            }
        }
    }
    return updateResultReportFlow(newResultReportFlow)
}

/**
 * 绑定 [androidx.recyclerview.widget.RecyclerView]
 * 功能包括：Item数据的添加、空视图、错误视图、往后加载更多视图、往前加载更多视图
 *
 * @param emptyItem         数据为空时显示的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultEmptyItem]
 * @param errorItem         请求出错时显示的视图。只针对初始化出错时才显示，刷新出错时不显示。[com.like.recyclerview.ui]库中默认实现了：[DefaultErrorItem]
 * @param loadMoreFooter    往后加载更多的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultLoadMoreFooter]
 * @param loadMoreHeader    往前加载更多的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultLoadMoreHeader]
 */
fun <ValueInList : IRecyclerViewItem> Result<List<ValueInList>?>.bindRecyclerView(
    adapter: BaseAdapter,
    emptyItem: IEmptyItem? = null,
    errorItem: IErrorItem? = null,
    loadMoreFooter: ILoadMoreFooter? = null,
    loadMoreHeader: ILoadMoreHeader? = null,
): Result<List<ValueInList>?> {
    val newResultReportFlow = resultReportFlow.onEach { resultReport ->
        val state = resultReport.state
        val type = resultReport.type
        when {
            (type is RequestType.Initial || type is RequestType.Refresh) && state is RequestState.Success -> {
                val list = state.data
                if (list.isNullOrEmpty()) {
                    emptyItem?.let {
                        adapter.setEmptyItem(emptyItem)
                    }
                } else {
                    adapter.clearAndAddAll(list)
                    loadMoreFooter?.let {
                        loadMoreFooter.onLoading()
                        adapter.addFooterToEnd(loadMoreFooter)
                    }
                    loadMoreHeader?.let {
                        loadMoreHeader.onLoading()
                        adapter.addHeaderToStart(loadMoreHeader)
                    }
                }
            }
            type is RequestType.Initial && state is RequestState.Failed -> {
                errorItem?.let {
                    errorItem.throwable = state.throwable
                    adapter.setErrorItem(errorItem)
                }
            }
            type is RequestType.After && state is RequestState.Success -> {
                loadMoreFooter?.let {
                    val list = state.data
                    if (list.isNullOrEmpty()) {
                        // 到底了
                        loadMoreFooter.onEnd()
                    } else {
                        loadMoreFooter.onLoading()
                        adapter.addItemsToEnd(list.map {
                            it as IItem
                        })
                    }
                }
            }
            type is RequestType.After && state is RequestState.Failed -> {
                loadMoreFooter?.onError()
            }
            type is RequestType.Before && state is RequestState.Success -> {
                loadMoreHeader?.let {
                    val list = state.data
                    if (list.isNullOrEmpty()) {
                        // 到顶了
                        loadMoreHeader.onEnd()
                    } else {
                        loadMoreHeader.onLoading()
                        adapter.addItemsToStart(list.map {
                            it as IItem
                        })
                    }
                }
            }
            type is RequestType.Before && state is RequestState.Failed -> {
                loadMoreHeader?.onError()
            }
        }
    }
    return updateResultReportFlow(newResultReportFlow)
}

fun <ResultType> Result<ResultType>.updateResultReportFlow(newResultReportFlow: Flow<ResultReport<ResultType>>): Result<ResultType> {
    return Result(newResultReportFlow, initial, refresh, retry, loadAfter, loadBefore)
}