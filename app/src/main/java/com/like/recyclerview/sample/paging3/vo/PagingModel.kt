package com.like.recyclerview.sample.paging3.vo

data class PagingModel<T>(
    val curPage: Int,
    val size: Int,
    val pageCount: Int,
    val total: Int,
    val datas: List<T>?
)