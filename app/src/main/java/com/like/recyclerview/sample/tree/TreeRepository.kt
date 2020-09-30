package com.like.recyclerview.sample.tree

class TreeRepository(
    private val treeNotPagingDataSource: TreeNotPagingDataSource
) {
    fun getResult() = treeNotPagingDataSource.result()
}