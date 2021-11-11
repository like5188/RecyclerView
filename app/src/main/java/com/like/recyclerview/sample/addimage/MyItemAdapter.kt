package com.like.recyclerview.sample.addimage

import androidx.databinding.ObservableBoolean
import coil.load
import com.like.recyclerview.ext.addimage.ItemAdapter
import com.like.recyclerview.sample.databinding.ViewImageBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import java.io.File

class MyItemAdapter(maxSelectNum: Int = Int.MAX_VALUE) : ItemAdapter<ViewImageBinding, AddImageViewInfo>(maxSelectNum) {
    val showDeleteButton: ObservableBoolean = ObservableBoolean()

    override fun onBindViewHolder(
        holder: BindingViewHolder<ViewImageBinding>,
        binding: ViewImageBinding,
        position: Int,
        item: AddImageViewInfo
    ) {
        super.onBindViewHolder(holder, binding, position, item)
        binding.iv.load(File(item.localMedia.compressPath))
        binding.tv.text = item.des
        binding.root.setOnLongClickListener {
            // 显示删除按钮
            if (!showDeleteButton.get()) {
                showDeleteButton.set(true)
            }
            true
        }
        binding.ivDelete.setOnClickListener {
            removeItem(holder.bindingAdapterPosition)
        }
        binding.showDeleteButton = showDeleteButton
    }

}
