package com.like.recyclerview.ui.model

import androidx.annotation.ColorRes
import androidx.databinding.ObservableInt
import com.like.recyclerview.ui.R

/**
 * 用于 RecyclerView 的往后加载更多的视图
 *
 * @param completeTip       加载中的描述
 * @param endTip            没有数据了的描述
 * @param errorTip          加载失败的描述
 */
data class LoadMoreItem(
    val completeTip: String = "加载中……",
    val endTip: String = "没有数据啦",
    val errorTip: String = "加载失败，点击重试！",
    @ColorRes val bgColor: Int = R.color.recyclerview_bg_white_0,
    @ColorRes val progressBarBgColor: Int = R.color.recyclerview_bg_gray_0,
    @ColorRes val textColor: Int = R.color.recyclerview_text_gray_0,
    val textSize: Float = 16f,
) {
    val status: ObservableInt = ObservableInt(0)

    fun onComplete() {
        status.set(0)
    }

    fun onEnd() {
        status.set(1)
    }

    fun onError(throwable: Throwable) {
        status.set(2)
    }

}
