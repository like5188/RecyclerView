package com.like.recyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.RequestType
import com.like.paging.Result
import com.like.paging.ResultReport
import com.like.paging.bind
import com.like.recyclerview.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

/*
 * 绑定分页或者不分页数据到 ConcatAdapter。
 */

/**
 * 不分页（线程安全）
 *
 * @param result            获取列表数据的代码块
 * @param headerAdapter     header
 * @param itemAdapter       列表
 * @param emptyAdapter      空视图
 * @param errorAdapter      错误视图
 * @param show              显示进度条
 * @param hide              隐藏进度条
 * @param onSuccess         请求成功时回调，在这里进行额外数据处理。
 * 返回值为一个集合，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
 * @param onError           请求失败时回调，在这里进行额外错误处理，这里默认在初始化失败时添加了错误视图，刷新失败时没有做处理。
 */
@OptIn(FlowPreview::class)
fun <ResultType, ValueInList> ConcatAdapter.bind(
    recyclerView: RecyclerView,
    result: (suspend () -> ResultType),
    headerAdapter: BaseAdapter<*, ValueInList>? = null,
    itemAdapter: BaseAdapter<*, ValueInList>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onSuccess: suspend (ResultType) -> List<List<ValueInList>?>? = {
        if (headerAdapter == null) {// 如果返回值[ResultType]为 List<ValueInList>? 类型
            listOf(emptyList(), it as? List<ValueInList>)
        } else {// 如果返回值[ResultType]为 List<List<ValueInList>?>? 类型
            it as? List<List<ValueInList>?>
        }
    },
    onError: (suspend (Throwable) -> Unit)? = null,
): Flow<ResultType> {
    val contentAdapter = ConcatAdapter()
    return bind(
        recyclerView = recyclerView,
        result = result,
        contentAdapter = contentAdapter,
        emptyAdapter = emptyAdapter,
        errorAdapter = errorAdapter,
        show = show,
        hide = hide,
        onSuccess = {
            val res = onSuccess(it)
            if (!res.isNullOrEmpty()) {
                val headers = res.getOrNull(0)
                val items = res.getOrNull(1)
                contentAdapter.clear()
                if (!headers.isNullOrEmpty() && headerAdapter != null) {
                    contentAdapter.add(headerAdapter)
                    headerAdapter.clear()
                    headerAdapter.addAllToEnd(headers)
                }
                if (!items.isNullOrEmpty()) {
                    contentAdapter.add(itemAdapter)
                    itemAdapter.clear()
                    itemAdapter.addAllToEnd(items)
                }
            }
        },
        onError = onError
    )
}

/**
 * 不分页（线程安全）
 *
 * @param result                    获取数据的代码块
 * @param contentAdapter            内容，可以包括列表、header等。
 * @param emptyAdapter              空视图
 * @param errorAdapter              错误视图
 * @param show                      显示进度条
 * @param hide                      隐藏进度条
 * @param onSuccess                 请求成功时回调，在这里对[contentAdapter]进行数据处理。
 * @param onError                   请求失败时回调，在这里进行额外错误处理，这里默认在初始化失败时添加了错误视图，刷新失败时没有做处理。
 */
@OptIn(FlowPreview::class)
fun <ResultType> ConcatAdapter.bind(
    recyclerView: RecyclerView,
    result: (suspend () -> ResultType),
    contentAdapter: RecyclerView.Adapter<*>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onSuccess: suspend (ResultType) -> Unit,
    onError: (suspend (Throwable) -> Unit)? = null,
): Flow<ResultType> {
    var isFirstLoad = true
    return result.asFlow().flowOn(Dispatchers.IO)
        .onStart {
            show?.invoke()
        }.onEach {
            onSuccess(it)
            if (contentAdapter.itemCount == 0) {
                clear()
                add(emptyAdapter)
            } else {
                clear()
                add(contentAdapter)
                recyclerView.scrollToTop()
            }
        }.catch {
            if (isFirstLoad) {// 初始化时才显示错误视图
                clear()
                add(errorAdapter)
                errorAdapter?.onError(it)
            }
            onError?.invoke(it)
        }.onCompletion {
            hide?.invoke()
            isFirstLoad = false
        }.flowOn(Dispatchers.Main)
}

/**
 * 往后分页（线程安全）
 *
 * @param result            使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
 * @param headerAdapter     header
 * @param itemAdapter       列表
 * @param loadMoreAdapter   加载更多视图
 * @param emptyAdapter      空视图
 * @param errorAdapter      错误视图
 * @param show              显示进度条
 * @param hide              隐藏进度条
 * @param onSuccess         请求成功时回调，在这里进行额外数据处理。
 * 返回值为一个集合，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
 * @param onError           请求失败时回调，在这里进行额外错误处理，这里默认在初始化或者加载更多失败时添加了错误视图，刷新失败时没有做处理。
 */
