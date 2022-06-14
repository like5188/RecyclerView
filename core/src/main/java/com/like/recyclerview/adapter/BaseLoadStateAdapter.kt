package com.like.recyclerview.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.viewholder.BindingViewHolder
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseLoadStateAdapter<VB : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    LoadStateAdapter<BindingViewHolder<VB>>() {
    private val isFirstLoad = AtomicBoolean(true)
    private var hasInserted = false
    private var preLoadState: LoadState? = null
    private var curLoadState: LoadState? = null

    init {
        this.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    hasInserted = true
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    super.onItemRangeRemoved(positionStart, itemCount)
                    hasInserted = false
                }
            }
        )
    }

    final override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BindingViewHolder<VB> {
        return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false))
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, @SuppressLint("RecyclerView") loadState: LoadState) {
        onBindViewHolder(holder)
        if (curLoadState != loadState) {
            preLoadState = curLoadState
            curLoadState = loadState
            onLoadStateChange(preLoadState, curLoadState, holder)
        }
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        if (isFirstLoad.compareAndSet(true, false)) {// 首次加载时不显示
            return false
        }
        if (!hasInserted) {// 通过 notifyItemInserted 方法插入
            notifyItemInserted(0)
        }
        return true// 只触发 notifyItemChanged，具体逻辑查看父类的 set(loadState) 代码
    }

    abstract fun onBindViewHolder(holder: BindingViewHolder<VB>)
    abstract fun onLoadStateChange(preState: LoadState?, curState: LoadState?, holder: BindingViewHolder<VB>)

}