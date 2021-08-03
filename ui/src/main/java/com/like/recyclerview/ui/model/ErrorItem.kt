package com.like.recyclerview.ui.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.like.recyclerview.ui.R

/**
 * 用于 RecyclerView 的错误视图
 *
 * @param resId         图标资源id
 */
data class ErrorItem(
    var throwable: Throwable = RuntimeException("unknown error"),
    @DrawableRes val resId: Int = R.drawable.icon_error_item,
    @ColorRes val bgColor: Int = R.color.recyclerview_bg_white_0,
    @ColorRes val textColor: Int = R.color.recyclerview_text_gray_0,
    val textSize: Float = 16f,
) {
    fun onError(throwable: Throwable) {
        this.throwable = throwable
    }
}