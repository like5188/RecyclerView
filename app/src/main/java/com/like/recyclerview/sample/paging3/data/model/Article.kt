package com.like.recyclerview.sample.paging3.data.model

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

    @PrimaryKey
    var id: Int? = null

    var title: String? = null

    override fun toString(): String {
        return "Article(id=$id, title=$title)"
    }

}