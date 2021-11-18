package com.like.recyclerview.model

/*
 * 使用本库时，RecyclerView 中的数据如果实现这个接口，那么就会自动绑定变量和返回 ItemViewType，如下：
    override fun onBindViewHolder(holder: BindingViewHolder<ItemErrorBinding>, position: Int) {
        holder.binding.setVariable(BR.errorItem, get(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.item_error
    }
 * 如果不实现此接口，那么就需要在 Adapter 中实现：
    override fun onBindViewHolder(holder: BindingViewHolder<VB>, binding: VB, position: Int, item: ValueInList) {
        super.onBindViewHolder(holder, position)
        holder.binding.setVariable(BR.errorItem, get(position))
    }

    override fun getItemViewType(position: Int, item: ValueInList): Int {
        return R.layout.item_error
    }
 */
interface IRecyclerViewItem {

    /**
     * item 所在布局的id。比如：R.layout.xxx
     */
    val layoutId: Int

    /**
     * item 数据对应的变量id。默认为 -1，表示没有变量需要绑定。比如：BR.xxx
     *
     * 如果要在此布局中绑定其它类型的变量，请重写[onBindViewHolder]方法。
     */
    val variableId: Int
        get() = -1

}
