package com.like.recyclerview.sample.paging3.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ItemBannerBinding
import com.like.recyclerview.sample.databinding.ItemHeaderBinding
import com.like.recyclerview.sample.databinding.ItemTopArticleBinding
import com.like.recyclerview.sample.paging3.data.model.BannerInfo
import com.like.recyclerview.sample.paging3.data.model.TopArticle
import com.like.recyclerview.viewholder.BindingViewHolder

class HeaderAdapter : LoadStateAdapter<BindingViewHolder<ItemHeaderBinding>>() {
    var bannerInfo: BannerInfo? = null
        set(value) {
            field = value
            notifyItemChanged(0)
        }
    var topArticleList: List<TopArticle>? = null
        set(value) {
            field = value
            if (!value.isNullOrEmpty()) {
                notifyItemRangeChanged(1, value.size)
            }
        }
    private val bannerAdapter by lazy {
        object : RecyclerView.Adapter<BindingViewHolder<ItemBannerBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ItemBannerBinding> {
                return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_banner, parent, false))
            }

            override fun onBindViewHolder(holder: BindingViewHolder<ItemBannerBinding>, position: Int) {
                holder.binding.item = bannerInfo?.banners?.get(position)
            }

            override fun getItemCount(): Int {
                return bannerInfo?.banners?.size ?: 0
            }

        }
    }
    private val topArticleAdapter by lazy {
        object : RecyclerView.Adapter<BindingViewHolder<ItemTopArticleBinding>>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<ItemTopArticleBinding> {
                return BindingViewHolder(
                    DataBindingUtil.inflate(
                        LayoutInflater.from(parent.context),
                        R.layout.item_top_article,
                        parent,
                        false
                    )
                )
            }

            override fun onBindViewHolder(holder: BindingViewHolder<ItemTopArticleBinding>, position: Int) {
                holder.binding.item = topArticleList?.get(position)
            }

            override fun getItemCount(): Int {
                return topArticleList?.size ?: 0
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BindingViewHolder<ItemHeaderBinding> {
        return BindingViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_header, parent, false))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ItemHeaderBinding>, loadState: LoadState) {
        holder.binding.apply {
            vp.adapter = bannerAdapter
            rv.layoutManager = LinearLayoutManager(rv.context)
            rv.adapter = topArticleAdapter
        }
    }

    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return true
    }

}