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
 * 不分页时，把数据绑定到 [BaseAdapter] 上。
 *
 * @param block         获取 List<IRecyclerViewItem> 类型数据的函数
 * @param isRefresh     是否刷新操作。true：刷新操作；false：初始化操作；
 * @param emptyItem     数据为空时显示的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultEmptyItem]
 * @param errorItem     请求出错时显示的视图。只针对初始化出错时才显示，刷新出错时不显示。[com.like.recyclerview.ui]库中默认实现了：[DefaultErrorItem]
 * @param show          初始化或者刷新开始时显示进度条
 * @param hide          初始化或者刷新成功或者失败时隐藏进度条
 */
suspend fun <ValueInList : IRecyclerViewItem> BaseAdapter.bindData(
    block: suspend () -> List<ValueInList>?,
    isRefresh: Boolean = false,
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
): List<ValueInList>? = withContext(Dispatchers.Main) {
    show?.invoke()
    try {
        val list = withContext(Dispatchers.IO) {
            block()
        }
        if (list.isNullOrEmpty()) {
            emptyItem?.let {
                this@bindData.mAdapterDataManager.setEmptyItem(emptyItem)
            }
        } else {
            this@bindData.mAdapterDataManager.clearAndAddAll(list)
        }
        list
    } catch (e: Exception) {
        if (!isRefresh) {
            errorItem?.let {
                errorItem.throwable = e
                this@bindData.mAdapterDataManager.setErrorItem(errorItem)
            }
        }
        throw e
    } finally {
        hide?.invoke()
    }
}

/**
 * 分页时，把数据绑定到 [BaseAdapter] 上。配合[com.like.paging]库使用
 *
 * @param result            分页数据源提供的[com.like.paging.Result]
 * @param isLoadAfter       是否为往后加载更多。true：往后加载更多；false：往前加载更多；
 * @param loadMoreFooter    往后加载更多的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultLoadMoreFooter]
 * @param loadMoreHeader    往前加载更多的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultLoadMoreHeader]
 * @param emptyItem         数据为空时显示的视图。[com.like.recyclerview.ui]库中默认实现了：[DefaultEmptyItem]
 * @param errorItem         请求出错时显示的视图。只针对初始化出错时才显示，刷新出错时不显示。[com.like.recyclerview.ui]库中默认实现了：[DefaultErrorItem]
 * @param show              初始化或者刷新开始时显示进度条
 * @param hide              初始化或者刷新成功或者失败时隐藏进度条
 * @param onFailed          失败回调，如果需要做其它错误处理，可以从这里获取。
 * @param onSuccess         成功回调，如果需要结果，可以从这里获取。
 */
suspend fun <ValueInList : IRecyclerViewItem> BaseAdapter.bindData(
    result: Result<List<ValueInList>?>,
    isLoadAfter: Boolean = true,
    loadMoreFooter: ILoadMoreFooter? = if (isLoadAfter) {
        DefaultLoadMoreFooter { result.retry() }
    } else {
        null
    },
    loadMoreHeader: ILoadMoreHeader? = if (!isLoadAfter) {
        DefaultLoadMoreHeader { result.retry() }
    } else {
        null
    },
    emptyItem: IEmptyItem? = DefaultEmptyItem(),
    errorItem: IErrorItem? = DefaultErrorItem(),
    show: (() -> Unit)? = null,
    hide: (() -> Unit)? = null,
    onFailed: ((RequestType, Throwable) -> Unit)? = null,
    onSuccess: ((RequestType, List<ValueInList>?) -> Unit)? = null,
) = withContext(Dispatchers.Main) {
    var result1 = result.bindRecyclerView(
        this@bindData,
        emptyItem,
        errorItem,
        loadMoreFooter,
        loadMoreHeader)
    if (show != null && hide != null) {
        result1 = result1.bindProgress(show, hide)
    }
    result1.collect(onFailed, onSuccess)
}