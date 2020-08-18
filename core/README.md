## 功能介绍

1、定义了一系列的 Adapter。
    ① BaseAdapter（不分页时使用）、BaseLoadAfterAdapter（分页时使用）、BaseLoadBeforeAdapter（分页时使用）。

2、定义了多种数据类型的接口，用于展示不同类型的item：
    IRecyclerViewItem（RecyclerView中的数据必须实现的接口）、
    IHeader、IItem、IFooter（IRecyclerViewItem的子接口。用于header、item、footer来实现）、
    IErrorItem、IEmptyItem（IItem的子接口。这两个是显示错误页面、空页面时，数据需要实现的接口。）、
    ILoadMore、（加载更多视图需要实现的接口）、ILoadMoreFooter（往后加载更多视图需要实现的接口）、ILoadMoreHeader（往前加载更多视图需要实现的接口）

3、WrapGridLayoutManager、WrapLinearLayoutManager、WrapStaggeredGridLayoutManager：解决了IndexOutOfBoundsException的bug。

4、ColorLineItemDecoration（带颜色的分割线）

5、AdapterDataManager 属于 BaseAdapter，专门用于数据的管理。可以通过它来操作集合数据源，包括 Header、Footer、Item 的增加、删除、更新、查询、交换位置等