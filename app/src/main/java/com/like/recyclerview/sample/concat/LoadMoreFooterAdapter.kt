package com.like.recyclerview.sample.concat

import android.util.Log
import com.like.recyclerview.adapter.AbstractLoadMoreFooterAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.FooterBinding
import com.like.recyclerview.sample.model.Footer
import com.like.recyclerview.viewholder.BindingViewHolder

class LoadMoreFooterAdapter(onLoad: () -> Unit) : AbstractLoadMoreFooterAdapter<FooterBinding, Footer>(onLoad) {
    private lateinit var mFooter: Footer

    override fun onBindViewHolder(holder: BindingViewHolder<FooterBinding>, position: Int) {
        Log.i("FooterAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}")
        mFooter = mList[position]
        super.onBindViewHolder(holder, position)
        holder.binding.setVariable(BR.footer, mFooter)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.footer
    }

    override fun onComplete() {
        super.onComplete()
        mFooter.name.set("onLoading")
    }

    override fun onEnd() {
        super.onEnd()
        mFooter.name.set("onEnd")
    }

    override fun onError(throwable: Throwable) {
        super.onError(throwable)
        mFooter.name.set("onError ${throwable.message} 点击重试")
    }

}
