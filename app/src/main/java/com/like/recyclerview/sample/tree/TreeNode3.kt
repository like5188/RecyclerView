package com.like.recyclerview.sample.tree

import com.like.recyclerview.ext.tree.BaseTreeNode
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

class TreeNode3(val header: String, val name: String, var job: String) : BaseTreeNode() {
    override var layoutId: Int = R.layout.tree_item3
    override val variableId: Int = BR.treeNode3
}