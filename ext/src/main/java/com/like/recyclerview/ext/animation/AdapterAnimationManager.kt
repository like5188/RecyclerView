package com.like.recyclerview.ext.animation

import android.animation.Animator
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import androidx.core.view.ViewCompat
import com.like.recyclerview.viewholder.BindingViewHolder

/**
 * item 动画管理
 * 使用方式：在 adapter 的 onBindViewHolder 方法中调用 adapterAnimationManager.onBindViewHolder(holder, position)
 *
 * @param animators 需要item执行的动画效果
 *
 * 比如：
 * 1、item带缩放动画
 * val scaleX = ObjectAnimator.ofFloat(view, "scaleX", mFrom, 1f)
 * val scaleY = ObjectAnimator.ofFloat(view, "scaleY", mFrom, 1f)
 * arrayOf(scaleX, scaleY)
 *
 * 2、item带透明渐显动画
 * arrayOf(ObjectAnimator.ofFloat(view, "alpha", mFrom, 1f))
 */
class AdapterAnimationManager(private val animators: (View) -> Array<Animator>) {
    private var mDuration = 300L
    private var mInterpolator: Interpolator = LinearInterpolator()
    private var mLastPosition = -1

    /**
     * 是否只是第一次启动动画
     */
    private var isFirstOnly = true

    fun onBindViewHolder(holder: BindingViewHolder<*>) {
        val position = holder.bindingAdapterPosition
        if (!isFirstOnly || position > mLastPosition) {
            val animators = animators(holder.itemView)
            animators.forEach {
                it.interpolator = mInterpolator
                it.setDuration(mDuration).start()
            }
            mLastPosition = position
        } else {
            clear(holder.itemView)
        }
    }

    fun setDuration(duration: Long) {
        mDuration = duration
    }

    fun setInterpolator(interpolator: Interpolator) {
        mInterpolator = interpolator
    }

    /**
     * 设置开始动画的item的位置，当item的位置大于此值时，就会触发动画
     */
    fun setStartPosition(start: Int) {
        mLastPosition = start
    }

    fun setFirstOnly(firstOnly: Boolean) {
        isFirstOnly = firstOnly
    }

    private fun clear(v: View) {
        ViewCompat.setAlpha(v, 1f)
        ViewCompat.setScaleY(v, 1f)
        ViewCompat.setScaleX(v, 1f)
        ViewCompat.setTranslationY(v, 0f)
        ViewCompat.setTranslationX(v, 0f)
        ViewCompat.setRotation(v, 0f)
        ViewCompat.setRotationY(v, 0f)
        ViewCompat.setRotationX(v, 0f)
        ViewCompat.setPivotY(v, (v.measuredHeight / 2).toFloat())
        ViewCompat.setPivotX(v, (v.measuredWidth / 2).toFloat())
        ViewCompat.animate(v).setInterpolator(null).startDelay = 0
    }
}
