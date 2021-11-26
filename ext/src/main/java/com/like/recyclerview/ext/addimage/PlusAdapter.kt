package com.like.recyclerview.ext.addimage

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import com.like.common.util.selectMultiplePhoto
import com.like.common.util.selectSinglePhoto
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.viewholder.BindingViewHolder
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.launch

open class PlusAdapter<VB : ViewDataBinding, ValueInList>(
    private val maxSelectNum: Int = Int.MAX_VALUE
) : BaseAdapter<VB, ValueInList>() {
    lateinit var activity: AppCompatActivity
    lateinit var getSelectedLocalMedias: () -> List<LocalMedia>
    lateinit var onSelected: (List<LocalMedia>) -> Unit
    lateinit var onPlusClicked: () -> Unit

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, item: ValueInList) {
        super.onBindViewHolder(holder, item)
        holder.binding.root.setOnClickListener {
            activity.lifecycleScope.launch {
                if (maxSelectNum == 1) {
                    activity.selectSinglePhoto()?.apply {
                        onSelected(listOf(this))
                    }
                } else {
                    activity.selectMultiplePhoto(getSelectedLocalMedias(), maxSelectNum)?.apply {
                        onSelected(this)
                    }
                }
            }
            onPlusClicked()
        }
    }

}
