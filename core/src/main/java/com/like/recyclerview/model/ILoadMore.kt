package com.like.recyclerview.model

/**
 * 加载更多视图需要实现的接口
 */
interface ILoadMore {
    fun onLoading()
    fun onEnd()
    fun onError()
}