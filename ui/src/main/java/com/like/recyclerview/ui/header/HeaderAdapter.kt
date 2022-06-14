package com.like.recyclerview.ui.header

import android.graphics.PorterDuff
import android.view.View
import androidx.core.content.ContextCompat
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

    override fun onLoading(holder: BindingViewHolder<ItemLoadStateBinding>) {
        holder.binding.llLoading.visibility = View.VISIBLE
        holder.binding.llNoMore.visibility = View.GONE
        holder.binding.tvError.visibility = View.GONE
    }

    override fun onNoMore(holder: BindingViewHolder<ItemLoadStateBinding>) {
        holder.binding.llLoading.visibility = View.GONE
        holder.binding.llNoMore.visibility = View.VISIBLE
        holder.binding.tvError.visibility = View.GONE
    }

    override fun onError(holder: BindingViewHolder<ItemLoadStateBinding>, throwable: Throwable) {
        holder.binding.llLoading.visibility = View.GONE
        holder.binding.llNoMore.visibility = View.GONE
        holder.binding.tvError.visibility = View.VISIBLE
    }

}