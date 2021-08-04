package com.like.recyclerview.sample.concat

import android.util.Log
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.Header1Binding
import com.like.recyclerview.sample.databinding.Header2Binding
import com.like.recyclerview.sample.model.Header1
import com.like.recyclerview.sample.model.Header2
import com.like.recyclerview.viewholder.BindingViewHolder

class HeaderAdapter : AbstractAdapter<ViewDataBinding, Any>() {

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) {
        Log.v(
            "HeaderAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}"
        )
        val binding = holder.binding
        val data = get(position)
        when (binding) {
            is Header1Binding -> {
                binding.setVariable(BR.header1, data)
            }
            is Header2Binding -> {
                binding.setVariable(BR.header2, data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (get(position)) {
            is Header1 -> R.layout.header1
            is Header2 -> R.layout.header2
            else -> -1
        }
    }

}
