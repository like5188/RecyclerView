package com.like.recyclerview.ext.tree

import androidx.databinding.ObservableBoolean
import com.like.recyclerview.model.IRecyclerViewItem
import java.util.*

/**
 * 树形结构节点必须继承的类。注意，不能重写equals、hashCode这两个方法。因为已经重写过了。
 */
abstract class BaseTreeNode : IRecyclerViewItem {
    /**
     * 节点的唯一标志
     */
    val id = UUID.randomUUID().toString().replace("-", "") + Random().nextLong()
    var isExpanded = false
    var parent: BaseTreeNode? = null
    val isChecked = ObservableBoolean()

    /**
     * 是否是指定parent的孩子
     */
    fun isChild(parent: BaseTreeNode): Boolean {
        return when {
            this.parent == null -> false
            this.parent == parent -> true
            else -> this.parent!!.isChild(parent)
        }
    }

    /**
     * 获取所有父亲
     */
    fun getParents(): List<BaseTreeNode> {
        val parents = mutableListOf<BaseTreeNode>()
        this.parent?.let {
            parents.add(it)
            parents.addAll(it.getParents())
        }
        return parents
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BaseTreeNode

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}