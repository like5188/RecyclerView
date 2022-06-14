package com.like.recyclerview.ui.header

import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
import androidx.paging.LoadState
import com.like.recyclerview.adapter.BaseLoadStateAdapter
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemLoadStateBinding
import com.like.recyclerview.viewholder.BindingViewHolder

class HeaderAdapter : BaseLoadStateAdapter<ItemLoadStateBinding>(R.layout.item_load_state) {

    override fun onBindViewHolder(holder: BindingViewHolder<ItemLoadStateBinding>) {
        val context = holder.itemView.context
        holder.binding.apply {
            fl.setBackgroundResource(R.color.recyclerview_bg_white_0)
            pb.indeterminateDrawable.setColorFilter(
                ContextCompat.getColor(context, R.color.recyclerview_bg_gray_0),
                PorterDuff.Mode.MULTIPLY
            )
            tvLoading.apply {
                text = "加载中……"
                setTextColor(ContextCompat.getColor(context, R.color.recyclerview_text_gray_0))
                textSize = 16f
            }
            tvNoMore.apply {
                text = "到顶啦"
                setTextColor(ContextCompat.getColor(context, R.color.recyclerview_text_gray_0))
                textSize = 16f
            }
            tvError.apply {
                text = "加载失败，点击重试！"
                setTextColor(ContextCompat.getColor(context, R.color.recyclerview_text_gray_0))
                textSize = 16f
            }
        }
    }

    override fun onLoadStateChange(preState: LoadState?, curState: LoadState?, holder: BindingViewHolder<ItemLoadStateBinding>) {
        when (curState) {
            is LoadState.Loading -> {
                showLoadingView(holder)
            }
            is LoadState.Error -> {
                showErrorView(holder)
            }
            is LoadState.NotLoading -> {
                if (curState.endOfPaginationReached) {
                    showNoMoreView(holder)
                } else {
                    when (preState) {
                        is LoadState.Loading -> {
                            showLoadingView(holder)
                        }
                        is LoadState.Error -> {
                            showErrorView(holder)
                        }
                        is LoadState.NotLoading -> {
                            if (curState.endOfPaginationReached) {
                                showNoMoreView(holder)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showLoadingView(holder: BindingViewHolder<ItemLoadStateBinding>) {
        holder.binding.llLoading.visibility = View.VISIBLE
        holder.binding.llNoMore.visibility = View.GONE
        holder.binding.tvError.visibility = View.GONE
    }

    private fun showErrorView(holder: BindingViewHolder<ItemLoadStateBinding>) {
        holder.binding.llLoading.visibility = View.GONE
        holder.binding.llNoMore.visibility = View.GONE
        holder.binding.tvError.visibility = View.VISIBLE
    }

    private fun showNoMoreView(holder: BindingViewHolder<ItemLoadStateBinding>) {
        holder.binding.llLoading.visibility = View.GONE
        holder.binding.llNoMore.visibility = View.VISIBLE
        holder.binding.tvError.visibility = View.GONE
    }

}