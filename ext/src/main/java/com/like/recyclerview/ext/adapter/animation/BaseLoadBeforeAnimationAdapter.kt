package com.like.recyclerview.ext.adapter.animation

import android.animation.Animator
import android.view.View
import com.like.recyclerview.adapter.BaseLoadBeforeAdapter
import com.like.recyclerview.ext.utils.AdapterAnimationManager
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.viewholder.CommonViewHolder

/**
 * 带动画效果的Adapter基类，当需要往前加载更多时使用
 */
open class BaseLoadBeforeAnimationAdapter(
    animators: (view: View) -> Array<Animator>,
    pageSize: Int,
    onLoadBefore: () -> Unit
) : BaseLoadBeforeAdapter(pageSize, onLoadBefore) {

    private val adapterAnimationManager: AdapterAnimationManager by lazy {
        AdapterAnimationManager(animators)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {
        super.onBindViewHolder(holder, position, item)
        adapterAnimationManager.onBindViewHolder(holder, position)
    }

}
