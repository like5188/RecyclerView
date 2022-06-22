package com.like.recyclerview.sample.paging3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ItemBannerBinding
import com.like.recyclerview.sample.paging3.data.model.BannerInfo
import com.like.recyclerview.viewholder.BindingViewHolder

class BannerAdapter : LoadStateAdapter<BindingViewHolder<ItemBannerBinding>>() {
    var bannerInfo: BannerInfo? = null
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BindingViewHolder<ItemBannerBinding> {
        return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_banner, parent, false))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemBannerBinding>, loadState: LoadState) {
        holder.binding.item = bannerInfo
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return true
    }

}