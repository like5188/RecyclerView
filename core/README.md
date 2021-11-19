## 功能介绍

1、定义了一系列的 Adapter。

    ① BaseAdapter（列表视图）、BaseErrorAdapter（错误视图）、BaseLoadMoreAdapter（加载更多视图）。

2、 IRecyclerViewItem（RecyclerView 中的数据如果实现此接口，那么就会自动绑定变量和返回 ItemViewType）

3、WrapGridLayoutManager、WrapLinearLayoutManager、WrapStaggeredGridLayoutManager：解决了IndexOutOfBoundsException的bug。

4、ColorLineItemDecoration（带颜色的分割线）

5、BaseAdapter 新增了很多方法用于数据的管理。可以通过它来操作集合数据源，包括增删改查、交换位置等。

6、OnItemClickListener、OnItemLongClickListener

7、RecyclerView.kt（用于RecyclerView的一些常见操作及数据绑定的扩展工具类）

8、BindingViewHolder（通用的结合dataBinding的RecyclerView的ViewHolder）
