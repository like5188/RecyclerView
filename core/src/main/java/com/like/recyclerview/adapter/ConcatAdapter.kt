package com.like.recyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.RequestType
import com.like.paging.Result
import com.like.recyclerview.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 收集不分页数据到 ConcatAdapter（线程安全）
 *
 * @param dataFlow          获取数据的[Flow]
 * @param headerAdapter     header
 * @param itemAdapter       列表
 * @param emptyAdapter      空视图
 * @param errorAdapter      错误视图
 * @param transformer       在这里进行数据转换，返回值为一个集合，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
 * @param show              初始化或者刷新开始时显示进度条
 * @param hide              初始化或者刷新成功或者失败时隐藏进度条
 * @param onError           请求失败时回调，在这里进行额外错误处理，这里默认在初始化失败时添加了错误视图，刷新失败时没有做处理。
 * @param onSuccess         请求成功时回调
 * @return 操作请求数据的代码块
 */
@OptIn(FlowPreview::class)
suspend fun <ResultType, ValueInList> ConcatAdapter.collectFlow(
    dataFlow: Flow<ResultType>,
    recyclerView: RecyclerView,
    headerAdapter: BaseAdapter<*, ValueInList>? = null,
    itemAdapter: BaseAdapter<*, ValueInList>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    transformer: suspend (ResultType) -> List<List<ValueInList>?>? = {
        if (headerAdapter == null) {// 如果返回值[ResultType]为 List<ValueInList>? 类型
            listOf(emptyList(), it as? List<ValueInList>)
        } else {// 如果返回值[ResultType]为 List<List<ValueInList>?>? 类型
            it as? List<List<ValueInList>?>
        }
    },
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onError: (suspend (Throwable) -> Unit)? = null,
    onSuccess: (suspend (ResultType) -> Unit)? = null,
): suspend () -> Unit {
    return collectFlow(
        recyclerView = recyclerView,
        dataFlow = dataFlow,
        contentAdapter = ConcatAdapter(),
        emptyAdapter = emptyAdapter,
        errorAdapter = errorAdapter,
        contentAdapterDataHandler = { contentAdapter, resultType ->
            val res = transformer(resultType)
            val headers = res?.getOrNull(0)
            val items = res?.getOrNull(1)
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
        },
        show = show,
        hide = hide,
        onError = onError,
        onSuccess = onSuccess
    )
}

/**
 * 收集不分页数据到 ConcatAdapter（线程安全）
 *
 * @param dataFlow                      获取数据的[Flow]
 * @param contentAdapter                内容，可以包括列表、header等。
 * @param emptyAdapter                  空视图
 * @param errorAdapter                  错误视图
 * @param contentAdapterDataHandler     [contentAdapter]数据处理
 * @param show                          初始化或者刷新开始时显示进度条
 * @param hide                          初始化或者刷新成功或者失败时隐藏进度条
 * @param onError                       请求失败时回调，在这里进行额外错误处理，这里默认在初始化失败时添加了错误视图，刷新失败时没有做处理。
 * @param onSuccess                     请求成功时回调
 * @return 操作请求数据的代码块
 */
@OptIn(FlowPreview::class)
private suspend fun <ResultType, ContentAdapter : RecyclerView.Adapter<*>> ConcatAdapter.collectFlow(
    dataFlow: Flow<ResultType>,
    recyclerView: RecyclerView,
    contentAdapter: ContentAdapter,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    contentAdapterDataHandler: suspend (ContentAdapter, ResultType) -> Unit,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onError: (suspend (Throwable) -> Unit)? = null,
    onSuccess: (suspend (ResultType) -> Unit)? = null,
): suspend () -> Unit {
    val isFirstLoad = AtomicBoolean(true)
    val flow = dataFlow.flowOn(Dispatchers.IO)
        .onStart {
            show?.invoke()
        }.onCompletion {
            hide?.invoke()
        }.catch {
            if (isFirstLoad.compareAndSet(true, false)) {// 初始化时才显示错误视图
                clear()
                add(errorAdapter)
                errorAdapter?.onError(it)
            }
            onError?.invoke(it)
        }.flowOn(Dispatchers.Main)
    val collect = suspend {
        flow.collect {
            contentAdapterDataHandler(contentAdapter, it)
            if (contentAdapter.itemCount == 0) {
                clear()
                add(emptyAdapter)
            } else {
                clear()
                add(contentAdapter)
                recyclerView.scrollToTop()
            }
            onSuccess?.invoke(it)
        }
    }
    collect()
    return collect
}

/**
 * 收集往后分页数据到 ConcatAdapter（线程安全）
 *
 * @param result            使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
 * @param headerAdapter     header
 * @param itemAdapter       列表
 * @param loadMoreAdapter   加载更多视图
 * @param emptyAdapter      空视图
 * @param errorAdapter      错误视图
 * @param transformer       在这里进行数据转换，返回值为一个集合，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
 * @param show              初始化或者刷新开始时显示进度条
 * @param hide              初始化或者刷新成功或者失败时隐藏进度条
 * @param onError           请求失败时回调，在这里进行额外错误处理，这里默认在初始化或者加载更多失败时添加了错误视图，刷新失败时没有做处理。
 * @param onSuccess         请求成功时回调。
 */
