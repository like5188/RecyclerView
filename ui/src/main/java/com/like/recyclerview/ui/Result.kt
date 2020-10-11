package com.like.recyclerview.ui

import androidx.lifecycle.LifecycleOwner
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.model.*
import com.like.repository.Result

/**
 * 把 [Result] 与 [androidx.recyclerview.widget.RecyclerView] 进行绑定，不分页
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
    bindRecyclerView(
        lifecycleOwner,
        adapter,
        emptyItem,
        errorItem,
        null,
        null,
        listener
    )
}

/**
 * 把 [Result] 与 [androidx.recyclerview.widget.RecyclerView] 进行绑定，往后加载更多分页
 * 包括空视图、错误视图、往后加载更多视图、点击监听及Item数据的自动添加
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param loadMoreFooter    往后加载更多的视图。默认为：[DefaultLoadMoreFooter]
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
    bindRecyclerView(
        lifecycleOwner,
        adapter,
        emptyItem,
        errorItem,
        loadMoreFooter,
        null,
        listener
    )
}

/**
 * 把 [Result] 与 [androidx.recyclerview.widget.RecyclerView] 进行绑定，往前加载更多分页
 * 包括空视图、错误视图、往前加载更多视图、点击监听及Item数据的自动添加
 *
 * @param errorItem         失败时显示的视图。默认为：[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。默认为：[DefaultEmptyItem]
 * @param loadMoreHeader    往前加载更多的视图。默认为：[DefaultLoadMoreHeader]
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
    bindRecyclerView(
        lifecycleOwner,
        adapter,
        emptyItem,
        errorItem,
        null,
        loadMoreHeader,
        listener
    )
}

/**
 * 把 [Result] 与 [androidx.recyclerview.widget.RecyclerView] 进行绑定
 * 功能包括：Item数据的添加、空视图、错误视图、往后加载更多视图、往前加载更多视图、点击监听
 *
 * @param errorItem         失败时显示的视图。库中默认实现了[DefaultErrorItem]
 * @param emptyItem         数据为空时显示的视图。库中默认实现了[DefaultEmptyItem]
 * @param loadMoreFooter    往后加载更多的视图。库中默认实现了[DefaultLoadMoreFooter]
 * @param loadMoreHeader    往前加载更多的视图。库中默认实现了[DefaultLoadMoreHeader]
 * @param listener          item点击监听
 */
private fun <T : IRecyclerViewItem> Result<List<T>?>.bindRecyclerView(
    lifecycleOwner: LifecycleOwner,
    adapter: BaseAdapter,
    emptyItem: IEmptyItem? = null,
    errorItem: IErrorItem? = null,
    loadMoreFooter: ILoadMoreFooter? = null,
    loadMoreHeader: ILoadMoreHeader? = null,
    listener: OnItemClickListener? = null
) {
    val clearAndAddAll: (List<T>?) -> Unit = {
        adapter.mAdapterDataManager.clearAndAddAll(it)
    }
    val setEmptyItem: (() -> Unit)? = if (emptyItem != null) {
        { adapter.mAdapterDataManager.setEmptyItem(emptyItem) }
    } else {
        null
    }
    val setErrorItem: ((Throwable) -> Unit)? = if (errorItem != null) {
        {
            if (errorItem.errorMessage.isEmpty()) {
                errorItem.errorMessage = it.message ?: "unknown error"
            }
            adapter.mAdapterDataManager.setErrorItem(errorItem)
        }
    } else {
        null
    }
    val addFooterToEnd: (() -> Unit)? = if (loadMoreFooter != null) {
        {
            adapter.mAdapterDataManager.addFooterToEnd(loadMoreFooter)
        }
    } else {
        null
    }
    val onLoadingAfter: (() -> Unit)? = if (loadMoreFooter != null) {
        {
            adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                if (iFooter is ILoadMoreFooter) {
                    iFooter.onLoading()
                }
            }
        }
    } else {
        null
    }
    val onLoadAfterFailed: (() -> Unit)? = if (loadMoreFooter != null) {
        {
            adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                if (iFooter is ILoadMoreFooter) {
                    iFooter.onError()
                }
            }
        }
    } else {
        null
    }
    val onLoadAfterEnd: (() -> Unit)? = if (loadMoreFooter != null) {
        {
            adapter.mAdapterDataManager.getFooters().forEach { iFooter ->
                if (iFooter is ILoadMoreFooter) {
                    iFooter.onEnd()
                }
            }
        }
    } else {
        null
    }
    val addItemsToEnd: ((List<T>?) -> Unit)? = if (loadMoreFooter != null) {
        {
            adapter.mAdapterDataManager.addItemsToEnd(it.map())
        }
    } else {
        null
    }
    val addHeaderToStart: (() -> Unit)? = if (loadMoreHeader != null) {
        {
            adapter.mAdapterDataManager.addHeaderToStart(loadMoreHeader)
        }
    } else {
        null
    }
    val onLoadingBefore: (() -> Unit)? = if (loadMoreHeader != null) {
        {
            adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                if (iHeader is ILoadMoreHeader) {
                    iHeader.onLoading()
                }
            }
        }
    } else {
        null
    }
    val onLoadBeforeFailed: (() -> Unit)? = if (loadMoreHeader != null) {
        {
            adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                if (iHeader is ILoadMoreHeader) {
                    iHeader.onError()
                }
            }
        }
    } else {
        null
    }
    val onLoadBeforeEnd: (() -> Unit)? = if (loadMoreHeader != null) {
        {
            adapter.mAdapterDataManager.getHeaders().forEach { iHeader ->
                if (iHeader is ILoadMoreHeader) {
                    iHeader.onEnd()
                }
            }
        }
    } else {
        null
    }
    val addItemsToStart: ((List<T>?) -> Unit)? = if (loadMoreHeader != null) {
        {
            adapter.mAdapterDataManager.addItemsToStart(it.map())
        }
    } else {
        null
    }
    val removeOnItemClickListener = if (listener != null) {
        {
            adapter.removeOnItemClickListener(listener)
        }
    } else {
        null
    }
    val addOnItemClickListener = if (listener != null) {
        {
            adapter.addOnItemClickListener(listener)
        }
    } else {
        null
    }
    bindRecyclerView(
        lifecycleOwner,
        clearAndAddAll,
        setEmptyItem,
        setErrorItem,
        addFooterToEnd,
        onLoadingAfter,
        onLoadAfterFailed,
        onLoadAfterEnd,
        addItemsToEnd,
        addHeaderToStart,
        onLoadingBefore,
        onLoadBeforeFailed,
        onLoadBeforeEnd,
        addItemsToStart,
        removeOnItemClickListener,
        addOnItemClickListener
    )
}