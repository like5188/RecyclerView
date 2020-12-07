package com.like.recyclerview.model

/**
 * 获取数据失败时的item需要实现的接口
 */
interface IErrorItem : IItem {
    // 如果实现类没有对它进行赋值，那么在RecyclerViewHelper中，失败的时候会自动赋值。
    var throwable: Throwable
}