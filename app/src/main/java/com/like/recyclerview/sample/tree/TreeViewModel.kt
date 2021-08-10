package com.like.recyclerview.sample.tree

import androidx.lifecycle.ViewModel

class TreeViewModel : ViewModel() {
    private val treeNotPagingDataSource = TreeNotPagingDataSource()

    suspend fun getItems(): List<TreeNode0>? {
        return treeNotPagingDataSource.load()
    }
}