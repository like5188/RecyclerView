package com.like.recyclerview.ext.addimage

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.previewPhotos
import com.like.recyclerview.adapter.BaseListAdapter
import com.like.recyclerview.utils.ItemTouchHelperCallback
import com.like.recyclerview.viewholder.BindingViewHolder
import com.luck.picture.lib.entity.LocalMedia

open class ItemAdapter<VB : ViewDataBinding, ValueInList>(
    diffCallback: DiffUtil.ItemCallback<ValueInList>,
    private val maxSelectNum: Int = Int.MAX_VALUE
) : BaseListAdapter<VB, ValueInList>(diffCallback) {
    lateinit var activity: AppCompatActivity
    lateinit var itemCreator: (LocalMedia) -> ValueInList
    lateinit var getSelectedLocalMedias: () -> List<LocalMedia>
    lateinit var notifyRemovePlus: () -> Unit
    lateinit var notifyAddPlus: () -> Unit
    private val mItemTouchHelper by lazy {
        ItemTouchHelper(ItemTouchHelperCallback(this))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, item: ValueInList) {
        super.onBindViewHolder(holder, item)
        holder.binding.root.setOnClickListener {
            activity.previewPhotos(getSelectedLocalMedias(), holder.bindingAdapterPosition)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun addLocalMedias(list: List<LocalMedia>) {
        val localMedias = getSelectedLocalMedias()
        // 去掉已经添加过的
        val items = list.filter { !localMedias.contains(it) }.map {
            itemCreator(it)
        }
        val curCount = itemCount
        when {
            curCount + items.size < maxSelectNum -> {
                val newItems = currentList.toMutableList()
                newItems.addAll(items)
                submitList(newItems)
                onAdded()
            }
            curCount + items.size == maxSelectNum -> {// 移除+号
                notifyRemovePlus()
                val newItems = currentList.toMutableList()
                newItems.addAll(items)
                submitList(newItems)
                onAdded()
            }
            else -> {// 不能添加
                Toast.makeText(activity, "只能添加 $maxSelectNum 张图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removeItem(position: Int) {
        val curCount = itemCount
        when {
            curCount < maxSelectNum -> {
                val newItems = currentList.toMutableList()
                newItems.removeAt(position)
                submitList(newItems)
                onRemoved()
            }
            curCount == maxSelectNum -> {// 添加+号
                val newItems = currentList.toMutableList()
                newItems.removeAt(position)
                submitList(newItems)
                notifyAddPlus()
                onRemoved()
            }
        }
    }

    open fun onAdded() {

    }

    open fun onRemoved() {

    }

}
