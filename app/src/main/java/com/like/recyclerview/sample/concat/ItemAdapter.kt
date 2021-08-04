package com.like.recyclerview.sample.concat

import androidx.databinding.ViewDataBinding
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.Item1Binding
import com.like.recyclerview.sample.databinding.Item2Binding
import com.like.recyclerview.sample.model.Item1
import com.like.recyclerview.sample.model.Item2
import com.like.recyclerview.viewholder.BindingViewHolder

class ItemAdapter : AbstractAdapter<ViewDataBinding, Any>() {

    override fun onBindViewHolder(holder: BindingViewHolder<ViewDataBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val binding = holder.binding
        val data = get(position)
        when (binding) {
            is Item1Binding -> {
                binding.setVariable(BR.item1, data)
            }
            is Item2Binding -> {
                binding.setVariable(BR.item2, data)
            }
        }
    }

    override fun getLayoutId(position: Int): Int {
        return when (get(position)) {
            is Item1 -> R.layout.item1
            is Item2 -> R.layout.item2
            else -> -1
        }
    }

}
