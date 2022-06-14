package com.like.recyclerview.utils

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.like.common.util.Logger
import com.like.paging.PagingResult
import com.like.paging.RequestType
import com.like.recyclerview.adapter.BaseAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * 收集不分页数据[Flow]并显示到 RecyclerView（线程安全）
 *
 * @param dataFlow          获取数据的[Flow]
 * @param concatAdapter     合并的 adapter
 * @param headerAdapter     header 的 adapter
 * @param itemAdapter       列表的 adapter
 * @param transformer       在这里进行数据转换，返回值为一个集合，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
 * @param show              请求开始时显示进度条
 * @param hide              请求完成时隐藏进度条
 * @param onError           请求失败时回调。
 * @param onSuccess         请求成功时回调
 */
fun <ResultType, ValueInList> RecyclerView.bindFlow(
    dataFlow: Flow<ResultType>,
    concatAdapter: ConcatAdapter,
    headerAdapter: BaseAdapter<*, ValueInList>? = null,
    itemAdapter: BaseAdapter<*, ValueInList>,
    transformer: suspend (ResultType) -> List<List<ValueInList>?>? = { resultType ->
        @Suppress("UNCHECKED_CAST")
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
    onError: (suspend (RequestType, Throwable, RequestHandler<ResultType>) -> Unit)? = null,
    onSuccess: (suspend (RequestType, ResultType, RequestHandler<ResultType>) -> Unit)? = null,
): RequestHandler<ResultType> = RequestHandler(dataFlow).apply {
    this.show = show
    this.hide = hide
    this.onError = { requestType, throwable ->
        onError?.invoke(requestType, throwable, this)
    }
    this.onSuccess = { requestType, resultType ->
        val res = transformer(resultType)
        concatAdapter.clear()
        if (!res.isNullOrEmpty()) {
            val headers = res.getOrNull(0)
            val items = res.getOrNull(1)
            if (!headers.isNullOrEmpty() && headerAdapter != null) {
                headerAdapter.clear()
                headerAdapter.addAllToEnd(headers)
                concatAdapter.add(headerAdapter)
            }
            if (!items.isNullOrEmpty()) {
                itemAdapter.clear()
                itemAdapter.addAllToEnd(items)
                concatAdapter.add(itemAdapter)
            }
            scrollToTop()
        }
        onSuccess?.invoke(requestType, resultType, this)
    }
}

///**
// * 绑定往后分页数据并显示到 RecyclerView
// */
//fun <ResultType, ValueInList> RecyclerView.bindAfterPagingResult(
//    pagingResult: PagingResult<ResultType>,
//    concatAdapter: ConcatAdapter,
//    headerAdapter: BaseAdapter<*, ValueInList>? = null,
//    itemAdapter: BaseAdapter<*, ValueInList>,
//    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
//    transformer: suspend (RequestType, ResultType) -> List<List<ValueInList>?>? = { requestType, resultType ->
//        @Suppress("UNCHECKED_CAST")
//        if (headerAdapter == null) {// 如果返回值[ResultType]为 List<ValueInList>? 类型
//            if (resultType is List<*>? && !resultType.isNullOrEmpty()) {
//                listOf(emptyList(), resultType as? List<ValueInList>)
//            } else {
//                emptyList()
//            }
//        } else {// 如果返回值[ResultType]为 List<List<ValueInList>?>? 类型
//            resultType as? List<List<ValueInList>?>
//        }
//    },
//    show: (() -> Unit)? = null,
//    hide: (() -> Unit)? = null,
//    onError: (suspend (RequestType, Throwable, RequestHandler<ResultType>) -> Unit)? = null,
//    onSuccess: (suspend (RequestType, ResultType, RequestHandler<ResultType>) -> Unit)? = null,
//): RequestHandler<ResultType> = bindPagingResult(
//    true, pagingResult, concatAdapter, headerAdapter, itemAdapter, loadMoreAdapter,
//    transformer, show, hide, onError, onSuccess
//).apply {
//    loadMoreAdapter.onLoadMore = ::after
//}
//
///**
// * 绑定往前分页数据并显示到 RecyclerView
// */
//fun <ResultType, ValueInList> RecyclerView.bindBeforePagingResult(
//    pagingResult: PagingResult<ResultType>,
//    concatAdapter: ConcatAdapter,
//    itemAdapter: BaseAdapter<*, ValueInList>,
//    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
//    transformer: suspend (RequestType, ResultType) -> List<ValueInList>? = { requestType, resultType ->
//        @Suppress("UNCHECKED_CAST")
//        resultType as? List<ValueInList>
//    },
//    show: (() -> Unit)? = null,
//    hide: (() -> Unit)? = null,
//    onError: (suspend (RequestType, Throwable, RequestHandler<ResultType>) -> Unit)? = null,
//    onSuccess: (suspend (RequestType, ResultType, RequestHandler<ResultType>) -> Unit)? = null,
//): RequestHandler<ResultType> = bindPagingResult(
//    false, pagingResult, concatAdapter, null, itemAdapter, loadMoreAdapter,
//    transformer = { requestType, resultType ->
//        if (resultType is List<*>? && !resultType.isNullOrEmpty()) {
//            listOf(emptyList(), transformer(requestType, resultType))
//        } else {
//            emptyList()
//        }
//    },
//    show, hide, onError, onSuccess
//).apply {
//    loadMoreAdapter.onLoadMore = ::before
//}
//
///**
// * 收集分页数据[PagingResult]并显示到 RecyclerView（线程安全）
// *
// * @param isAfter           是否是往后加载更多。true：往后加载更多；false：往前加载更多；
// * @param pagingResult      使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
// * @param concatAdapter     合并的 adapter
// * @param headerAdapter     header 的 adapter
// * @param itemAdapter       列表的 adapter
// * @param loadMoreAdapter   加载更多视图 的 adapter
// * @param transformer       在这里进行数据转换，返回值为一个集合，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
// * @param show              初始化或者刷新开始时显示进度条
// * @param hide              初始化或者刷新完成时隐藏进度条
// * @param onError           请求失败时回调。
// * 在这里进行额外错误处理：
// * 加载更多失败时，更新了[loadMoreAdapter]。
// * @param onSuccess         请求成功时回调。
// */
//private fun <ResultType, ValueInList> RecyclerView.bindPagingResult(
//    isAfter: Boolean,
//    pagingResult: PagingResult<ResultType>,
//    concatAdapter: ConcatAdapter,
//    headerAdapter: BaseAdapter<*, ValueInList>? = null,
//    itemAdapter: BaseAdapter<*, ValueInList>,
//    loadMoreAdapter: BaseLoadMoreAdapter<*, *>,
//    transformer: suspend (RequestType, ResultType) -> List<List<ValueInList>?>?,
//    show: (() -> Unit)? = null,
//    hide: (() -> Unit)? = null,
//    onError: (suspend (RequestType, Throwable, RequestHandler<ResultType>) -> Unit)? = null,
//    onSuccess: (suspend (RequestType, ResultType, RequestHandler<ResultType>) -> Unit)? = null,
//): RequestHandler<ResultType> = RequestHandler(pagingResult).apply {
//    this.show = show
//    this.hide = hide
//    this.onError = { requestType, throwable ->
//        if (requestType is RequestType.After || requestType is RequestType.Before) {
//            // 加载更多失败时，直接更新[loadMoreAdapter]
//            loadMoreAdapter.error(throwable)
//        }
//        onError?.invoke(requestType, throwable, this)
//    }
//    this.onSuccess = { requestType, resultType ->
//        val res = transformer(requestType, resultType)
//        when (requestType) {
//            is RequestType.Initial, is RequestType.Refresh -> {
//                concatAdapter.clear()
//                if (!res.isNullOrEmpty()) {
//                    val headers = res.getOrNull(0)
//                    val items = res.getOrNull(1)
//                    // 往后加载更多时，才添加 header
//                    if (isAfter && !headers.isNullOrEmpty() && headerAdapter != null) {
//                        headerAdapter.clear()
//                        headerAdapter.addAllToEnd(headers)
//                        concatAdapter.add(headerAdapter)
//                    }
//                    if (!items.isNullOrEmpty()) {
//                        itemAdapter.clear()
//                        if (isAfter) {
//                            itemAdapter.addAllToEnd(items)
//                            concatAdapter.addAll(itemAdapter, loadMoreAdapter)
//                        } else {
//                            itemAdapter.addAllToStart(items)
//                            concatAdapter.addAll(loadMoreAdapter, itemAdapter)
//                        }
//                    }
//                    if (isAfter) {
//                        scrollToTop()
//                    } else {
//                        scrollToBottom()
//                    }
//                    if (!items.isNullOrEmpty()) {
//                        loadMoreAdapter.hasMore()
//                    }
//                }
//            }
//            is RequestType.After, is RequestType.Before -> {
//                val items = res?.getOrNull(1)
//                if (items.isNullOrEmpty()) {
//                    // 没有更多数据需要加载
//                    loadMoreAdapter.end()
//                } else {
//                    // 还有更多数据需要加载
//                    if (isAfter) {
//                        itemAdapter.addAllToEnd(items)
//                    } else {
//                        itemAdapter.addAllToStart(items)
//                        keepPosition(items.size, 1)
//                    }
//                    loadMoreAdapter.hasMore()
//                }
//            }
//        }
//        onSuccess?.invoke(requestType, resultType, this)
//    }
//}

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
                Logger.d("RecyclerView requestType=$requestType")
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

/**
 * 滚动到最顶部
 */
fun RecyclerView.scrollToTop() {
    scrollToPosition(0)
}

/**
 * 滚动到最底部
 */
fun RecyclerView.scrollToBottom() {
    val adapter = this.adapter ?: return
    scrollToPosition(adapter.itemCount - 1)
}

/**
 * 保持位置（类似于聊天界面的处理，实际上就是使得往前加载更多和往后加载更多的效果一致）
 */
fun RecyclerView.keepPosition(insertedItemCount: Int, headerCount: Int) {
    // 做类似于聊天界面的处理
    val layoutManager = this.layoutManager
    if (layoutManager is LinearLayoutManager) {
        // 第一个item的视图
        layoutManager.getChildAt(headerCount)?.let {
            val offset = it.top
            val position = layoutManager.getPosition(it)
            layoutManager.scrollToPositionWithOffset(insertedItemCount + position, offset)
        }
    }
}

fun RecyclerView.findFirstVisibleItemPosition(): Int {
    return when (val lm = layoutManager) {
        is LinearLayoutManager -> lm.findFirstVisibleItemPosition()
        is StaggeredGridLayoutManager -> {
            val intArray = IntArray(2)
            lm.findFirstVisibleItemPositions(intArray)
            Math.min(intArray[0], intArray[1])
        }
        else -> RecyclerView.NO_POSITION
    }
}

fun RecyclerView.findLastVisibleItemPosition(): Int {
    return when (val lm = layoutManager) {
        is LinearLayoutManager -> lm.findLastVisibleItemPosition()
        is StaggeredGridLayoutManager -> {
            val intArray = IntArray(2)
            lm.findLastVisibleItemPositions(intArray)
            Math.max(intArray[0], intArray[1])
        }
        else -> RecyclerView.NO_POSITION
    }
}
