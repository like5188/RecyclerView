package com.like.recyclerview.sample.concat

import android.util.Log
import com.like.recyclerview.adapter.AbstractFooterAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.FooterBinding
import com.like.recyclerview.sample.model.Footer
import com.like.recyclerview.viewholder.BindingViewHolder

class FooterAdapter(onLoadAfter: () -> Unit) : AbstractFooterAdapter<FooterBinding, Footer>(onLoadAfter) {

    override fun onBindViewHolder(holder: BindingViewHolder<FooterBinding>, position: Int) {
        Log.i("FooterAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}")
        super.onBindViewHolder(holder, position)
        holder.binding.setVariable(BR.footer, mList[position])
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.footer
    }

}
