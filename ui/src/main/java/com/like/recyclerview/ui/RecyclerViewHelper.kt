package com.like.recyclerview.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.model.*
import com.like.repository.RequestState
import com.like.repository.RequestType
import com.like.repository.Result
import com.like.repository.StateReport

/**
 * 初始化不分页的 RecyclerView。
 * 包括空视图、错误视图、点击监听及Item数据的自动添加
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param listener          item点击监听
 */
fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerViewForNotPaging(
    lifecycleOwner: LifecycleOwner,
    adapter: BaseAdapter,
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    listener: OnItemClickListener? = null
) {
    RecyclerViewHelper.initRecyclerView(
        lifecycleOwner,
        adapter,
        this.liveState,
        this.liveValue,
        emptyItem,
        errorItem,
        null,
        null,
        listener
    )
}

/**
 * 初始化往后加载更多分页的 RecyclerView。
 * 包括空视图、错误视图、加载更多视图、点击监听及Item数据的自动添加
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param loadMoreFooter    加载更多的视图。默认为：[DefaultLoadMoreFooter]
 * @param listener          item点击监听
 */
fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerViewForLoadAfterPaging(
    lifecycleOwner: LifecycleOwner,
    adapter: BaseAdapter,
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    loadMoreFooter: ILoadMoreFooter? = DefaultLoadMoreFooter { this.retry() },
    listener: OnItemClickListener? = null
) {
    RecyclerViewHelper.initRecyclerView(
        lifecycleOwner,
        adapter,
        this.liveState,
        this.liveValue,
        emptyItem,
        errorItem,
        loadMoreFooter,
        null,
        listener
    )
}

/**
 * 初始化往前加载更多分页的 RecyclerView。
 * 包括空视图、错误视图、加载更多视图、点击监听及Item数据的自动添加
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param loadMoreHeader    加载更多的视图。默认为：[DefaultLoadMoreHeader]
 * @param listener          item点击监听
 */
fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerViewForLoadBeforePaging(
    lifecycleOwner: LifecycleOwner,
    adapter: BaseAdapter,
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    loadMoreHeader: ILoadMoreHeader? = DefaultLoadMoreHeader { this.retry() },
    listener: OnItemClickListener? = null
) {
    RecyclerViewHelper.initRecyclerView(
        lifecycleOwner,
        adapter,
        this.liveState,
        this.liveValue,
        emptyItem,
        errorItem,
        null,
        loadMoreHeader,
        listener
    )
}

/**
 * 和 [com.github.like5188:Repository] 库配合使用时，对 RecyclerView 的常用操作进行了封装，用于帮助快速创建包含 RecyclerView 的界面
 */
object RecyclerViewHelper {

