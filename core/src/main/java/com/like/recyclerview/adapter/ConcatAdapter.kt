package com.like.recyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.RequestType
import com.like.paging.Result
import com.like.paging.ResultReport
import com.like.paging.bind
import com.like.recyclerview.utils.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/*
 * 绑定分页或者不分页数据到 ConcatAdapter。
 */

/**
 * 不分页
 *
 * @param result            获取列表数据的代码块
 * @param listAdapter       列表
 * @param emptyAdapter      空视图
 * @param errorAdapter      错误视图
 * @param show              显示进度条
 * @param hide              隐藏进度条
 * @param onSuccess         请求成功时回调，在这里进行额外数据处理。
 * @param onError           请求失败时回调，在这里进行额外错误处理。
 */
@OptIn(FlowPreview::class)
suspend fun <ValueInList> ConcatAdapter.bind(
    result: (suspend () -> List<ValueInList>?),
    listAdapter: AbstractAdapter<*, ValueInList>,
    emptyAdapter: AbstractAdapter<*, *>? = null,
    errorAdapter: AbstractErrorAdapter<*, *>? = null,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onSuccess: (suspend (List<ValueInList>?) -> Unit)? = null,
    onError: (suspend (Throwable) -> Unit)? = null,
): Flow<List<ValueInList>?> = result.asFlow().onStart {
    show?.invoke()
}.onEach {
    if (it.isNullOrEmpty()) {
        clear()
        add(emptyAdapter)
    } else {
        clear()
        add(listAdapter)
        listAdapter.clear()
        listAdapter.addAllToEnd(it)
    }
    onSuccess?.invoke(it)
}.onCompletion {
    hide?.invoke()
}.catch {
    clear()
    add(errorAdapter)
    errorAdapter?.onError(it)
    onError?.invoke(it)
}

/**
 * 不分页
 *
 * @param result                    获取数据的代码块
 * @param contentAdapter            内容，可以包括列表、header等。
 * @param emptyAdapter              空视图
 * @param errorAdapter              错误视图
 * @param show                      显示进度条
 * @param hide                      隐藏进度条
 * @param onSuccess                 请求成功时回调，在这里进行额外数据处理。
 * 返回值表示是否显示空视图
 * @param onError                   请求失败时回调，在这里进行额外错误处理。
 */
@OptIn(FlowPreview::class)
suspend fun <ResultType> ConcatAdapter.bind(
    result: (suspend () -> ResultType),
    contentAdapter: RecyclerView.Adapter<*>,
    emptyAdapter: AbstractAdapter<*, *>? = null,
    errorAdapter: AbstractErrorAdapter<*, *>? = null,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onSuccess: suspend (ResultType) -> Boolean,
    onError: (suspend (Throwable) -> Unit)? = null,
): Flow<ResultType> = result.asFlow().onStart {
    show?.invoke()
}.onEach {
    if (onSuccess(it)) {
        clear()
        add(emptyAdapter)
    } else {
        clear()
        add(contentAdapter)
    }
}.onCompletion {
    hide?.invoke()
}.catch {
    clear()
    add(errorAdapter)
    errorAdapter?.onError(it)
    onError?.invoke(it)
}

/**
 * 往后分页
 *
 * @param result            使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
 * @param listAdapter       列表。
 * @param loadMoreAdapter   加载更多视图
 * @param emptyAdapter      空视图
 * @param errorAdapter      错误视图
 * @param show              初始化或者刷新开始时显示进度条
 * @param hide              初始化或者刷新成功或者失败时隐藏进度条
 * @param onSuccess         请求成功时回调，在这里进行额外数据处理。
 * @param onError           请求失败时回调，在这里进行额外错误处理。
 */
fun <ValueInList> ConcatAdapter.bindLoadAfter(
    recyclerView: RecyclerView,
    result: Result<List<ValueInList>?>,
    listAdapter: AbstractAdapter<*, ValueInList>,
    loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
    emptyAdapter: AbstractAdapter<*, *>? = null,
    errorAdapter: AbstractErrorAdapter<*, *>? = null,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
): Flow<ResultReport<List<ValueInList>?>> = result.bind(
    onSuccess = { requestType, data ->
        when {
            requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                if (data.isNullOrEmpty()) {
                    clear()
                    add(emptyAdapter)
                } else {
                    clear()
                    addAll(listAdapter, loadMoreAdapter)
                    listAdapter.clear()
                    listAdapter.addAllToEnd(data)
                    loadMoreAdapter.reload()
                    loadMoreAdapter.onComplete()
                    recyclerView.scrollToTop()
                }
            }
            requestType is RequestType.After || requestType is RequestType.Before -> {
                if (data.isNullOrEmpty()) {
                    loadMoreAdapter.onEnd()
                } else {
                    listAdapter.addAllToEnd(data)
                    loadMoreAdapter.reload()
                    loadMoreAdapter.onComplete()
                }
            }
        }
        onSuccess?.invoke(requestType, data)
    },
    onError = { requestType, throwable ->
        when {
            requestType is RequestType.Initial -> {
                clear()
                add(errorAdapter)
                errorAdapter?.onError(throwable)
            }
            requestType is RequestType.After || requestType is RequestType.Before -> {
                loadMoreAdapter.onError(throwable)
            }
        }
        onError?.invoke(requestType, throwable)
    },
    show = show,
    hide = hide,
)

