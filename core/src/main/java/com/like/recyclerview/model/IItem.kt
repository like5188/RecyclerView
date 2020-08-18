package com.like.recyclerview.model

import com.like.recyclerview.model.IRecyclerViewItem.Companion.DEFAULT_SORT_TAG

/**
 * item必须实现的接口
 */
interface IItem : IRecyclerViewItem {

    /**
     * [IItem] 类型的 item 在 RecyclerView 中的排序依据
     */
    override fun sortTag(): Int {
        return DEFAULT_SORT_TAG
    }
}