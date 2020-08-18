package com.like.recyclerview.sample.tree

import android.widget.CheckBox
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.ext.adapter.BaseTreeRecyclerViewAdapter
import com.like.recyclerview.ext.model.BaseTreeNode
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.TreeItem0Binding
import com.like.recyclerview.sample.databinding.TreeItem1Binding
import com.like.recyclerview.sample.databinding.TreeItem2Binding
import com.like.recyclerview.sample.databinding.TreeItem3Binding

class TreeRecyclerViewAdapter : com.like.recyclerview.ext.adapter.BaseTreeRecyclerViewAdapter() {
    override fun onExpand(parent: com.like.recyclerview.ext.model.BaseTreeNode, position: Int, binding: ViewDataBinding): List<com.like.recyclerview.ext.model.BaseTreeNode> {
        val result = mutableListOf<com.like.recyclerview.ext.model.BaseTreeNode>()
        when (parent) {
            is TreeNode0 -> {
                (1..parent.count).forEach {
                    result.add(TreeNode1(R.drawable.extract, "纪委会_$it", 3))
                }
            }
            is TreeNode1 -> {
                (1..parent.count).forEach {
                    result.add(TreeNode2(R.drawable.extract, "信息处_$it", 2))
                }
            }
            is TreeNode2 -> {
                (1..parent.count).forEach {
                    result.add(TreeNode3("", "name_$it", "job_$it"))
                }
            }
        }
        return result
    }

    override fun getCheckBox(binding: ViewDataBinding): CheckBox? {
        return when (binding) {
            is TreeItem0Binding -> binding.cb
            is TreeItem1Binding -> binding.cb
            is TreeItem2Binding -> binding.cb
            is TreeItem3Binding -> binding.cb
            else -> null
        }
    }
}