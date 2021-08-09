package com.like.recyclerview.sample.tree

import androidx.annotation.DrawableRes
import com.like.recyclerview.ext.tree.BaseTreeNode
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

class TreeNode1(@DrawableRes val icon: Int, val name: String, var count: Int) : BaseTreeNode() {
    override var layoutId: Int = R.layout.tree_item1
    override fun variableId(): Int = BR.treeNode1
}