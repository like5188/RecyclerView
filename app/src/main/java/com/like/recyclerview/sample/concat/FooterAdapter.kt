package com.like.recyclerview.sample.concat

import android.util.Log
import com.like.recyclerview.adapter.AbstractFooterAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.FooterBinding
import com.like.recyclerview.sample.model.Footer
import com.like.recyclerview.viewholder.BindingViewHolder

class FooterAdapter(private val onLoad: () -> Unit) : AbstractFooterAdapter<FooterBinding, Footer>(onLoad) {
    private lateinit var mFooter: Footer
    private lateinit var mBinding: FooterBinding

    override fun onBindViewHolder(holder: BindingViewHolder<FooterBinding>, position: Int) {
        Log.i("FooterAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}")
        mFooter = mList[position]
        mBinding = holder.binding
        super.onBindViewHolder(holder, position)
        holder.binding.setVariable(BR.footer, mFooter)
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.footer
    }

    fun onLoading() {
        mFooter.name = "onLoading"
        mBinding.root.setOnClickListener(null)
    }

    fun onEnd() {
        mFooter.name = "onEnd"
        mBinding.root.setOnClickListener(null)
    }

    fun onError(throwable: Throwable) {
        mFooter.name = "onError ${throwable.message}"
        mBinding.root.setOnClickListener {
            trigger()
            onLoad()
        }
    }

}
