package com.like.recyclerview.sample.concat

import android.animation.ObjectAnimator
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.ext.animation.AdapterAnimationManager
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.viewholder.BindingViewHolder

class ItemAdapter : AbstractAdapter<ViewDataBinding, IRecyclerViewItem>() {
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