/**
 * 往后分页
 *
 * @param result                    使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
 * @param onInitialOrRefreshSuccess 初始化或者刷新成功时回调。
 * 返回值：0：显示空视图；1：不显示空视图，没有更多数据需要加载；2：不显示空视图，有更多数据需要加载；
 * @param onLoadMoreSuccess         处理加载更多成功时回调。
 * 返回值表示是否还有更多数据需要加载。
 * @param contentAdapter            内容，可以包括列表、header等。
 * @param loadMoreAdapter           加载更多视图
 * @param emptyAdapter              空视图
 * @param errorAdapter              错误视图
 * @param show                      初始化或者刷新开始时显示进度条
 * @param hide                      初始化或者刷新成功或者失败时隐藏进度条
 * @param onSuccess                 请求成功时回调，在这里进行额外数据处理。
 * @param onError                   请求失败时回调，在这里进行额外错误处理。
 */
fun <ResultType> ConcatAdapter.bindLoadAfter(
    recyclerView: RecyclerView,
    result: Result<ResultType>,
    onInitialOrRefreshSuccess: (ResultType) -> Int,
    onLoadMoreSuccess: (ResultType) -> Boolean,
    contentAdapter: RecyclerView.Adapter<*>,
    loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
    emptyAdapter: AbstractAdapter<*, *>? = null,
    errorAdapter: AbstractErrorAdapter<*, *>? = null,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
): Flow<ResultReport<ResultType>> = result.bind(
    onSuccess = { requestType, data ->
        when {
            requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                when (onInitialOrRefreshSuccess(data)) {
                    0 -> {// 显示空视图
                        clear()
                        add(emptyAdapter)
                    }
                    1 -> {// 不显示空视图，没有更多数据需要加载
                        clear()
                        add(contentAdapter)
                        recyclerView.scrollToTop()
                    }
                    2 -> {// 不显示空视图，有更多数据需要加载
                        clear()
                        add(contentAdapter)
                        add(loadMoreAdapter)
                        loadMoreAdapter.reload()
                        loadMoreAdapter.onComplete()
                        recyclerView.scrollToTop()
                    }
                }
            }
            requestType is RequestType.After || requestType is RequestType.Before -> {
                if (onLoadMoreSuccess(data)) {
                    loadMoreAdapter.onEnd()
                } else {
                    loadMoreAdapter.reload()
                    loadMoreAdapter.onComplete()
                }
            }
        }
        onSuccess?.invoke(requestType, data)
    },
    onError = { requestType, throwable ->
        when {
            requestType is RequestType.Initial -> {
                clear()
                add(errorAdapter)
                errorAdapter?.onError(throwable)
            }
            requestType is RequestType.After || requestType is RequestType.Before -> {
                loadMoreAdapter.onError(throwable)
            }
        }
        onError?.invoke(requestType, throwable)
    },
    show = show,
    hide = hide,
)

/**
 * 往前分页
 *
 * @param result            使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
 * @param listAdapter       列表。
 * @param loadMoreAdapter   加载更多视图
 * @param emptyAdapter      空视图
 * @param errorAdapter      错误视图
 * @param show              初始化或者刷新开始时显示进度条
 * @param hide              初始化或者刷新成功或者失败时隐藏进度条
 * @param onSuccess         请求成功时回调，在这里进行额外数据处理。
 * @param onError           请求失败时回调，在这里进行额外错误处理。
 */
fun <ValueInList> ConcatAdapter.bindLoadBefore(
    recyclerView: RecyclerView,
    result: Result<List<ValueInList>?>,
    listAdapter: AbstractAdapter<*, ValueInList>,
    loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
    emptyAdapter: AbstractAdapter<*, *>? = null,
    errorAdapter: AbstractErrorAdapter<*, *>? = null,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
): Flow<ResultReport<List<ValueInList>?>> = result.bind(
    onSuccess = { requestType, data ->
        when {
            requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                if (data.isNullOrEmpty()) {
                    clear()
                    add(emptyAdapter)
                } else {
                    clear()
                    addAll(loadMoreAdapter, listAdapter)
                    listAdapter.clear()
                    listAdapter.addAllToEnd(data)
                    loadMoreAdapter.reload()
                    loadMoreAdapter.onComplete()
                    recyclerView.scrollToBottom()
                }
            }
            requestType is RequestType.After || requestType is RequestType.Before -> {
                if (data.isNullOrEmpty()) {
                    loadMoreAdapter.onEnd()
                } else {
                    listAdapter.addAllToStart(data)
                    recyclerView.keepPosition(data.size, 1)
                    loadMoreAdapter.reload()
                    loadMoreAdapter.onComplete()
                }
            }
        }
        onSuccess?.invoke(requestType, data)
    },
    onError = { requestType, throwable ->
        when {
            requestType is RequestType.Initial -> {
                clear()
                add(errorAdapter)
                errorAdapter?.onError(throwable)
            }
            requestType is RequestType.After || requestType is RequestType.Before -> {
                loadMoreAdapter.onError(throwable)
            }
        }
        onError?.invoke(requestType, throwable)
    },
    show = show,
    hide = hide,
)