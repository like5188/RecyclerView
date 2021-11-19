package com.like.recyclerview.utils

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.PagingResult
import com.like.paging.RequestType
import com.like.paging.util.ConcurrencyHelper
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.adapter.BaseErrorAdapter
import com.like.recyclerview.adapter.BaseLoadMoreAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

fun ConcatAdapter.clear() {
    adapters.forEach {
        this.removeAdapter(it)
    }
}

fun ConcatAdapter.contains(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?): Boolean {
    adapter ?: return false
    adapters.forEach {
        if (it == adapter) {
            return true
        }
    }
    return false
}

fun ConcatAdapter.add(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    adapter ?: return
    addAdapter(adapter)
}

fun ConcatAdapter.addAll(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    adapters.forEach {
        add(it)
    }
}

fun ConcatAdapter.remove(adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    adapter ?: return
    removeAdapter(adapter)
}

fun ConcatAdapter.removeAll(vararg adapters: RecyclerView.Adapter<out RecyclerView.ViewHolder>?) {
    adapters.forEach {
        remove(it)
    }
}

/**
 * 收集不分页数据[Flow]并显示到 ConcatAdapter（线程安全）
 *
 * @param dataFlow          获取数据的[Flow]
 * @param recyclerView
 * @param headerAdapter     header 的 adapter
 * @param itemAdapter       列表的 adapter
 * @param emptyAdapter      空视图 的 adapter
 * @param errorAdapter      错误视图 的 adapter
 * @param transformer       在这里进行数据转换，返回值为一个集合，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
 * @param show              请求开始时显示进度条
 * @param hide              请求完成时隐藏进度条
 * @param onError           请求失败时回调。
 * 在这里进行额外错误处理：
 * 如果当前显示的是列表，则不处理，否则显示[errorAdapter]；
 * @param onSuccess         请求成功时回调
 */
fun <ResultType, ValueInList> ConcatAdapter.bindFlow(
    dataFlow: Flow<ResultType>,
    recyclerView: RecyclerView,
    headerAdapter: BaseAdapter<*, ValueInList>? = null,
    itemAdapter: BaseAdapter<*, ValueInList>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    transformer: suspend (ResultType) -> List<List<ValueInList>?>? = { resultType ->
        if (headerAdapter == null) {// 如果返回值[ResultType]为 List<ValueInList>? 类型
            if (resultType is List<*>? && !resultType.isNullOrEmpty()) {
                listOf(emptyList(), resultType as? List<ValueInList>)
            } else {
                emptyList()
            }
        } else {// 如果返回值[ResultType]为 List<List<ValueInList>?>? 类型
            resultType as? List<List<ValueInList>?>
        }
    },
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
    onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
): RequestHandler<ResultType> = RequestHandler(dataFlow).apply {
    this.show = show
    this.hide = hide
    this.onError = { requestType, throwable ->
        when (adapters.firstOrNull()) {
            null -> {
                add(errorAdapter)
                errorAdapter?.onError(throwable)
            }
            errorAdapter -> {
                errorAdapter?.onError(throwable)
            }
            emptyAdapter -> {
                clear()
                add(errorAdapter)
                errorAdapter?.onError(throwable)
            }
        }
        onError?.invoke(requestType, throwable)
    }
    this.onSuccess = { requestType, resultType ->
        val res = transformer(resultType)
        clear()
        if (res.isNullOrEmpty()) {
            // 显示空视图
            add(emptyAdapter)
        } else {
            val headers = res.getOrNull(0)
            val items = res.getOrNull(1)
            if (!headers.isNullOrEmpty() && headerAdapter != null) {
                headerAdapter.clear()
                headerAdapter.addAllToEnd(headers)
                add(headerAdapter)
            }
            if (!items.isNullOrEmpty()) {
                itemAdapter.clear()
                itemAdapter.addAllToEnd(items)
                add(itemAdapter)
            }
            recyclerView.scrollToTop()
        }
        onSuccess?.invoke(requestType, resultType)
    }
}

/**
 * 绑定往后分页数据并显示到 ConcatAdapter
 */
