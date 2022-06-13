package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.Logger
import com.like.recyclerview.viewholder.BindingViewHolder
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseLoadStateAdapter<VB : ViewDataBinding>(@LayoutRes private val layoutId: Int) :
    LoadStateAdapter<BindingViewHolder<VB>>() {
    private var isFirstRefreshCompleted = AtomicBoolean(true)
    private var refreshLoadState: LoadState? = null

    init {
        this.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    Logger.d("onItemRangeInserted")
                }

                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeChanged(positionStart, itemCount)
                    Logger.d("onItemRangeChanged")
                }

                override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                    super.onItemRangeRemoved(positionStart, itemCount)
                    Logger.d("onItemRangeRemoved")
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

    /**
     * 判断是否显示item
     */
    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        Logger.e("append loadState=$loadState refreshLoadState=$refreshLoadState")
        return refreshLoadState != null
    }

    // 把 refresh 状态添加进来。因为 displayLoadStateAsItem() 方法中的参数只是针对 append。
    fun handRefreshLoadState(loadState: LoadState) {
        Logger.v("refresh loadState=$loadState")
        refreshLoadState = loadState
        if (loadState is LoadState.NotLoading && isFirstRefreshCompleted.compareAndSet(true, false)) {
            notifyItemInserted(0)
        }
    }

    abstract fun onLoading(holder: BindingViewHolder<VB>)
    abstract fun onEnd(holder: BindingViewHolder<VB>)
    abstract fun onError(holder: BindingViewHolder<VB>, throwable: Throwable)
    abstract fun onBindViewHolder(holder: BindingViewHolder<VB>)

}