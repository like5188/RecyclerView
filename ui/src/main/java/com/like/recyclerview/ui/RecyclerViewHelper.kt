package com.like.recyclerview.ui

import com.like.datasource.Result
import com.like.datasource.util.LoadState
import com.like.datasource.util.LoadType
import com.like.datasource.util.StateReport
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.model.*
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * 初始化不分页的 RecyclerView。
 * 包括空视图、错误视图、加载更多视图、Item的添加及点击监听
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param listener          item点击监听
 */
suspend fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerViewForNotPaging(
    adapter: BaseAdapter,
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    listener: OnItemClickListener? = null
) {
    RecyclerViewHelper.initRecyclerView(
        adapter,
        this@bindRecyclerViewForNotPaging.stateReportFlow,
        this@bindRecyclerViewForNotPaging.dataFlow,
        emptyItem,
        errorItem,
        null,
        null,
        listener
    )
}

/**
 * 初始化往后加载更多分页的 RecyclerView。
 * 包括空视图、错误视图、加载更多视图、Item的添加及点击监听
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param loadMoreFooter    加载更多的视图。默认为：[DefaultLoadMoreFooter]
 * @param listener          item点击监听
 */
suspend fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerViewForLoadAfterPaging(
    adapter: BaseAdapter,
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    loadMoreFooter: ILoadMoreFooter? = null,
    listener: OnItemClickListener? = null
) = coroutineScope {
    RecyclerViewHelper.initRecyclerView(
        adapter,
        this@bindRecyclerViewForLoadAfterPaging.stateReportFlow,
        this@bindRecyclerViewForLoadAfterPaging.dataFlow,
        emptyItem,
        errorItem,
        loadMoreFooter ?: DefaultLoadMoreFooter {
            this.launch {
                this@bindRecyclerViewForLoadAfterPaging.retry()
            }
        },
        null,
        listener
    )
}

/**
 * 初始化往前加载更多分页的 RecyclerView。
 * 包括空视图、错误视图、加载更多视图、Item的添加及点击监听
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param loadMoreHeader    加载更多的视图。默认为：[DefaultLoadMoreHeader]
 * @param listener          item点击监听
 */
suspend fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerViewForLoadBeforePaging(
    adapter: BaseAdapter,
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    loadMoreHeader: ILoadMoreHeader? = null,
    listener: OnItemClickListener? = null
) = coroutineScope {
    RecyclerViewHelper.initRecyclerView(
        adapter,
        this@bindRecyclerViewForLoadBeforePaging.stateReportFlow,
        this@bindRecyclerViewForLoadBeforePaging.dataFlow,
        emptyItem,
        errorItem,
        null,
        loadMoreHeader ?: DefaultLoadMoreHeader {
            this.launch {
                this@bindRecyclerViewForLoadBeforePaging.retry()
            }
        },
        listener
    )
}

/**
 * 和 [com.github.like5188:DataSource] 库配合使用时，对 RecyclerView 的常用操作进行了封装，用于帮助快速创建包含 RecyclerView 的界面
 */
object RecyclerViewHelper {

    /**
     * 初始化 RecyclerView
     * 包括空视图、错误视图、加载更多视图、Item的添加及点击监听
     *
     * @param errorItem         失败时显示的视图。库中默认实现了[DefaultErrorItem]
     * @param emptyItem         数据为空时显示的视图。库中默认实现了[DefaultEmptyItem]
     * @param loadMoreFooter    加载更多的视图。库中默认实现了[DefaultLoadMoreFooter]
     * @param listener          item点击监听
     */
    internal suspend fun <T : IRecyclerViewItem> initRecyclerView(
        adapter: BaseAdapter,
        stateReportFlow: Flow<StateReport>,
        dataFlow: Flow<List<T>?>,
        emptyItem: IEmptyItem? = null,
        errorItem: IErrorItem? = null,
        loadMoreFooter: ILoadMoreFooter? = null,
        loadMoreHeader: ILoadMoreHeader? = null,
        listener: OnItemClickListener? = null
    ) {
        emptyItem?.let {
            addEmptyItemToRecyclerView(
                adapter,
                stateReportFlow,
                dataFlow,
                it
            )
        }
        errorItem?.let {
            addErrorItemToRecyclerView(
                adapter,
                stateReportFlow,
                it
            )
        }
        addItemToRecyclerView(
            adapter,
            stateReportFlow,
            dataFlow
        )
        loadMoreFooter?.let {
            addLoadMoreFooterToRecyclerView(
                adapter,
                stateReportFlow,
                dataFlow,
                loadMoreFooter
            )
        }
        loadMoreHeader?.let {
            addLoadMoreHeaderToRecyclerView(
                adapter,
                stateReportFlow,
                dataFlow,
                loadMoreHeader
            )
        }
        listener?.let {
            addOnItemClickListenerToRecyclerView(
                adapter,
                stateReportFlow,
                dataFlow,
                it
            )
        }
    }

    private suspend fun <T : IRecyclerViewItem> addEmptyItemToRecyclerView(
        adapter: BaseAdapter,
        stateReportFlow: Flow<StateReport>,
        dataFlow: Flow<List<T>?>,
        emptyItem: IEmptyItem
    ) = coroutineScope {
        dataFlow.collectLatest {
            val stateReport = stateReportFlow.firstOrNull()
            when {
                (stateReport?.loadType == LoadType.INITIAL || stateReport?.loadType == LoadType.REFRESH) && stateReport.loadState is LoadState.Success -> {
                    if (it.isNullOrEmpty()) {
                        adapter.mAdapterDataManager.setEmptyItem(emptyItem)
                    }
                }
            }
        }
    }

