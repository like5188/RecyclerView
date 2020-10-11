package com.like.recyclerview.sample

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.util.bindProgress
import com.like.common.util.bindRecyclerView
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.model.*
import com.like.recyclerview.ui.DefaultEmptyItem
import com.like.recyclerview.ui.DefaultErrorItem
import com.like.recyclerview.ui.DefaultLoadMoreFooter
import com.like.recyclerview.ui.DefaultLoadMoreHeader
import com.like.repository.Result

/**
 * 把 [Result] 与 [SwipeRefreshLayout] 进行绑定
 */
fun <ResultType> Result<ResultType>.bindProgress(
    lifecycleOwner: LifecycleOwner,
    swipeRefreshLayout: SwipeRefreshLayout,
    @ColorInt vararg colors: Int = intArrayOf(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW),
) {
    swipeRefreshLayout.setColorSchemeColors(*colors)

    swipeRefreshLayout.setOnRefreshListener {
        refresh()
    }

    bindProgress(
        lifecycleOwner,
        { swipeRefreshLayout.isRefreshing = true },
        { swipeRefreshLayout.isRefreshing = false }
    )
}

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