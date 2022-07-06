package com.like.recyclerview.sample.paging3.vo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

/**
 * @Entity的详细文档：https://www.jianshu.com/p/66a586a6fbe0
 */
class BannerInfo : IRecyclerViewItem {
    @Ignore
    override val layoutId: Int = R.layout.item_banner

    @Ignore
    override val variableId: Int = BR.item

    var banners: List<Banner>? = null

    @Entity
    class Banner {
        @PrimaryKey
        var id: Int? = null

        var imagePath: String? = null

        override fun toString(): String {
            return "Banner(id=$id)"
        }
    }

    override fun toString(): String {
        return "BannerInfo(banners=$banners)"
    }

}