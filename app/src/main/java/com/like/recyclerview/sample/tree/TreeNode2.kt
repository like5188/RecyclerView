package com.like.recyclerview.sample.tree

import androidx.annotation.DrawableRes
import com.like.recyclerview.ext.tree.BaseTreeNode
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

class TreeNode2(@DrawableRes val icon: Int, val name: String, var count: Int) : BaseTreeNode() {
    override var layoutId: Int = R.layout.tree_item2
    override fun variableId(): Int = BR.treeNode2
}