package com.like.recyclerview.ui.error

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.databinding.ObservableField
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.ui.BR
import com.like.recyclerview.ui.R

/**
 * 用于 RecyclerView 的错误视图
 *
 * @param resId         图标资源id
 */
data class ErrorItem(
    @DrawableRes val resId: Int = R.drawable.icon_error_item,
    @ColorRes val bgColor: Int = R.color.recyclerview_bg_white_0,
    @ColorRes val textColor: Int = R.color.recyclerview_text_gray_0,
    val textSize: Float = 16f,
) : IRecyclerViewItem {
    override val layoutId: Int = R.layout.item_error
    override val variableId: Int = BR.errorItem
    val throwable: ObservableField<Throwable> = ObservableField(RuntimeException("unknown error"))

    fun error(throwable: Throwable) {
        this.throwable.set(throwable)
    }
}