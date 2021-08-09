package com.like.recyclerview.sample.tree

import androidx.annotation.DrawableRes
import com.like.recyclerview.ext.pinned.IPinnedItem
import com.like.recyclerview.ext.tree.BaseTreeNode
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

class TreeNode0(@DrawableRes val icon: Int, val name: String, var count: Int) : BaseTreeNode(), IPinnedItem {
    override val layoutId: Int = R.layout.tree_item0
    override val variableId: Int = BR.treeNode0
}