package com.like.recyclerview.sample.paging3.vo

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

@Entity
class Article : IRecyclerViewItem {
    @Ignore
    override val layoutId: Int = R.layout.item_article

    @Ignore
    override val variableId: Int = BR.item

    // todo 不知道为什么，Room会在插入数据时根据PrimaryKey进行排序。所以不能使用[id]，会造成顺序错乱。
    @PrimaryKey(autoGenerate = true)
    var orderId: Int = 0

    var id: Int? = null

    var title: String? = null

    override fun toString(): String {
        return "Article(id=$id, title=$title)"
    }

}