package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.Logger
import com.like.recyclerview.viewholder.BindingViewHolder

abstract class BaseLoadStateAdapter<VB : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    LoadStateAdapter<BindingViewHolder<VB>>() {
    private var refreshLoadState: LoadState? = null
    private var hasInserted = false

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

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, loadState: LoadState) {
        onBindViewHolder(holder)
        when (loadState) {
            is LoadState.Error -> {
                onError(holder, loadState.error)
                Logger.d("onError")
            }
            is LoadState.NotLoading -> {
                if (loadState.endOfPaginationReached) {
                    onEnd(holder)
                    Logger.d("onEnd")
                } else {
                    Logger.d("onIdle")
                }
            }
            is LoadState.Loading -> {
                onLoading(holder)
                Logger.d("onLoading")
            }
        }
    }

    fun updateLoadState(states: CombinedLoadStates) {
        this.refreshLoadState = states.refresh
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        val result = super.displayLoadStateAsItem(loadState) || refreshLoadState is LoadState.NotLoading
        if (result && !hasInserted) {
            notifyItemInserted(0)
        }
        return result
    }

    abstract fun onLoading(holder: BindingViewHolder<VB>)
    abstract fun onEnd(holder: BindingViewHolder<VB>)
    abstract fun onError(holder: BindingViewHolder<VB>, throwable: Throwable)
    abstract fun onBindViewHolder(holder: BindingViewHolder<VB>)

}