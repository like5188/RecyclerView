package com.like.recyclerview.model

import com.like.recyclerview.model.IRecyclerViewItem.Companion.DEFAULT_SORT_TAG

/**
 * header必须实现的接口。插入的[IItem]数据始终在[IHeader]下面
 */
interface IHeader : IRecyclerViewItem {

    /**
     * [IHeader] 类型的 item 在 RecyclerView 中的排序依据
     */
    override fun sortTag(): Int {
        return DEFAULT_SORT_TAG
    }
}