#### 最新版本

模块|RecyclerView
---|---
最新版本|[![Download](https://jitpack.io/v/like5188/RecyclerView.svg)](https://jitpack.io/#like5188/RecyclerView)

## 功能介绍

1、定义了一系列的 Adapter。
    ① BaseAdapter（不分页时使用）、BaseLoadAfterAdapter（分页时使用）、BaseLoadBeforeAdapter（分页时使用）。
    ② BaseAnimationAdapter（不分页时使用）、BaseLoadAfterAnimationAdapter（分页时使用）、BaseLoadBeforeAnimationAdapter（分页时使用）：让item带动画效果。
    ③ BaseAddImageViewAdapter（不分页时使用）：添加照片专用，维护了+号item。
    ④ BaseTreeRecyclerViewAdapter（不分页时使用） + BaseTreeNode：用于树形结构。

2、定义了多种数据类型的接口，用于展示不同类型的item：
    IRecyclerViewItem（RecyclerView中的数据必须实现的接口）、
    IHeader、IItem、IFooter（IRecyclerViewItem的子接口。用于header、item、footer来实现）、
    IErrorItem、IEmptyItem（IItem的子接口。这两个是显示错误页面、空页面时，数据需要实现的接口。）、
    IPinnedItem（IItem的子接口。粘性悬浮标签时要实现的接口）、
    ILoadMore、（加载更多视图需要实现的接口）、ILoadMoreFooter（往后加载更多视图需要实现的接口）、ILoadMoreHeader（往前加载更多视图需要实现的接口）
    BaseTreeNode（IItem的子接口。树形结构时需要实现的接口）

3、WrapGridLayoutManager、WrapLinearLayoutManager、WrapStaggeredGridLayoutManager：解决了IndexOutOfBoundsException的bug。

4、ColorLineItemDecoration（带颜色的分割线）

5、PinnedItemDecoration + IPinnedItem：粘性悬浮标签。
    注意：RecyclerView不能设置padding、margin，否则显示会错乱。
         RecyclerView的外层必须是FrameLayout、RelativeLayout。

6、AdapterDataManager 属于 BaseAdapter，专门用于数据的管理。可以通过它来操作集合数据源，包括 Header、Footer、Item 的增加、删除、更新、查询、交换位置等

7、RecyclerViewHelper：把 RecyclerView 的显示和 com.like.repository.Result 联系起来。用于简化 RecyclerView 的相关操作。

## 使用方法：

1、引用

在Project的gradle中加入：
```groovy
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
```
在Module的gradle中加入：
```groovy
    dependencies {
        implementation 'com.github.like5188:Repository:3.0.2'

        implementation 'com.github.like5188:RecyclerView:版本号'
    }
```