package com.like.recyclerview.sample.tree

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class TreeViewModel : ViewModel() {
    private val treeDataSource = TreeDataSource()

    fun getItemsFlow(): Flow<List<TreeNode0>?> {
        return treeDataSource::load.asFlow()
    }
}