    private suspend fun addErrorItemToRecyclerView(
        adapter: BaseAdapter,
        stateReportFlow: Flow<StateReport>,
        errorItem: IErrorItem
    ) = coroutineScope {
        stateReportFlow.collectLatest {
            when {
                it.loadType == LoadType.INITIAL && it.loadState is LoadState.Error -> {
                    if (errorItem.errorMessage.isEmpty()) {
                        errorItem.errorMessage = (it.loadState as LoadState.Error).throwable.message ?: "unknown error"
                    }
                    adapter.mAdapterDataManager.setErrorItem(errorItem)
                }
            }
        }
    }

    private suspend fun <T : IRecyclerViewItem> addItemToRecyclerView(
        adapter: BaseAdapter,
        stateReportFlow: Flow<StateReport>,
        dataFlow: Flow<List<T>?>,
    ) = coroutineScope {
        dataFlow.collectLatest {
            val stateReport = stateReportFlow.firstOrNull()
            when {
                (stateReport?.loadType == LoadType.INITIAL || stateReport?.loadType == LoadType.REFRESH) && stateReport.loadState is LoadState.Success -> {
                    if (!it.isNullOrEmpty()) {
                        adapter.mAdapterDataManager.clearAndAddAll(it.map())
                    }
                }
            }
        }
    }

    private suspend fun <T : IRecyclerViewItem> addLoadMoreFooterToRecyclerView(
        adapter: BaseAdapter,
        stateReportFlow: Flow<StateReport>,
        dataFlow: Flow<List<T>?>,
        loadMoreFooter: ILoadMoreFooter
    ) = coroutineScope {
        stateReportFlow.collectLatest {
            when {
                it.loadType == LoadType.APPEND && it.loadState is LoadState.Error -> {
                    adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                        if (iFooter is ILoadMoreFooter) {
                            iFooter.onError()
                        }
                    }
                }
            }
        }
        dataFlow.collectLatest {
            val stateReport = stateReportFlow.firstOrNull()
            when {
                (stateReport?.loadType == LoadType.INITIAL || stateReport?.loadType == LoadType.REFRESH) && stateReport.loadState is LoadState.Success -> {
                    if (!it.isNullOrEmpty()) {
                        adapter.mAdapterDataManager.addFooterToEnd(loadMoreFooter)
                    }
                }
                stateReport?.loadType == LoadType.APPEND && stateReport.loadState is LoadState.Success -> {
                    // 因为 footer 的状态和数据有关，所以放到 liveValue 的监听里面来。
                    if (it.isNullOrEmpty()) {
                        // 到底了
                        adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                            if (iFooter is ILoadMoreFooter)
                                iFooter.onEnd()
                        }
                    } else {
                        adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                            if (iFooter is ILoadMoreFooter)
                                iFooter.onLoading()
                        }

                        adapter.mAdapterDataManager.addItemsToEnd(it.map())
                    }
                }
            }
        }
    }

    private suspend fun <T : IRecyclerViewItem> addLoadMoreHeaderToRecyclerView(
        adapter: BaseAdapter,
        stateReportFlow: Flow<StateReport>,
        dataFlow: Flow<List<T>?>,
        loadMoreHeader: ILoadMoreHeader
    ) = coroutineScope {
        stateReportFlow.collectLatest {
            when {
                it.loadType == LoadType.PREPEND && it.loadState is LoadState.Error -> {
                    adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                        if (iHeader is ILoadMoreHeader) {
                            iHeader.onError()
                        }
                    }
                }
            }
        }
        dataFlow.collectLatest {
            val stateReport = stateReportFlow.firstOrNull()
            when {
                (stateReport?.loadType == LoadType.INITIAL || stateReport?.loadType == LoadType.REFRESH) && stateReport.loadState is LoadState.Success -> {
                    if (!it.isNullOrEmpty()) {
                        adapter.mAdapterDataManager.addHeaderToStart(loadMoreHeader)
                    }
                }
                stateReport?.loadType == LoadType.PREPEND && stateReport.loadState is LoadState.Success -> {
                    // 因为 header 的状态和数据有关，所以放到 dataFlow 的监听里面来。
                    if (it.isNullOrEmpty()) {
                        // 到顶了
                        adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                            if (iHeader is ILoadMoreHeader) {
                                iHeader.onEnd()
                            }
                        }
                    } else {
                        adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                            if (iHeader is ILoadMoreHeader) {
                                iHeader.onLoading()
                            }
                        }

                        adapter.mAdapterDataManager.addItemsToStart(it.map())
                    }
                }
            }
        }
    }

    private suspend fun <T : IRecyclerViewItem> addOnItemClickListenerToRecyclerView(
        adapter: BaseAdapter,
        stateReportFlow: Flow<StateReport>,
        dataFlow: Flow<List<T>?>,
        listener: OnItemClickListener
    ) = coroutineScope {
        stateReportFlow.collectLatest {
            when {
                it.loadType == LoadType.INITIAL && it.loadState is LoadState.Error -> {
                    adapter.removeOnItemClickListener(listener)
                }
            }
        }
        dataFlow.collectLatest {
            val stateReport = stateReportFlow.firstOrNull()
            when {
                (stateReport?.loadType == LoadType.INITIAL || stateReport?.loadType == LoadType.REFRESH) && stateReport.loadState is LoadState.Success -> {
                    if (it.isNullOrEmpty()) {
                        adapter.removeOnItemClickListener(listener)
                    } else {
                        adapter.addOnItemClickListener(listener)
                    }
                }
            }
        }
    }

    private inline fun <T, reified V> List<T>?.map(): List<V> {
        val result = mutableListOf<V>()
        this?.forEach {
            if (it is V) {
                result.add(it)
            }
        }
        return result
    }

}