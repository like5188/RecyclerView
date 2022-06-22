package com.like.recyclerview.sample.paging3.data.model

data class PagingModel<T>(
    val nextKey: Int? = null,
    val prevKey: Int? = null,
    val curPage: Int,
    val datas: List<T>?
)