suspend fun <ResultType, ValueInList> ConcatAdapter.collectResultForLoadAfter(
    result: Result<ResultType>,
    recyclerView: RecyclerView,
    headerAdapter: BaseAdapter<*, ValueInList>? = null,
    itemAdapter: BaseAdapter<*, ValueInList>,
    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    transformer: suspend (RequestType, ResultType) -> List<List<ValueInList>?>? = { requestType, resultType ->
        if (headerAdapter == null) {// 如果返回值[ResultType]为 List<ValueInList>? 类型
            listOf(emptyList(), resultType as? List<ValueInList>)
        } else {// 如果返回值[ResultType]为 List<List<ValueInList>?>? 类型
            resultType as? List<List<ValueInList>?>
        }
    },
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
    onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
) {
    collectResultForLoadAfter(
        recyclerView = recyclerView,
        result = result,
        contentAdapter = ConcatAdapter(),
        loadMoreAdapter = loadMoreAdapter,
        emptyAdapter = emptyAdapter,
        errorAdapter = errorAdapter,
        contentAdapterDataHandler = { contentAdapter, requestType, resultType ->
            val res = transformer(requestType, resultType)
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
        show = show,
        hide = hide,
        onError = onError,
        onSuccess = onSuccess
    )
}

/**
 * 收集往后分页数据到 ConcatAdapter（线程安全）
 *
 * @param result                        使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
 * @param contentAdapter                内容，可以包括列表、header等。
 * @param loadMoreAdapter               加载更多视图
 * @param emptyAdapter                  空视图
 * @param errorAdapter                  错误视图
 * @param contentAdapterDataHandler     [contentAdapter]数据处理
 * 返回值：
 * 0：显示空视图；
 * 1：不显示空视图，没有更多数据需要加载（只有 Header 的情况）；
 * 2：不显示空视图，有更多数据需要加载（有列表数据的情况）；
 * 3：还有更多数据需要加载；
 * 4：没有更多数据需要加载；
 * @param show                          初始化或者刷新开始时显示进度条
 * @param hide                          初始化或者刷新成功或者失败时隐藏进度条
 * @param onError                       请求失败时回调，在这里进行额外错误处理，这里默认在初始化或者加载更多失败时添加了错误视图，刷新失败时没有做处理。
 * @param onSuccess                     请求成功时回调。
 */
private suspend fun <ResultType, ContentAdapter : RecyclerView.Adapter<*>> ConcatAdapter.collectResultForLoadAfter(
    result: Result<ResultType>,
    recyclerView: RecyclerView,
    contentAdapter: ContentAdapter,
    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    contentAdapterDataHandler: suspend (ContentAdapter, RequestType, ResultType) -> Int,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
    onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
) {
    result.collect(
        show = show,
        hide = hide,
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
        }
    ) { requestType, resultType ->
        when (contentAdapterDataHandler(contentAdapter, requestType, resultType)) {
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
        onSuccess?.invoke(requestType, resultType)
    }
}

/**
 * 收集往前分页数据到 ConcatAdapter（线程安全）
 *
 * @param result            使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
 * @param itemAdapter       列表。
 * @param loadMoreAdapter   加载更多视图
 * @param emptyAdapter      空视图
 * @param errorAdapter      错误视图
 * @param transformer       在这里进行数据转换，返回值为一个集合。
 * @param show              初始化或者刷新开始时显示进度条
 * @param hide              初始化或者刷新成功或者失败时隐藏进度条
 * @param onError           请求失败时回调，在这里进行额外错误处理，这里默认在初始化或者加载更多失败时添加了错误视图，刷新失败时没有做处理。
 * @param onSuccess         请求成功时回调。
 */
suspend fun <ResultType, ValueInList> ConcatAdapter.collectResultForLoadBefore(
    result: Result<ResultType>,
    recyclerView: RecyclerView,
    itemAdapter: BaseAdapter<*, ValueInList>,
    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    transformer: suspend (RequestType, ResultType) -> List<ValueInList>? = { requestType, resultType ->
        resultType as? List<ValueInList>
    },
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
    onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
) {
    result.collect(
        show = show,
        hide = hide,
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
        }
    ) { requestType, resultType ->
        val res = transformer(requestType, resultType)
        when {
            requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                if (res.isNullOrEmpty()) {
                    clear()
                    add(emptyAdapter)
                } else {
                    clear()
                    addAll(loadMoreAdapter, itemAdapter)
                    itemAdapter.clear()
                    itemAdapter.addAllToEnd(res)
                    loadMoreAdapter.reload()
                    loadMoreAdapter.onComplete()
                    recyclerView.scrollToBottom()
                }
            }
            requestType is RequestType.After || requestType is RequestType.Before -> {
                if (res.isNullOrEmpty()) {
                    loadMoreAdapter.onEnd()
                } else {
                    itemAdapter.addAllToStart(res)
                    recyclerView.keepPosition(res.size, 1)
                    loadMoreAdapter.reload()
                    loadMoreAdapter.onComplete()
                }
            }
        }
        onSuccess?.invoke(requestType, resultType)
    }

}
