package com.like.recyclerview.ui

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.like.recyclerview.ui.BR
import com.like.recyclerview.ui.R
import com.like.recyclerview.model.IErrorItem

/**
 * 用于 RecyclerView 的错误视图
 *
 * @param resId         图标资源id
 * @param errorMessage  如果为空，那么在RecyclerViewHelper中，失败的时候会自动把失败原因赋值给它。
 */
data class DefaultErrorItem(
    @DrawableRes val resId: Int = R.drawable.recyclerview_default_error_item,
    override var errorMessage: String = "",
    @ColorRes val bgColor: Int = R.color.recyclerview_bg_white_0,
    @ColorRes val textColor: Int = R.color.recyclerview_text_gray_0,
    val textSize: Float = 16f
) : IErrorItem {
    override val layoutId: Int = R.layout.recyclerview_default_error_item
    override fun variableId(): Int = BR.errorItem
}