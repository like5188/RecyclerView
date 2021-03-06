package com.like.recyclerview.sample.addimage

import androidx.annotation.DrawableRes
import com.like.recyclerview.model.IItem
import com.like.recyclerview.sample.R

/**
 * +号视图需要的数据
 */
data class AddInfo(@DrawableRes val addImageResId: Int) : IItem {
    override var layoutId: Int = R.layout.view_add_image
}