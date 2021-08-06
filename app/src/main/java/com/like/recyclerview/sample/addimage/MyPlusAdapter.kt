package com.like.recyclerview.sample.addimage

import androidx.annotation.DrawableRes
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.sample.databinding.ViewAddImageBinding
import com.like.recyclerview.viewholder.BindingViewHolder

class MyPlusAdapter(
    private val activity: PictureSelectorActivity,
    @DrawableRes addImageResId: Int
) : AbstractAdapter<ViewAddImageBinding, AddInfo>() {

    init {
        addToEnd(AddInfo(addImageResId))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewAddImageBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = get(position) ?: return
        val binding = holder.binding
        binding.iv.setImageResource(item.addImageResId)
        binding.iv.setOnClickListener {
            activity.selectPhoto()
        }
    }

}