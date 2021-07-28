package com.like.recyclerview.listener

interface OnLoadMoreListener {
    fun onLoading()
    fun onComplete()
    fun onEnd()
    fun onError(throwable: Throwable)
}