package com.like.recyclerview.ui.loadmore

import androidx.annotation.ColorRes
import androidx.databinding.ObservableInt
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.ui.BR
import com.like.recyclerview.ui.R

/**
 * 用于 RecyclerView 的往后加载更多的视图
 *
 * @param loadingTip        加载中的描述
 * @param endTip            没有数据了的描述
 * @param errorTip          加载失败的描述
 */
data class LoadMoreItem(
    val loadingTip: String = "加载中……",
    val endTip: String = "没有数据啦",
    val errorTip: String = "加载失败，点击重试！",
    @ColorRes val bgColor: Int = R.color.recyclerview_bg_white_0,
    @ColorRes val progressBarBgColor: Int = R.color.recyclerview_bg_gray_0,
    @ColorRes val textColor: Int = R.color.recyclerview_text_gray_0,
    val textSize: Float = 16f,
) : IRecyclerViewItem {
    companion object {
        const val LOADING = 0
        const val END = 1
        const val ERROR = 2
    }

    override val layoutId: Int = R.layout.item_load_more
    override val variableId: Int = BR.loadMoreItem
    val status: ObservableInt = ObservableInt(LOADING)

    fun loading() {
        status.set(LOADING)
    }

    fun end() {
        status.set(END)
    }

    fun error(throwable: Throwable) {
        status.set(ERROR)
    }

}
