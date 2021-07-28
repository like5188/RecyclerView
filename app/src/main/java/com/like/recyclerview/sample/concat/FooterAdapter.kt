package com.like.recyclerview.sample.concat

import android.util.Log
import com.like.recyclerview.adapter.AbstractLoadAfterAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.Footer1Binding
import com.like.recyclerview.sample.model.Footer1
import com.like.recyclerview.viewholder.BindingViewHolder

class FooterAdapter : AbstractLoadAfterAdapter<Footer1Binding>() {

    override fun onBindViewHolder(holder: BindingViewHolder<Footer1Binding>, position: Int) {
        Log.e("FooterAdapter",
            "position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}")
        super.onBindViewHolder(holder, position)
        holder.binding.setVariable(BR.footer1, Footer1(position, "footer $position"))
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.footer1
    }

}
