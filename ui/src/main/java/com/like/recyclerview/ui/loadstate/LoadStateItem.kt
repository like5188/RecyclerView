package com.like.recyclerview.ui.loadstate

import androidx.annotation.ColorRes
import androidx.databinding.ObservableInt
import com.like.recyclerview.ui.R

/**
 * 加载状态数据
 *
 * @param loadingTip        加载中的描述
 * @param endTip            没有数据了的描述
 * @param errorTip          加载失败的描述
 */
data class LoadStateItem(
    val loadingTip: String = "加载中……",
    val endTip: String = "没有数据啦",
    val errorTip: String = "加载失败，点击重试！",
    @ColorRes val bgColor: Int = R.color.recyclerview_bg_white_0,
    @ColorRes val progressBarBgColor: Int = R.color.recyclerview_bg_gray_0,
    @ColorRes val textColor: Int = R.color.recyclerview_text_gray_0,
    val textSize: Float = 16f,
) {
    companion object {
        const val LOADING = 0
        const val END = 1
        const val ERROR = 2
    }

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
