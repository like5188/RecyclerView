package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.utils.IListenerManager
import com.like.recyclerview.utils.ListenerManager
import com.like.recyclerview.viewholder.BindingViewHolder

/**
 * 继承自 [ListAdapter]
 * 1：单击、长按监听；
 * 2：对[IRecyclerViewItem]类型的数据进行了对应处理；
 */
open class BaseListAdapter<VB : ViewDataBinding, ValueInList>(diffCallback: DiffUtil.ItemCallback<ValueInList>) :
    ListAdapter<ValueInList, BindingViewHolder<VB>>(diffCallback),
    IListenerManager<VB> by ListenerManager() {

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        return BindingViewHolder(DataBindingUtil.inflate<VB>(LayoutInflater.from(parent.context), viewType, parent, false)).apply {
            // 为list添加Item的点击事件监听
            itemView.setOnClickListener {
                onItemClick(this)
            }
            // 为list添加Item的长按事件监听
            itemView.setOnLongClickListener {
                onItemLongClick(this)
                true
            }
        }
    }

    final override fun getItemViewType(position: Int): Int {
        val item = getItemOrNull(position)
        if (item != null && item is IRecyclerViewItem) {
            return item.layoutId
        }
        return getItemViewType(position, item)
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        val item = getItemOrNull(holder.bindingAdapterPosition)
        if (item != null && item is IRecyclerViewItem) {
            val variableId = item.variableId
            if (variableId >= 0) {
                try {
                    holder.binding.setVariable(variableId, item)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        // 这里不能直接把 holder.bindingAdapterPosition 的值传递下去，因为有添加删除前面的 item 都会造成后面 item 的位置改变，
        // 所以在使用的时候，需要随时使用 holder.bindingAdapterPosition 重新获取。
        onBindViewHolder(holder, item)
    }

    fun getItemOrNull(position: Int): ValueInList? {
        return try {
            getItem(position)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 如果没有使用 [IRecyclerViewItem] 类型的数据类，则必须重写此方法
     */
    open fun getItemViewType(position: Int, item: ValueInList?): Int = -1

    open fun onBindViewHolder(holder: BindingViewHolder<VB>, item: ValueInList?) {}

}
