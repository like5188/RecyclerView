package com.like.recyclerview.sample.concat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.Footer1Binding
import com.like.recyclerview.sample.model.Footer1
import com.like.recyclerview.viewholder.CommonViewHolder

class FooterAdapter : RecyclerView.Adapter<CommonViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        val binding = DataBindingUtil.inflate<Footer1Binding>(LayoutInflater.from(parent.context),
            R.layout.footer1,
            parent,
            false)
        return CommonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        holder.binding.setVariable(BR.footer1, Footer1(position, "footer $position"))
    }

    override fun getItemCount(): Int {
        return 3
    }

    override fun getItemViewType(position: Int): Int {
        return  R.layout.footer1
    }
}
