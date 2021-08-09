package com.like.recyclerview.sample.addimage

import androidx.annotation.DrawableRes
import com.like.recyclerview.ext.addimage.PlusAdapter
import com.like.recyclerview.sample.databinding.ViewAddImageBinding
import com.like.recyclerview.viewholder.BindingViewHolder

class MyPlusAdapter(
    @DrawableRes addImageResId: Int
) : PlusAdapter<ViewAddImageBinding, AddInfo>() {

    init {
        addToEnd(AddInfo(addImageResId))
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<ViewAddImageBinding>,
        binding: ViewAddImageBinding,
        position: Int,
        item: AddInfo
    ) {
        super.onBindViewHolder(holder, binding, position, item)
        binding.iv.setImageResource(item.addImageResId)
    }

}
