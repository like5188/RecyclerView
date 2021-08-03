package com.like.recyclerview.ui.adapter

import android.util.Log
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.ui.BR
import com.like.recyclerview.ui.R
import com.like.recyclerview.ui.databinding.ItemErrorBinding
import com.like.recyclerview.ui.model.ErrorItem
import com.like.recyclerview.viewholder.BindingViewHolder

class ErrorAdapter : AbstractAdapter<ItemErrorBinding, ErrorItem>() {

    override fun onBindViewHolder(holder: BindingViewHolder<ItemErrorBinding>, position: Int) {
        Log.i(
            "ErrorAdapter",
            "onBindViewHolder position=$position bindingAdapterPosition=${holder.bindingAdapterPosition} absoluteAdapterPosition=${holder.absoluteAdapterPosition}"
        )
        holder.binding.setVariable(BR.errorItem, get(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_error
    }

    override fun onError(throwable: Throwable) {
        get(0)?.onError(throwable)
    }
}
