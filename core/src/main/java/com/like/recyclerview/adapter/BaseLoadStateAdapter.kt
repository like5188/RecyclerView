package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.like.common.util.Logger
import com.like.recyclerview.viewholder.BindingViewHolder
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseLoadStateAdapter<VB : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    LoadStateAdapter<BindingViewHolder<VB>>() {
    // 是否第一次刷新完成
    private var isFirstRefreshCompleted = AtomicBoolean(true)

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

    /**
     * 判断是否显示item，这里不显示，在 [updateLoadState] 方法中自己判断。
     *
     * @param loadState     append 的状态
     */
    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return false
    }

    fun updateLoadState(states: CombinedLoadStates) {
        // 第一次刷新完成时添加 Footer，以后都是更新 Footer，不存在删除 Footer 的时候。
        if (states.refresh is LoadState.NotLoading && isFirstRefreshCompleted.compareAndSet(true, false)) {
            notifyItemInserted(0)
        } else {
            notifyItemChanged(0)
        }
    }

    abstract fun onLoading(holder: BindingViewHolder<VB>)
    abstract fun onEnd(holder: BindingViewHolder<VB>)
    abstract fun onError(holder: BindingViewHolder<VB>, throwable: Throwable)
    abstract fun onBindViewHolder(holder: BindingViewHolder<VB>)

}