    /**
     * 初始化 RecyclerView
     * 包括空视图、错误视图、加载更多视图、点击监听及Item数据的自动添加
     *
     * @param errorItem         失败时显示的视图。库中默认实现了[DefaultErrorItem]
     * @param emptyItem         数据为空时显示的视图。库中默认实现了[DefaultEmptyItem]
     * @param loadMoreFooter    加载更多的视图。库中默认实现了[DefaultLoadMoreFooter]
     * @param listener          item点击监听
     */
    fun <T : IRecyclerViewItem> initRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        liveState: LiveData<StateReport>,
        liveValue: LiveData<List<T>?>,
        emptyItem: IEmptyItem? = null,
        errorItem: IErrorItem? = null,
        loadMoreFooter: ILoadMoreFooter? = null,
        loadMoreHeader: ILoadMoreHeader? = null,
        listener: OnItemClickListener? = null
    ) {
        emptyItem?.let {
            addEmptyItemToRecyclerView(
                lifecycleOwner,
                adapter,
                liveState,
                liveValue,
                it
            )
        }
        errorItem?.let {
            addErrorItemToRecyclerView(
                lifecycleOwner,
                adapter,
                liveState,
                it
            )
        }
        addItemToRecyclerView(
            lifecycleOwner,
            adapter,
            liveState,
            liveValue
        )
        loadMoreFooter?.let {
            addLoadMoreFooterToRecyclerView(
                lifecycleOwner,
                adapter,
                liveState,
                liveValue,
                loadMoreFooter
            )
        }
        loadMoreHeader?.let {
            addLoadMoreHeaderToRecyclerView(
                lifecycleOwner,
                adapter,
                liveState,
                liveValue,
                loadMoreHeader
            )
        }
        listener?.let {
            addOnItemClickListenerToRecyclerView(
                lifecycleOwner,
                adapter,
                liveState,
                liveValue,
                it
            )
        }
    }

    private fun <T : IRecyclerViewItem> addEmptyItemToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        liveState: LiveData<StateReport>,
        liveValue: LiveData<List<T>?>,
        emptyItem: IEmptyItem
    ) {
        liveValue.observe(lifecycleOwner) {
            val stateReport = liveState.value
            when {
                (stateReport?.type is RequestType.Initial || stateReport?.type is RequestType.Refresh) && stateReport.state is RequestState.Success -> {
                    if (it.isNullOrEmpty()) {
                        adapter.mAdapterDataManager.setEmptyItem(emptyItem)
                    }
                }
            }
        }
    }

    private fun addErrorItemToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        liveState: LiveData<StateReport>,
        errorItem: IErrorItem
    ) {
        liveState.observe(lifecycleOwner) {
            when {
                it.type is RequestType.Initial && it.state is RequestState.Failed -> {
                    if (errorItem.errorMessage.isEmpty()) {
                        errorItem.errorMessage =
                            (it.state as RequestState.Failed).throwable.message ?: "unknown error"
                    }
                    adapter.mAdapterDataManager.setErrorItem(errorItem)
                }
            }
        }
    }

    private fun <T : IRecyclerViewItem> addItemToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        liveState: LiveData<StateReport>,
        liveValue: LiveData<List<T>?>
    ) {
        liveValue.observe(lifecycleOwner) {
            val stateReport = liveState.value
            when {
                (stateReport?.type is RequestType.Initial || stateReport?.type is RequestType.Refresh) && stateReport.state is RequestState.Success -> {
                    if (!it.isNullOrEmpty()) {
                        adapter.mAdapterDataManager.clearAndAddAll(it.map())
                    }
                }
            }
        }
    }

    private fun <T : IRecyclerViewItem> addLoadMoreFooterToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        liveState: LiveData<StateReport>,
        liveValue: LiveData<List<T>?>,
        loadMoreFooter: ILoadMoreFooter
    ) {
        liveState.observe(lifecycleOwner) {
            when {
                it.type is RequestType.After && it.state is RequestState.Failed -> {
                    adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                        if (iFooter is ILoadMoreFooter)
                            iFooter.onError()
                    }
                }
            }
        }
        liveValue.observe(lifecycleOwner) {
            val stateReport = liveState.value
            when {
                (stateReport?.type is RequestType.Initial || stateReport?.type is RequestType.Refresh) && stateReport.state is RequestState.Success -> {
                    if (!it.isNullOrEmpty()) {
                        adapter.mAdapterDataManager.addFooterToEnd(loadMoreFooter)
                    }
                }
                stateReport?.type is RequestType.After && stateReport.state is RequestState.Success -> {
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

    private fun <T : IRecyclerViewItem> addLoadMoreHeaderToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        liveState: LiveData<StateReport>,
        liveValue: LiveData<List<T>?>,
        loadMoreHeader: ILoadMoreHeader
    ) {
        liveState.observe(lifecycleOwner) {
            when {
                it.type is RequestType.Before && it.state is RequestState.Failed -> {
                    adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                        if (iHeader is ILoadMoreHeader)
                            iHeader.onError()
                    }
                }
            }
        }
        liveValue.observe(lifecycleOwner) {
            val stateReport = liveState.value
            when {
                (stateReport?.type is RequestType.Initial || stateReport?.type is RequestType.Refresh) && stateReport.state is RequestState.Success -> {
                    if (!it.isNullOrEmpty()) {
                        adapter.mAdapterDataManager.addHeaderToStart(loadMoreHeader)
                    }
                }
                stateReport?.type is RequestType.Before && stateReport.state is RequestState.Success -> {
                    // 因为 header 的状态和数据有关，所以放到 liveValue 的监听里面来。
                    if (it.isNullOrEmpty()) {
                        // 到顶了
                        adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                            if (iHeader is ILoadMoreHeader)
                                iHeader.onEnd()
                        }
                    } else {
                        adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                            if (iHeader is ILoadMoreHeader)
                                iHeader.onLoading()
                        }

                        adapter.mAdapterDataManager.addItemsToStart(it.map())
                    }
                }
            }
        }
    }

    private fun <T : IRecyclerViewItem> addOnItemClickListenerToRecyclerView(
        lifecycleOwner: LifecycleOwner,
        adapter: BaseAdapter,
        liveState: LiveData<StateReport>,
        liveValue: LiveData<List<T>?>,
        listener: OnItemClickListener
    ) {
        liveState.observe(lifecycleOwner) {
            when {
                it.type is RequestType.Initial && it.state is RequestState.Failed -> {
                    adapter.removeOnItemClickListener(listener)
                }
            }
        }
        liveValue.observe(lifecycleOwner) {
            val stateReport = liveState.value
            when {
                (stateReport?.type is RequestType.Initial || stateReport?.type is RequestType.Refresh) && stateReport.state is RequestState.Success -> {
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