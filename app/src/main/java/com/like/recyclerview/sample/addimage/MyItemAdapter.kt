package com.like.recyclerview.sample.addimage

import androidx.databinding.ObservableBoolean
import coil.load
import com.like.recyclerview.ext.addimage.ItemAdapter
import com.like.recyclerview.sample.databinding.ViewImageBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import java.io.File

class MyItemAdapter : ItemAdapter<ViewImageBinding, AddImageViewInfo>( 9) {
    val showDeleteButton: ObservableBoolean = ObservableBoolean()

    override fun onBindViewHolder(holder: BindingViewHolder<ViewImageBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = get(position) ?: return
        val binding = holder.binding
        binding.iv.load(File(item.compressImagePath))
        binding.tv.text = item.des
        binding.root.setOnLongClickListener {
            // 显示删除按钮
            if (!showDeleteButton.get()) {
                showDeleteButton.set(true)
            }
            true
        }
        binding.ivDelete.setOnClickListener {
            removeItem(item)
        }
        binding.showDeleteButton = showDeleteButton
    }

}
