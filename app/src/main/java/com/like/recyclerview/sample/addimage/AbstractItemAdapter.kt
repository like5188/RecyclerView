package com.like.recyclerview.sample.addimage

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableBoolean
import coil.load
import com.like.common.util.CoilEngine
import com.like.recyclerview.ext.adapter.DragAdapter
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ViewImageBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import java.io.File

abstract class AbstractItemAdapter(
    private val activity: AppCompatActivity,
) : DragAdapter<ViewImageBinding, AddImageViewInfo>() {
    private val maxImageCount = 9
    val showDeleteButton: ObservableBoolean = ObservableBoolean()

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
            if (!showDeleteButton.get()) {
                showDeleteButton.set(true)
            }
            true
        }
        binding.root.setOnClickListener {
            val localMedias = getLocalMedias()
            // 预览图片 可自定长按保存路径
            // 注意 .themeStyle(R.style.theme)；里面的参数不可删，否则闪退...
            PictureSelector.create(activity)
                .themeStyle(R.style.picture_default_style)
                .isNotPreviewDownload(true)
                .imageEngine(CoilEngine.create()) // 请参考Demo GlideEngine.java
                .openExternalPreview(position, localMedias)
        }
        binding.ivDelete.setOnClickListener {
            removeItem(item)
        }
        binding.deleteButtonShown = showDeleteButton
    }

    fun addLocalMedias(list: List<LocalMedia>) {
        val localMedias = getLocalMedias()
        // 去掉已经添加过的
        val items = list.filter { !localMedias.contains(it) }.map {
            AddImageViewInfo(it, "des")
        }
        val curCount = mList.size
        when {
            curCount + items.size < maxImageCount -> {
                addAllToEnd(items)
            }
            curCount + items.size == maxImageCount -> {
                notifyRemovePlus()
                addAllToEnd(items)
            }
            else -> {// 不能添加
                Toast.makeText(activity, "只能添加 $maxImageCount 张图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeItem(item: AddImageViewInfo) {
        val curCount = mList.size
        when {
            curCount < maxImageCount -> {// 不添加+号图片
                remove(item)
            }
            curCount == maxImageCount -> {// 添加+号图片
                remove(item)
                notifyAddPlus()
            }
        }
    }

    abstract fun notifyRemovePlus()
    abstract fun notifyAddPlus()

}