package com.like.recyclerview.ui

import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ContentLoadingProgressBar
import androidx.databinding.BindingAdapter

object BindingAdapters {

    @BindingAdapter("showImage")
    @JvmStatic
    fun showImage(iv: ImageView, @DrawableRes resId: Int) {
        iv.setImageResource(resId)
    }

    @BindingAdapter("setBackgroundResource")
    @JvmStatic
    fun setBackgroundResource(view: View, @ColorRes resId: Int) {
        view.setBackgroundResource(resId)
    }

    @BindingAdapter("setTextColor")
    @JvmStatic
    fun setTextColor(textView: TextView, @ColorRes resId: Int) {
        textView.setTextColor(ContextCompat.getColor(textView.context, resId))
    }

    @BindingAdapter("setTextSize")
    @JvmStatic
    fun setTextSize(textView: TextView, size: Float) {
        textView.textSize = size
    }

    @BindingAdapter("setContentLoadingProgressBarBgColor")
    @JvmStatic
    fun setContentLoadingProgressBarBgColor(progressBar: ContentLoadingProgressBar, @ColorRes resId: Int) {
        val color = ContextCompat.getColor(progressBar.context, resId)
        progressBar.indeterminateDrawable.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }
}
