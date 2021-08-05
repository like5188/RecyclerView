package com.like.recyclerview.utils

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.RequestType
import com.like.paging.Result
import com.like.paging.util.bind
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.adapter.AbstractErrorAdapter
import com.like.recyclerview.adapter.AbstractLoadMoreAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 辅助搜集分页或者不分页数据，并绑定到界面。
 */
class UIHelper(private val mAdapter: ConcatAdapter) {

    /**
     * 不分页
     *
     * @param result            获取列表数据的代码块
     * @param listAdapter       列表
     * @param emptyAdapter      空视图
     * @param errorAdapter      错误视图
     * @param show              显示进度条
     * @param hide              隐藏进度条
     */
    suspend fun <ValueInList> collect(
        result: (suspend () -> List<ValueInList>?),
        listAdapter: AbstractAdapter<*, ValueInList>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) {
        result.bind(
            onData = {
                if (it.isNullOrEmpty()) {
                    mAdapter.clear()
                    mAdapter.add(emptyAdapter)
                } else {
                    mAdapter.clear()
                    mAdapter.add(listAdapter)
                    listAdapter.clear()
                    listAdapter.addAllToEnd(it)
                }
            },
            onError = {
                mAdapter.clear()
                mAdapter.add(errorAdapter)
                errorAdapter?.onError(it)
            },
            show = show,
            hide = hide,
        )
    }

    /**
     * 不分页
     *
     * @param result            获取数据的代码块
     * @param onData            用于处理初始化或者刷新数据的回调。
     * 返回值表示是否显示空视图。
     * @param contentAdapter    内容，可以包括列表、header等。
     * @param emptyAdapter      空视图
     * @param errorAdapter      错误视图
     * @param show              显示进度条
     * @param hide              隐藏进度条
     */
    suspend fun <ResultType> collect(
        result: (suspend () -> ResultType),
        onData: suspend (ResultType) -> Boolean,
        contentAdapter: RecyclerView.Adapter<*>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) {
        result.bind(
            onData = {
                if (onData(it)) {
                    mAdapter.clear()
                    mAdapter.add(emptyAdapter)
                } else {
                    mAdapter.clear()
                    mAdapter.add(contentAdapter)
                }
            },
            onError = {
                mAdapter.clear()
                mAdapter.add(errorAdapter)
                errorAdapter?.onError(it)
            },
            show = show,
            hide = hide,
        )
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
     */
    suspend fun <ValueInList> collectForLoadAfter(
        recyclerView: RecyclerView,
        result: Result<List<ValueInList>?>,
        listAdapter: AbstractAdapter<*, ValueInList>,
        loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) = withContext(Dispatchers.Main) {
        val flow = result.bind(
            onData = { requestType, data ->
                when {
                    requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                        if (data.isNullOrEmpty()) {
                            mAdapter.clear()
                            mAdapter.add(emptyAdapter)
                        } else {
                            mAdapter.clear()
                            mAdapter.addAll(listAdapter, loadMoreAdapter)
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
            },
            onError = { requestType, throwable ->
                when {
                    requestType is RequestType.Initial -> {
                        mAdapter.clear()
                        mAdapter.add(errorAdapter)
                        errorAdapter?.onError(throwable)
                    }
                    requestType is RequestType.After || requestType is RequestType.Before -> {
                        loadMoreAdapter.onError(throwable)
                    }
                }
            },
            show = show,
            hide = hide,
        )
        launch {
            flow.collect()
        }
        launch {
            result.initial()
        }
    }

    /**
     * 往后分页
     *
     * @param result            使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
     * @param onData            用于处理初始化或者刷新数据的回调。
     * 返回值：0：显示空视图；1：不显示空视图，没有更多数据需要加载；2：不显示空视图，有更多数据需要加载；
     * @param onLoadMore        用于处理加载更多数据的回调。
     * 返回值表示是否还有更多数据需要加载。
     * @param contentAdapter    内容，可以包括列表、header等。
     * @param loadMoreAdapter   加载更多视图
     * @param emptyAdapter      空视图
     * @param errorAdapter      错误视图
     * @param show              初始化或者刷新开始时显示进度条
     * @param hide              初始化或者刷新成功或者失败时隐藏进度条
     */
    suspend fun <ResultType> collectForLoadAfter(
        recyclerView: RecyclerView,
        result: Result<ResultType>,
        onData: (ResultType) -> Int,
        onLoadMore: (ResultType) -> Boolean,
        contentAdapter: RecyclerView.Adapter<*>,
        loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) = withContext(Dispatchers.Main) {
        val flow = result.bind(
            onData = { requestType, data ->
                when {
                    requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                        when (onData(data)) {
                            0 -> {// 显示空视图
                                mAdapter.clear()
                                mAdapter.add(emptyAdapter)
                            }
                            1 -> {// 不显示空视图，没有更多数据需要加载
                                mAdapter.clear()
                                mAdapter.add(contentAdapter)
                                recyclerView.scrollToTop()
                            }
                            2 -> {// 不显示空视图，有更多数据需要加载
                                mAdapter.clear()
                                mAdapter.add(contentAdapter)
                                mAdapter.add(loadMoreAdapter)
                                loadMoreAdapter.reload()
                                loadMoreAdapter.onComplete()
                                recyclerView.scrollToTop()
                            }
                        }
                    }
                    requestType is RequestType.After || requestType is RequestType.Before -> {
                        if (onLoadMore(data)) {
                            loadMoreAdapter.onEnd()
                        } else {
                            loadMoreAdapter.reload()
                            loadMoreAdapter.onComplete()
                        }
                    }
                }
            },
            onError = { requestType, throwable ->
                when {
                    requestType is RequestType.Initial -> {
                        mAdapter.clear()
                        mAdapter.add(errorAdapter)
                        errorAdapter?.onError(throwable)
                    }
                    requestType is RequestType.After || requestType is RequestType.Before -> {
                        loadMoreAdapter.onError(throwable)
                    }
                }
            },
            show = show,
            hide = hide,
        )
        launch {
            flow.collect()
        }
        launch {
            result.initial()
        }
    }

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
     */
    suspend fun <ValueInList> collectForLoadBefore(
        recyclerView: RecyclerView,
        result: Result<List<ValueInList>?>,
        listAdapter: AbstractAdapter<*, ValueInList>,
        loadMoreAdapter: AbstractLoadMoreAdapter<*, *>,
        emptyAdapter: AbstractAdapter<*, *>? = null,
        errorAdapter: AbstractErrorAdapter<*, *>? = null,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
    ) = withContext(Dispatchers.Main) {
        val flow = result.bind(
            onData = { requestType, data ->
                when {
                    requestType is RequestType.Initial || requestType is RequestType.Refresh -> {
                        if (data.isNullOrEmpty()) {
                            mAdapter.clear()
                            mAdapter.add(emptyAdapter)
                        } else {
                            mAdapter.clear()
                            mAdapter.addAll(loadMoreAdapter, listAdapter)
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
            },
            onError = { requestType, throwable ->
                when {
                    requestType is RequestType.Initial -> {
                        mAdapter.clear()
                        mAdapter.add(errorAdapter)
                        errorAdapter?.onError(throwable)
                    }
                    requestType is RequestType.After || requestType is RequestType.Before -> {
                        loadMoreAdapter.onError(throwable)
                    }
                }
            },
            show = show,
            hide = hide,
        )
        launch {
            flow.collect()
        }
        launch {
            result.initial()
        }
    }

}