fun <ResultType, ValueInList> ConcatAdapter.bindAfterPagingResult(
    pagingResult: PagingResult<ResultType>,
    recyclerView: RecyclerView,
    headerAdapter: BaseAdapter<*, ValueInList>? = null,
    itemAdapter: BaseAdapter<*, ValueInList>,
    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    transformer: suspend (RequestType, ResultType) -> List<List<ValueInList>?>? = { requestType, resultType ->
        if (headerAdapter == null) {// 如果返回值[ResultType]为 List<ValueInList>? 类型
            if (resultType is List<*>? && !resultType.isNullOrEmpty()) {
                listOf(emptyList(), resultType as? List<ValueInList>)
            } else {
                emptyList()
            }
        } else {// 如果返回值[ResultType]为 List<List<ValueInList>?>? 类型
            resultType as? List<List<ValueInList>?>
        }
    },
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
    onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
): RequestHandler<ResultType> = bindPagingResult(
    true, pagingResult, recyclerView, headerAdapter, itemAdapter, loadMoreAdapter,
    emptyAdapter, errorAdapter, transformer, show, hide, onError, onSuccess
).apply {
    loadMoreAdapter.onLoadMore = ::after
}

/**
 * 绑定往前分页数据并显示到 ConcatAdapter
 */
fun <ResultType, ValueInList> ConcatAdapter.bindBeforePagingResult(
    pagingResult: PagingResult<ResultType>,
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
): RequestHandler<ResultType> = bindPagingResult(
    false, pagingResult, recyclerView, null, itemAdapter, loadMoreAdapter, emptyAdapter, errorAdapter,
    transformer = { requestType, resultType ->
        if (resultType is List<*>? && !resultType.isNullOrEmpty()) {
            listOf(emptyList(), transformer(requestType, resultType))
        } else {
            emptyList()
        }
    },
    show, hide, onError, onSuccess
).apply {
    loadMoreAdapter.onLoadMore = ::before
}

/**
 * 收集分页数据[PagingResult]并显示到 ConcatAdapter（线程安全）
 *
 * @param isAfter           是否是往后加载更多。true：往后加载更多；false：往前加载更多；
 * @param pagingResult      使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
 * @param recyclerView
 * @param headerAdapter     header 的 adapter
 * @param itemAdapter       列表的 adapter
 * @param loadMoreAdapter   加载更多视图 的 adapter
 * @param emptyAdapter      空视图 的 adapter
 * @param errorAdapter      错误视图 的 adapter
 * @param transformer       在这里进行数据转换，返回值为一个集合，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
 * @param show              初始化或者刷新开始时显示进度条
 * @param hide              初始化或者刷新完成时隐藏进度条
 * @param onError           请求失败时回调。
 * 在这里进行额外错误处理：
 * 初始化或者刷新失败时，如果当前显示的是列表，则不处理，否则显示[errorAdapter]；
 * 加载更多失败时，直接更新[loadMoreAdapter]。
 * @param onSuccess         请求成功时回调。
 */
