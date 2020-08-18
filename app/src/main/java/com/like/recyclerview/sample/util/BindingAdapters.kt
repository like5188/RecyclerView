package com.like.recyclerview.sample.util

import androidx.databinding.BindingAdapter
import androidx.annotation.DrawableRes
import android.widget.ImageView

object BindingAdapters {

    @BindingAdapter("showImage")
    @JvmStatic
    fun showImage(iv: ImageView, @DrawableRes resId: Int) {
        iv.setImageResource(resId)
    }
}
