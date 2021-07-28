package com.like.recyclerview.sample.concat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ItemBinding
import com.like.recyclerview.sample.model.Item
import com.like.recyclerview.viewholder.CommonViewHolder

class Adapter1 : RecyclerView.Adapter<CommonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val binding = DataBindingUtil.inflate<ItemBinding>(LayoutInflater.from(parent.context),
            R.layout.item,
            parent,
            false)
        return CommonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        holder.binding.setVariable(BR.item, Item(position, "name $position", "des $position"))
    }

    override fun getItemCount(): Int {
        return 20
    }

}
