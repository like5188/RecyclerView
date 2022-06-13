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
    //记录列表adapter的loadState
    private var outLoadStates: CombinedLoadStates? = null

    //记录自身是否被添加进RecycleView
    var hasInserted = false

    init {
        //注册监听，记录是否被添加
        this.registerAdapterDataObserver(
            object : RecyclerView.AdapterDataObserver() {

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    Logger.e("onItemRangeInserted")
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

    //更新外部LoadState
    fun updateLoadState(loadState: CombinedLoadStates) {
        outLoadStates = loadState
    }

    //重写，增加判断逻辑
    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        //新增逻辑，refresh状态为NotLoading之后，NotLoading再显示footer
        val resultB = (loadState is LoadState.NotLoading && outLoadStates?.refresh is LoadState.NotLoading)
        val result = super.displayLoadStateAsItem(loadState) || resultB
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