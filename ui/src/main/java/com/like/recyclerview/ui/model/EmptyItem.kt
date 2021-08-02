package com.like.recyclerview.ui.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.like.recyclerview.ui.R

/**
 * 用于 RecyclerView 的空视图
 *
 * @param drawableRes   图标资源id
 * @param des           数据为空的描述
 */
data class EmptyItem(
    @DrawableRes val drawableRes: Int = R.drawable.icon_empty_item,
    val des: String = "暂无数据",
    @ColorRes val bgColor: Int = R.color.recyclerview_bg_white_0,
    @ColorRes val textColor: Int = R.color.recyclerview_text_gray_0,
    val textSize: Float = 16f,
)