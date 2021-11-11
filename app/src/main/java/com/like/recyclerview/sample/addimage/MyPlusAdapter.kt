package com.like.recyclerview.sample.addimage

import androidx.annotation.DrawableRes
import com.like.recyclerview.ext.addimage.PlusAdapter
import com.like.recyclerview.sample.databinding.ViewAddImageBinding
import com.like.recyclerview.viewholder.BindingViewHolder

class MyPlusAdapter(
    @DrawableRes addImageResId: Int,
    maxSelectNum: Int = Int.MAX_VALUE
) : PlusAdapter<ViewAddImageBinding, AddInfo>(maxSelectNum) {

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
