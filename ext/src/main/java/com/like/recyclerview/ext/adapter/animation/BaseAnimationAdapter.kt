package com.like.recyclerview.ext.adapter.animation

import android.animation.Animator
import android.view.View
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.ext.utils.AdapterAnimationManager
import com.like.recyclerview.viewholder.CommonViewHolder

/**
 * 带动画效果的Adapter基类，当不需要分页加载时使用。
 */
open class BaseAnimationAdapter(animators: (view: View) -> Array<Animator>) : BaseAdapter() {
    private val adapterAnimationManager: AdapterAnimationManager by lazy {
        AdapterAnimationManager(animators)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        adapterAnimationManager.onBindViewHolder(holder, position)
    }

}
