package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.Logger
import com.like.recyclerview.viewholder.BindingViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
                Logger.d("BaseLoadStateAdapter onError")
            }
            is LoadState.NotLoading -> {
                if (loadState.endOfPaginationReached) {
                    onNoMore(holder)
                    Logger.d("BaseLoadStateAdapter onNoMore")
                } else {
                    Logger.d("BaseLoadStateAdapter onIdle")
                }
            }
            is LoadState.Loading -> {
                onLoading(holder)
                Logger.d("BaseLoadStateAdapter onLoading")
            }
        }
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        // 是否显示 Footer
        val result = super.displayLoadStateAsItem(loadState) || refreshLoadState is LoadState.NotLoading
        if (result && !hasInserted) {// 插入 Footer
            notifyItemInserted(0)
        }
        return result
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        // 获取 refreshLoadState 状态，用于 Footer 显示的判断。
        val pagingDataAdapter: PagingDataAdapter<*, *>? = when (val adapter = recyclerView.adapter) {
            is PagingDataAdapter<*, *> -> adapter
            is ConcatAdapter -> adapter.adapters.firstOrNull {
                it is PagingDataAdapter<*, *>
            } as? PagingDataAdapter<*, *>
            else -> null
        }
        val lifecycleScope: CoroutineScope? = when (val owner = recyclerView.context) {
            is LifecycleOwner -> owner.lifecycleScope
            else -> null
        }
        if (lifecycleScope != null && pagingDataAdapter != null) {
            lifecycleScope.launch {
                pagingDataAdapter.loadStateFlow.collectLatest {
                    this@BaseLoadStateAdapter.refreshLoadState = it.refresh
                }
            }
        }
    }

    abstract fun onLoading(holder: BindingViewHolder<VB>)
    abstract fun onNoMore(holder: BindingViewHolder<VB>)
    abstract fun onError(holder: BindingViewHolder<VB>, throwable: Throwable)
    abstract fun onBindViewHolder(holder: BindingViewHolder<VB>)

}