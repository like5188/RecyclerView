package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.like.recyclerview.viewholder.BindingViewHolder

abstract class BaseLoadStateAdapter<VB : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    LoadStateAdapter<BindingViewHolder<VB>>() {
    final override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BindingViewHolder<VB> {
        return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false))
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, loadState: LoadState) {
        when (loadState) {
            is LoadState.Error -> {
                onError(holder, loadState.error)
            }
            is LoadState.NotLoading -> {
                if (loadState.endOfPaginationReached) {
                    onEnd(holder)
                }
            }
            is LoadState.Loading -> {
                onLoading(holder)
            }
        }
    }

    /**
     * 判断是否显示item
     */
    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return loadState is LoadState.Loading || loadState is LoadState.Error || (loadState is LoadState.NotLoading && loadState.endOfPaginationReached)
    }

    abstract fun onLoading(holder: BindingViewHolder<VB>)
    abstract fun onEnd(holder: BindingViewHolder<VB>)
    abstract fun onError(holder: BindingViewHolder<VB>, throwable: Throwable)

}