package com.like.recyclerview.sample.tree

import com.like.recyclerview.sample.R

class TreeNotPagingDataSource {

    fun load(): List<TreeNode0>? {
        val treeNode1 = TreeNode0(R.drawable.file, "第一人民医院", 8)
        val treeNode2 = TreeNode0(R.drawable.file, "第二人民医院", 10)
        val treeNode3 = TreeNode0(R.drawable.file, "第三人民医院", 6)
        return listOf(treeNode1, treeNode2, treeNode3)
    }
}