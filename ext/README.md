## 功能介绍

1、定义了一系列的 Adapter。

    ① BaseAnimationAdapter（不分页时使用）、BaseLoadAfterAnimationAdapter（分页时使用）、BaseLoadBeforeAnimationAdapter（分页时使用）：让item带动画效果。

    ② BaseAddImageViewAdapter（不分页时使用）：添加照片专用，维护了“+”号item。

    ③ BaseTreeRecyclerViewAdapter（不分页时使用）：用于树形结构。

2、定义了多种数据类型的接口，用于展示不同类型的item：

    IPinnedItem（IItem的子接口。固定悬浮功能要实现的接口）

    BaseTreeNode（IItem的子接口。树形结构时要实现的接口）

3、BaseTreeRecyclerViewAdapter + BaseTreeNode：实现树形结构。

4、PinnedItemDecoration + IPinnedItem：实现固定悬浮功能。

    注意：RecyclerView不能设置padding、margin，否则显示会错乱。

         RecyclerView的外层必须是FrameLayout、RelativeLayout。