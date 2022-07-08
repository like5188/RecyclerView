package com.like.recyclerview.sample.tree

import android.widget.CheckBox
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.like.recyclerview.ext.tree.AbstractTreeRecyclerViewAdapter
import com.like.recyclerview.ext.tree.BaseTreeNode
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.TreeItem0Binding
import com.like.recyclerview.sample.databinding.TreeItem1Binding
import com.like.recyclerview.sample.databinding.TreeItem2Binding
import com.like.recyclerview.sample.databinding.TreeItem3Binding

class TreeRecyclerViewAdapter : AbstractTreeRecyclerViewAdapter<ViewDataBinding>(
    object : DiffUtil.ItemCallback<BaseTreeNode>() {
        override fun areItemsTheSame(oldItem: BaseTreeNode, newItem: BaseTreeNode): Boolean {
            return if (oldItem is TreeNode0 && newItem is TreeNode0) {
                oldItem.id == newItem.id
            } else if (oldItem is TreeNode1 && newItem is TreeNode1) {
                oldItem.id == newItem.id
            } else if (oldItem is TreeNode2 && newItem is TreeNode2) {
                oldItem.id == newItem.id
            } else if (oldItem is TreeNode3 && newItem is TreeNode3) {
                oldItem.id == newItem.id
            } else {
                false
            }
        }

        override fun areContentsTheSame(oldItem: BaseTreeNode, newItem: BaseTreeNode): Boolean {
            return false
        }
    }
) {
    override fun onExpand(parent: BaseTreeNode, position: Int, binding: ViewDataBinding): List<BaseTreeNode> {
        val result = mutableListOf<BaseTreeNode>()
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