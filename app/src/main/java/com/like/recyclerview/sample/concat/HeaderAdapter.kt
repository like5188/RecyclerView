package com.like.recyclerview.sample.concat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.Header1Binding
import com.like.recyclerview.sample.model.Header1
import com.like.recyclerview.viewholder.CommonViewHolder

class HeaderAdapter : RecyclerView.Adapter<CommonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val binding = DataBindingUtil.inflate<Header1Binding>(LayoutInflater.from(parent.context),
            R.layout.header1,
            parent,
            false)
        return CommonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        holder.binding.setVariable(BR.header1, Header1(position, "header $position"))
    }

    override fun getItemCount(): Int {
        return 2
    }

}
