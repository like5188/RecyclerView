package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.listener.OnItemLongClickListener
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.utils.AdapterDataManager
import com.like.recyclerview.utils.IAdapterDataManager
import com.like.recyclerview.utils.NotifyOnListChangedCallback
import com.like.recyclerview.viewholder.BindingViewHolder

/**
 * 封装了
 * 1：单击、长按监听；
 * 2：数据处理；
 * 3：界面更新；
 */
abstract class AbstractAdapter<VB : ViewDataBinding, ValueInList>
    : RecyclerView.Adapter<BindingViewHolder<VB>>(),
    IAdapterDataManager<ValueInList> by AdapterDataManager() {
    companion object {
        private const val TAG = "AbstractAdapter"
    }

    private val mOnItemClickListeners = mutableListOf<OnItemClickListener<VB>>()
    private val mOnItemLongClickListeners = mutableListOf<OnItemLongClickListener<VB>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        return BindingViewHolder(DataBindingUtil.inflate<VB>(LayoutInflater.from(parent.context), viewType, parent, false)).apply {
            // 为list添加Item的点击事件监听
            if (mOnItemClickListeners.isNotEmpty()) {
                itemView.setOnClickListener {
                    mOnItemClickListeners.forEach {
                        it.onItemClick(this)
                    }
                }
            }
            // 为list添加Item的长按事件监听
            if (mOnItemLongClickListeners.isNotEmpty()) {
                itemView.setOnLongClickListener {
                    mOnItemLongClickListeners.forEach {
                        it.onItemLongClick(this)
                    }
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    final override fun getItemViewType(position: Int): Int {
        val item = get(position)
        if (item is IRecyclerViewItem) {
            return item.layoutId
        }
        return getLayoutId(position)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        val item = get(position)
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
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mList.addOnListChangedCallback(NotifyOnListChangedCallback(recyclerView))
    }

    fun addOnItemClickListener(listener: OnItemClickListener<VB>) {
        mOnItemClickListeners.add(listener)
    }

    fun addOnItemLongClickListener(listener: OnItemLongClickListener<VB>) {
        mOnItemLongClickListeners.add(listener)
    }

    fun removeOnItemClickListener(listener: OnItemClickListener<VB>) {
        mOnItemClickListeners.remove(listener)
    }

    fun removeOnItemLongClickListener(listener: OnItemLongClickListener<VB>) {
        mOnItemLongClickListeners.remove(listener)
    }

    fun clearOnItemClickListeners() {
        mOnItemClickListeners.clear()
    }

    fun clearOnItemLongClickListeners() {
        mOnItemLongClickListeners.clear()
    }

    open fun getLayoutId(position: Int): Int = -1

}
