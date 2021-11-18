package com.like.recyclerview.ext.animation

import android.animation.Animator
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.like.recyclerview.viewholder.BindingViewHolder

/*
class ItemAdapter : BaseAdapter<ViewDataBinding, IRecyclerViewItem>() {
    private val mAdapterAnimationManager: AdapterAnimationManager by lazy {
        AdapterAnimationManager {
            val scaleX = ObjectAnimator.ofFloat(it, "scaleX", 0.5f, 1f)
            val scaleY = ObjectAnimator.ofFloat(it, "scaleY", 0.5f, 1f)
            arrayOf(scaleX, scaleY)
        }.apply {
            setFirstOnly(false)
        }
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ViewDataBinding>,
        binding: ViewDataBinding,
        position: Int,
        item: IRecyclerViewItem
    ) {
        super.onBindViewHolder(holder, binding, position, item)
        mAdapterAnimationManager.onBindViewHolder(holder)
    }
}
 */
/**
 * item 动画管理
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
 *
 * 参考：https://github.com/wasabeef/recyclerview-animators
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
        v.apply {
            alpha = 1f
            scaleY = 1f
            scaleX = 1f
            translationY = 0f
            translationX = 0f
            rotation = 0f
            rotationY = 0f
            rotationX = 0f
            pivotY = v.measuredHeight / 2f
            pivotX = v.measuredWidth / 2f
            animate().setInterpolator(null).startDelay = 0
        }
    }
}
