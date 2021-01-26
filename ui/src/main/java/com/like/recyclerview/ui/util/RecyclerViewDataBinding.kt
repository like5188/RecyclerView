package com.like.recyclerview.ui.util

import com.like.paging.RequestType
import com.like.paging.Result
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.model.*
import com.like.recyclerview.ui.DefaultEmptyItem
import com.like.recyclerview.ui.DefaultErrorItem
import com.like.recyclerview.ui.DefaultLoadMoreFooter
import com.like.recyclerview.ui.DefaultLoadMoreHeader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
 * 使用此工具类进行[RecyclerView]的数据绑定。
 */
/**
 * 把返回结果为 List<IRecyclerViewItem> 的不分页数据源绑定到 [RecyclerView] 上。
 *
 * @param block         获取数据的函数，IO线程
 * @param adapter
 * @param isRefresh     是否刷新操作。true：刷新操作；false：初始化操作；
 * @param emptyItem     数据为空时显示的视图
 * @param errorItem     请求出错时显示的视图。只针对初始化出错时才显示，刷新出错时不显示。
 * @param show          初始化或者刷新开始时显示进度条，主线程
 * @param hide          初始化或者刷新成功或者失败时隐藏进度条，主线程
 * @param onFailed      失败回调，如果需要做其它错误处理，可以从这里获取。，主线程
 * @param onSuccess     成功回调，如果需要结果，可以从这里获取。，主线程
 */
suspend fun <ValueInList : IRecyclerViewItem> bindToRV(
    block: suspend () -> List<ValueInList>?,
    adapter: BaseAdapter,
    isRefresh: Boolean = false,
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onFailed: ((Throwable) -> Unit)? = null,
    onSuccess: ((List<ValueInList>?) -> Unit)? = null
) = withContext(Dispatchers.Main) {
    show?.invoke()
    try {
        val list = withContext(Dispatchers.IO) {
            block()
        }
        if (list.isNullOrEmpty()) {
            emptyItem?.let {
                adapter.mAdapterDataManager.setEmptyItem(emptyItem)
            }
        } else {
            adapter.mAdapterDataManager.clearAndAddAll(list)
        }
        onSuccess?.invoke(list)
    } catch (e: Exception) {
        if (!isRefresh) {
            errorItem?.let {
                errorItem.throwable = e
                adapter.mAdapterDataManager.setErrorItem(errorItem)
            }
        }
        onFailed?.invoke(e)
    } finally {
        hide?.invoke()
    }
}

/**
 * 把 [com.like.paging.Result] 提供的分页数据绑定到 [RecyclerView] 上。
 */
suspend fun <ValueInList : IRecyclerViewItem> Result<List<ValueInList>?>.collectAndBindToRV(
    adapter: BaseAdapter,
    isLoadAfter: Boolean = true,
    loadMoreFooter: ILoadMoreFooter? = if (isLoadAfter) {
        DefaultLoadMoreFooter { this.retry() }
    } else {
        null
    },
    loadMoreHeader: ILoadMoreHeader? = if (!isLoadAfter) {
        DefaultLoadMoreHeader { this.retry() }
    } else {
        null
    },
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onFailed: ((RequestType, Throwable) -> Unit)? = null,
    onSuccess: ((RequestType, List<ValueInList>?) -> Unit)? = null
) = withContext(Dispatchers.Main) {
    var result = bindRecyclerView(adapter, emptyItem, errorItem, loadMoreFooter, loadMoreHeader)
    if (show != null && hide != null) {
        result = result.bindProgress(show, hide)
    }
    result.collect(onFailed, onSuccess)
}