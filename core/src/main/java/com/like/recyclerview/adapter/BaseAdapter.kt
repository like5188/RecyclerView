package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.utils.AdapterDataManager
import com.like.recyclerview.utils.IAdapterDataManager
import com.like.recyclerview.utils.IListenerManager
import com.like.recyclerview.utils.ListenerManager
import com.like.recyclerview.viewholder.BindingViewHolder

/**
 * 封装了
 * 1：单击、长按监听；
 * 2：数据处理；
 * 3：界面更新；
 */
open class BaseAdapter<VB : ViewDataBinding, ValueInList>
    : RecyclerView.Adapter<BindingViewHolder<VB>>(),
    IListenerManager<VB> by ListenerManager(),
    IAdapterDataManager<ValueInList> by AdapterDataManager() {
    init {
        this.initAdapterDataManager(this)
    }

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

    final override fun getItemCount(): Int {
        return mList.size
    }

    final override fun getItemViewType(position: Int): Int {
        val item = get(position) ?: return -1
        if (item is IRecyclerViewItem) {
            return item.layoutId
        }
        return getItemViewType(position, item)
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()) {
            val item = get(holder.bindingAdapterPosition) ?: return
            onBindViewHolder(holder, item, payloads)
        } else {
            onBindViewHolder(holder, position)
        }
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        val item = get(holder.bindingAdapterPosition) ?: return
        if (item is IRecyclerViewItem) {
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

    open fun getItemViewType(position: Int, item: ValueInList): Int = -1

    open fun onBindViewHolder(holder: BindingViewHolder<VB>, item: ValueInList, payloads: MutableList<Any>) {}

    open fun onBindViewHolder(holder: BindingViewHolder<VB>, item: ValueInList) {}

}
