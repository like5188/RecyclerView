package com.like.recyclerview.listener

interface OnLoadMoreListener {
    fun onLoading()
    fun onComplete()
    fun onError(throwable: Throwable)
}