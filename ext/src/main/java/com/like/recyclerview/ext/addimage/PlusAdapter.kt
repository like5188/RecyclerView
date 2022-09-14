package com.like.recyclerview.ext.addimage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.selectMultiplePhoto
import com.like.common.util.selectSinglePhoto
import com.like.recyclerview.viewholder.BindingViewHolder
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.launch

abstract class PlusAdapter<VB : ViewDataBinding>(
    private val maxSelectNum: Int = Int.MAX_VALUE
) : RecyclerView.Adapter<BindingViewHolder<VB>>() {
    lateinit var activity: AppCompatActivity
    lateinit var getSelectedLocalMedias: () -> List<LocalMedia>
    lateinit var onSelected: (List<LocalMedia>) -> Unit
    lateinit var onPlusClicked: () -> Unit

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(), parent, false))
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        holder.itemView.setOnClickListener {
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
        onBindViewHolder(holder)
    }

    final override fun getItemCount(): Int {
        return 1
    }

    abstract fun getLayoutId(): Int
    open fun onBindViewHolder(holder: BindingViewHolder<VB>) {}

}
