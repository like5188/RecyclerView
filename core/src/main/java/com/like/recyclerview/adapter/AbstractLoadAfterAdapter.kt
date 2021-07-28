package com.like.recyclerview.adapter

import androidx.databinding.ViewDataBinding
import com.like.recyclerview.listener.OnLoadMoreListener
import com.like.recyclerview.model.*
import com.like.recyclerview.viewholder.BindingViewHolder

abstract class AbstractLoadAfterAdapter<T : ViewDataBinding> : AbstractItemAdapter<T>() {
    lateinit var onLoadMore: () -> Boolean
    var onLoadMoreListener: OnLoadMoreListener? = null
    private var mLoadMoreStatus: LoadMoreStatus = LoadMoreComplete()

    override fun onBindViewHolder(holder: BindingViewHolder<T>, position: Int) {
        loadMore()
    }

    fun retry() {
        mLoadMoreStatus = LoadMoreComplete()
        loadMore()
    }

    private fun loadMore() {
        if (mLoadMoreStatus is LoadMoreComplete) {
            mLoadMoreStatus = LoadMoreLoading()
            onLoadMoreListener?.onLoading()
            try {
                if (onLoadMore()) {
                    mLoadMoreStatus = LoadMoreEnd()
                    onLoadMoreListener?.onEnd()
                } else {
                    mLoadMoreStatus = LoadMoreComplete()
                    onLoadMoreListener?.onComplete()
                }
            } catch (e: Exception) {
                mLoadMoreStatus = LoadMoreError()
                onLoadMoreListener?.onError(e)
            }
        }
    }

}
