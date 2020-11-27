package com.like.recyclerview.model

/**
 * 使用本库时，RecyclerView 中的数据必须实现这个接口或者其子接口。
 */
interface IRecyclerViewItem {
    companion object {
        const val DEFAULT_SORT_TAG = 0
        const val INVALID_VARIABLE_ID = -1
    }

    /**
     * item 所在布局的id。比如：R.layout.xxx
     */
    val layoutId: Int

    /**
     * item 数据对应的变量id。默认为 [INVALID_VARIABLE_ID]。比如：BR.xxx
     *
     * 如果要在此布局中绑定其它类型的变量，请重写[com.like.recyclerview.adapter.BaseAdapter.onBindViewHolder]方法。
     */
    fun variableId(): Int {
        return INVALID_VARIABLE_ID
    }

    /**
     * item 在 RecyclerView 中的排序标签。
     * 注意：[IHeader]、[IItem]、[IFooter] 这三种类型的 item 是分别比较的。因为它们本身的顺序是 [IHeader]->[IItem]->[IFooter]
     */
    fun sortTag(): Int
}