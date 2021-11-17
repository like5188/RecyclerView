package com.like.recyclerview.sample.tree

import androidx.lifecycle.ViewModel

class TreeViewModel : ViewModel() {
    private val treeDataSource = TreeDataSource()

    suspend fun getItems(): List<TreeNode0>? {
        return treeDataSource.load()
    }
}