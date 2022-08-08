package com.like.recyclerview.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.Logger
import com.like.recyclerview.viewholder.BindingViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 加载状态 Adapter
 *
 * 处理了3种触发加载更多的情况：1、数据插入时触发；2、滚动界面触发；3、加载失败后由点击事件触发；
 */
abstract class BaseLoadStateAdapter<VB : ViewDataBinding> : RecyclerView.Adapter<BindingViewHolder<VB>>() {
    private val hasMore = AtomicBoolean(false)
    internal var onLoadMore: suspend () -> Unit = {}
    private lateinit var mHolder: BindingViewHolder<VB>
    internal var isAfter: Boolean = true

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), getLayoutId(), parent, false))
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB>, position: Int) {
        mHolder = holder
        onBindViewHolder(holder)
    }

    override fun getItemViewType(position: Int): Int {
        loadMore()
        return super.getItemViewType(position)
    }

    final override fun getItemCount(): Int {
        return 1
    }

    /**
     * 如果还有更多数据时调用此方法进行标记。
     */
    internal fun hasMore() {
        hasMore.set(true)
    }

    /**
     * 加载更多数据
     */
    private fun loadMore() {
        if (!::mHolder.isInitialized) return
        if (hasMore.compareAndSet(true, false)) {
            mHolder.itemView.setOnClickListener(null)
            val context = mHolder.itemView.context
            if (context is LifecycleOwner) {
                context.lifecycleScope.launch(Dispatchers.Main) {
                    onLoading()
                    Logger.w("loadMore")
                    onLoadMore()
                }
            }
        }
    }

    /**
     * 没有更多数据时调用此方法更新界面。
     */
    internal fun end() {
        if (!::mHolder.isInitialized) return
        mHolder.itemView.setOnClickListener(null)
        onEnd()
    }

    /**
     * 请求数据出错时调用此方法更新界面。
     * 此方法中添加了出错重试点击监听。
     */
    internal fun error(throwable: Throwable) {
        if (!::mHolder.isInitialized) return
        mHolder.itemView.setOnClickListener {
            // 加载失败后由点击事件触发
            hasMore.set(true)
            loadMore()
        }
        onError(throwable)
    }

    abstract fun onLoading()
    abstract fun onEnd()
    abstract fun onError(throwable: Throwable)
    abstract fun getLayoutId(): Int
    open fun onBindViewHolder(holder: BindingViewHolder<VB>) {}
}
