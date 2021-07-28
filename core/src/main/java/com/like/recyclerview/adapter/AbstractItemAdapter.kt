package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.listener.OnItemLongClickListener
import com.like.recyclerview.viewholder.BindingViewHolder

abstract class AbstractItemAdapter<T : ViewDataBinding> : RecyclerView.Adapter<BindingViewHolder<T>>() {
    var recyclerView: RecyclerView? = null
    private val mOnItemClickListeners = mutableListOf<OnItemClickListener<T>>()
    private val mOnItemLongClickListeners = mutableListOf<OnItemLongClickListener<T>>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<T> {
        return BindingViewHolder(DataBindingUtil.inflate<T>(LayoutInflater.from(parent.context), viewType, parent, false)).apply {
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

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    fun addOnItemClickListener(listener: OnItemClickListener<T>) {
        mOnItemClickListeners.add(listener)
    }

    fun addOnItemLongClickListener(listener: OnItemLongClickListener<T>) {
        mOnItemLongClickListeners.add(listener)
    }

    fun removeOnItemClickListener(listener: OnItemClickListener<T>) {
        mOnItemClickListeners.remove(listener)
    }

    fun removeOnItemLongClickListener(listener: OnItemLongClickListener<T>) {
        mOnItemLongClickListeners.remove(listener)
    }

    fun clearOnItemClickListeners() {
        mOnItemClickListeners.clear()
    }

    fun clearOnItemLongClickListeners() {
        mOnItemLongClickListeners.clear()
    }

}