fun <ResultType, ValueInList> ConcatAdapter.bindLoadAfter(
    recyclerView: RecyclerView,
    result: Result<ResultType>,
    headerAdapter: BaseAdapter<*, ValueInList>? = null,
    itemAdapter: BaseAdapter<*, ValueInList>,
    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onSuccess: suspend (RequestType, ResultType) -> List<List<ValueInList>?>? = { requestType, resultType ->
        if (headerAdapter == null) {// 如果返回值[ResultType]为 List<ValueInList>? 类型
            listOf(emptyList(), resultType as? List<ValueInList>)
        } else {// 如果返回值[ResultType]为 List<List<ValueInList>?>? 类型
            resultType as? List<List<ValueInList>?>
        }
    },
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
): Flow<ResultReport<ResultType>> {
    val contentAdapter = ConcatAdapter()
    return this@bindLoadAfter.bindLoadAfter(
        recyclerView = recyclerView,
        result = result,
        contentAdapter = contentAdapter,
        loadMoreAdapter = loadMoreAdapter,
        emptyAdapter = emptyAdapter,
        errorAdapter = errorAdapter,
        show = show,
        hide = hide,
        onSuccess = { requestType, resultType ->
            val res = onSuccess(requestType, resultType)
            when {
                requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                    if (res.isNullOrEmpty()) {
                        0
                    } else {
                        val headers = res.getOrNull(0)
                        val items = res.getOrNull(1)
                        contentAdapter.clear()
                        if (!headers.isNullOrEmpty() && headerAdapter != null) {
                            contentAdapter.add(headerAdapter)
                            headerAdapter.clear()
                            headerAdapter.addAllToEnd(headers)
                        }
                        if (!items.isNullOrEmpty()) {
                            contentAdapter.add(itemAdapter)
                            itemAdapter.clear()
                            itemAdapter.addAllToEnd(items)
                        }
                        if (headers.isNullOrEmpty() && items.isNullOrEmpty()) {
                            0
                        } else if (!items.isNullOrEmpty()) {
                            2
                        } else {
                            1
                        }
                    }
                }
                requestType is RequestType.After || requestType is RequestType.Before -> {
                    val items = res?.getOrNull(1)
                    if (!items.isNullOrEmpty()) {
                        itemAdapter.addAllToEnd(items)
                        3
                    } else {
                        4
                    }
                }
                else -> -1
            }
        },
        onError = onError
    )
}

/**
 * 往后分页（线程安全）
 *
 * @param result            使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
 * @param contentAdapter    内容，可以包括列表、header等。
 * @param loadMoreAdapter   加载更多视图
 * @param emptyAdapter      空视图
 * @param errorAdapter      错误视图
 * @param show              初始化或者刷新开始时显示进度条
 * @param hide              初始化或者刷新成功或者失败时隐藏进度条
 * @param onSuccess         请求成功时回调，在这里对[contentAdapter]进行数据处理。
 * 返回值：
 * 0：显示空视图；
 * 1：不显示空视图，没有更多数据需要加载（只有 Header 的情况）；
 * 2：不显示空视图，有更多数据需要加载（有列表数据的情况）；
 * 3：还有更多数据需要加载；
 * 4：没有更多数据需要加载；
 * @param onError           请求失败时回调，在这里进行额外错误处理，这里默认在初始化或者加载更多失败时添加了错误视图，刷新失败时没有做处理。
 */
fun <ResultType> ConcatAdapter.bindLoadAfter(
    recyclerView: RecyclerView,
    result: Result<ResultType>,
    contentAdapter: RecyclerView.Adapter<*>,
    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onSuccess: suspend (RequestType, ResultType) -> Int,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
): Flow<ResultReport<ResultType>> = result.bind(
    onSuccess = { requestType, resultType ->
        when (onSuccess(requestType, resultType)) {
            0 -> {// 显示空视图
                clear()
                add(emptyAdapter)
            }
            1 -> {// 不显示空视图，没有更多数据需要加载（只有 Header 的情况）
                clear()
                add(contentAdapter)
                recyclerView.scrollToTop()
            }
            2 -> {// 不显示空视图，有更多数据需要加载（有列表数据的情况）
                clear()
                addAll(contentAdapter, loadMoreAdapter)
                loadMoreAdapter.reload()
                loadMoreAdapter.onComplete()
                recyclerView.scrollToTop()
            }
            3 -> {// 还有更多数据需要加载
                loadMoreAdapter.reload()
                loadMoreAdapter.onComplete()
            }
            4 -> {// 没有更多数据需要加载
                loadMoreAdapter.onEnd()
            }
        }
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
 * 往前分页（线程安全）
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
    listAdapter: BaseAdapter<*, ValueInList>,
    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
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