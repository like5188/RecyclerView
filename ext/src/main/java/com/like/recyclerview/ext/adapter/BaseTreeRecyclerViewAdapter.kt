package com.like.recyclerview.ext.adapter

import android.widget.CheckBox
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.ext.model.BaseTreeNode
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.viewholder.CommonViewHolder

/**
 * 树形结构的adapter，不使用分页
 */
abstract class BaseTreeRecyclerViewAdapter : BaseAdapter() {
    init {
//        addOnItemClickListener { holder, position, data -> clickItem(holder.binding, position, data) }
    }

    /**
     * 展开或者收缩item，用于粘性标签点击时调用
     */
    fun clickItem(binding: ViewDataBinding, position: Int, data: IRecyclerViewItem?) {
        if (data is BaseTreeNode) {
            if (data.isExpanded) {
                val children = getAll().filter { it is BaseTreeNode && it.isChild(data) }
                removeAll(children)
                onContract(data, position, binding)
                data.isExpanded = !data.isExpanded
            } else {
                val children = onExpand(data, position, binding)
                if (children.isNotEmpty()) {
                    children.forEach {
                        it.parent = data
                        it.isChecked.set(data.isChecked.get())
                    }
                    addItems(position + 1, children)
                    data.isExpanded = !data.isExpanded
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {
        val checkBox = getCheckBox(holder.binding)
        if (checkBox == null || item == null || item !is BaseTreeNode) {
            return
        }
        // 这里不能用setOnCheckedChangeListener监听，会循环触发
        checkBox.setOnClickListener {
            clickCheckBox(checkBox, item)
        }
    }

    /**
     * 选中操作，用于粘性标签中的CheckBox点击时调用
     */
    fun clickCheckBox(checkBox: CheckBox, item: BaseTreeNode) {
        item.isChecked.set(checkBox.isChecked)
        // 改变孩子的checkBox状态
        val children = getAll().filter { it is BaseTreeNode && it.isChild(item) }
        children.forEach {
            if (it is BaseTreeNode) {
                it.isChecked.set(checkBox.isChecked)
            }
        }
        // 改变父亲的checkBox状态
        val parents = item.getParents()
        if (checkBox.isChecked) {
            parents.forEach {
                it.isChecked.set(checkBox.isChecked)
            }
        } else {
            // 取消选中孩子，并不一定要取消选中父亲，因为父亲有可能有其它选中的孩子
            parents.forEach { parent ->
                val checkedChildren = getAll()
                    .filter { it is BaseTreeNode && it.isChild(parent) && it.isChecked.get() }
                if (checkedChildren.isEmpty()) {
                    parent.isChecked.set(checkBox.isChecked)
                }
            }
        }
    }

    /**
     * 获取已经选中的节点
     */
    fun getCheckedNodes() = getAll()
        .filter { it is BaseTreeNode && it.isChecked.get() }
        .map { it as BaseTreeNode }

    /**
     * 展开parent
     *
     * @return 返回下一层级的数据
     */
    abstract fun onExpand(
        parent: BaseTreeNode,
        position: Int,
        binding: ViewDataBinding
    ): List<BaseTreeNode>

    /**
     * 收缩parent
     */
    open fun onContract(parent: BaseTreeNode, position: Int, binding: ViewDataBinding) {}

    open fun getCheckBox(binding: ViewDataBinding): CheckBox? {
        return null
    }

}