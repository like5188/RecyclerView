package com.like.recyclerview.sample.addimage

import androidx.databinding.ObservableBoolean
import coil.load
import com.like.recyclerview.ext.adapter.DragAdapter
import com.like.recyclerview.sample.databinding.ViewImageBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import java.io.File

class MyDragAdapter(
    private val activity: PictureSelectorActivity,
) : DragAdapter<ViewImageBinding, AddImageViewInfo>() {
    val deleteButtonShown: ObservableBoolean = ObservableBoolean()

    fun getLocalMedias() = mList.map {
        it.localMedia
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewImageBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = get(position) ?: return
        val binding = holder.binding
        binding.iv.load(File(item.compressImagePath))
        binding.tv.text = item.des
        binding.root.setOnLongClickListener {
            // 显示删除按钮
            if (!deleteButtonShown.get()) {
                deleteButtonShown.set(true)
            }
            true
        }
        binding.root.setOnClickListener {
            activity.preViewImage(position)
        }
        binding.ivDelete.setOnClickListener {
            activity.removeItem(item)
        }
        binding.deleteButtonShown = deleteButtonShown
    }

}