package com.like.recyclerview.ext.tree

import android.widget.CheckBox
import androidx.databinding.ViewDataBinding
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.viewholder.BindingViewHolder

/**
 * 树形结构的adapter，不使用分页
 */
abstract class AbstractTreeRecyclerViewAdapter<VB : ViewDataBinding> : BaseAdapter<VB, BaseTreeNode>() {
    init {
        addOnItemClickListener {
            val binding = it.binding
            val position = it.bindingAdapterPosition
            val item = get(position)
            clickItem(binding, position, item)
        }
    }

    /**
     * 展开或者收缩item，用于粘性标签点击时调用
     */
    fun clickItem(binding: ViewDataBinding, position: Int, item: BaseTreeNode?) {
        item ?: return
        if (item.isExpanded) {
            val children = mList.filter { it.isChild(item) }
            removeAll(children)
            onContract(item, position, binding)
            item.isExpanded = !item.isExpanded
        } else {
            val children = onExpand(item, position, binding)
            if (children.isNotEmpty()) {
                children.forEach {
                    it.parent = item
                    it.isChecked.set(item.isChecked.get())
                }
                addAll(position + 1, children)
                item.isExpanded = !item.isExpanded
            }
        }
    }

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, item: BaseTreeNode) {
        super.onBindViewHolder(holder, item)
        val checkBox = getCheckBox(holder.binding) ?: return
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
        val children = mList.filter { it.isChild(item) }
        children.forEach {
            it.isChecked.set(checkBox.isChecked)
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
                val checkedChildren = mList
                    .filter { it.isChild(parent) && it.isChecked.get() }
                if (checkedChildren.isEmpty()) {
                    parent.isChecked.set(checkBox.isChecked)
                }
            }
        }
    }

    /**
     * 获取已经选中的节点
     */
    fun getCheckedNodes() = mList
        .filter { it.isChecked.get() }
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