private fun <ResultType, ValueInList> ConcatAdapter.bindPagingResult(
    isAfter: Boolean,
    pagingResult: PagingResult<ResultType>,
    recyclerView: RecyclerView,
    headerAdapter: BaseAdapter<*, ValueInList>? = null,
    itemAdapter: BaseAdapter<*, ValueInList>,
    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
    emptyAdapter: BaseAdapter<*, *>? = null,
    errorAdapter: BaseErrorAdapter<*, *>? = null,
    transformer: suspend (RequestType, ResultType) -> List<List<ValueInList>?>?,
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onError: (suspend (RequestType, Throwable) -> Unit)? = null,
    onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null,
): RequestHandler<ResultType> = RequestHandler(pagingResult).apply {
    this.show = show
    this.hide = hide
    this.onError = { requestType, throwable ->
        when {
            requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                // 初始化或者刷新失败时，如果当前显示的是列表，则不处理，否则显示[errorAdapter]
                when (adapters.firstOrNull()) {
                    null -> {
                        add(errorAdapter)
                        errorAdapter?.onError(throwable)
                    }
                    errorAdapter -> {
                        errorAdapter?.onError(throwable)
                    }
                    emptyAdapter -> {
                        clear()
                        add(errorAdapter)
                        errorAdapter?.onError(throwable)
                    }
                }
            }
            requestType is RequestType.After || requestType is RequestType.Before -> {
                // 加载更多失败时，直接更新[loadMoreAdapter]
                loadMoreAdapter.onError(throwable)
            }
        }
        onError?.invoke(requestType, throwable)
    }
    this.onSuccess = { requestType, resultType ->
        val res = transformer(requestType, resultType)
        when {
            requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                clear()
                if (res.isNullOrEmpty()) {
                    // 显示空视图
                    add(emptyAdapter)
                } else {
                    val headers = res.getOrNull(0)
                    val items = res.getOrNull(1)
                    // 往后加载更多时，才添加 header
                    if (isAfter && !headers.isNullOrEmpty() && headerAdapter != null) {
                        headerAdapter.clear()
                        headerAdapter.addAllToEnd(headers)
                        add(headerAdapter)
                    }
                    if (!items.isNullOrEmpty()) {
                        itemAdapter.clear()
                        if (isAfter) {
                            itemAdapter.addAllToEnd(items)
                            addAll(itemAdapter, loadMoreAdapter)
                        } else {
                            itemAdapter.addAllToStart(items)
                            addAll(loadMoreAdapter, itemAdapter)
                        }
                        loadMoreAdapter.reload()
                        loadMoreAdapter.onLoading()
                    }
                    if (isAfter) {
                        recyclerView.scrollToTop()
                    } else {
                        recyclerView.scrollToBottom()
                    }
                }
            }
            requestType is RequestType.After || requestType is RequestType.Before -> {
                val items = res?.getOrNull(1)
                if (items.isNullOrEmpty()) {
                    // 没有更多数据需要加载
                    loadMoreAdapter.onEnd()
                } else {
                    // 还有更多数据需要加载
                    if (isAfter) {
                        itemAdapter.addAllToEnd(items)
                    } else {
                        itemAdapter.addAllToStart(items)
                        recyclerView.keepPosition(items.size, 1)
                    }
                    loadMoreAdapter.reload()
                    loadMoreAdapter.onLoading()
                }
            }
        }
        onSuccess?.invoke(requestType, resultType)
    }
}

/**
 * 请求处理者。
 * 封装了初始化、刷新、往后加载更多、往前加载更多操作。
 * 请求并发处理规则：
 * 1、初始化、刷新：如果有操作正在执行，则取消正在执行的操作，执行新操作。
 * 2、往后加载更多、往前加载更多：如果有操作正在执行，则放弃新操作，否则执行新操作。
 */
class RequestHandler<ResultType> {
    private val mConcurrencyHelper = ConcurrencyHelper()

    /**
     * 初始化或者刷新开始时显示进度条
     */
    internal var show: (() -> Unit)? = null

    /**
     * 初始化或者刷新完成时隐藏进度条
     */
    internal var hide: (() -> Unit)? = null

    /**
     * 请求失败时回调
     */
    internal var onError: (suspend (RequestType, Throwable) -> Unit)? = null

    /**
     * 请求成功时回调
     */
    internal var onSuccess: (suspend (RequestType, ResultType) -> Unit)? = null

    private var pagingResult: PagingResult<ResultType>? = null
    private var flow: Flow<ResultType>? = null

    internal constructor(pagingResult: PagingResult<ResultType>) {
        this.pagingResult = pagingResult
    }

    internal constructor(flow: Flow<ResultType>) {
        this.flow = flow
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

    suspend fun after() {
        mConcurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.After)
            collect(RequestType.After, show, hide, onError, onSuccess)
        }
    }

    suspend fun before() {
        mConcurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.Before)
            collect(RequestType.Before, show, hide, onError, onSuccess)
        }
    }

    /**
     * 收集分页数据
     *
     * @param show              初始化或者刷新开始时显示进度条
     * @param hide              初始化或者刷新完成时隐藏进度条
     * @param onError           请求失败时回调。
     * @param onSuccess         请求成功时回调。
     */
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
                if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
                    show?.invoke()
                }
            }.onCompletion {
                if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
                    hide?.invoke()
                }
            }.catch {
                onError?.invoke(requestType, it)
            }.flowOn(Dispatchers.Main)
            .collect {
                onSuccess?.invoke(requestType, it)
            }
    }
}
