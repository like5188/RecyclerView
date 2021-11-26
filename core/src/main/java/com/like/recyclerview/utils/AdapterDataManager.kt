package com.like.recyclerview.utils

import com.like.recyclerview.adapter.BaseAdapter
import java.util.*

/**
 * Adapter中的数据管理及界面更新。包括Header、Footer、Item的增删改查、交换位置。
 */
internal class AdapterDataManager<ValueInList> : IAdapterDataManager<ValueInList> {
    private var mAdapter: BaseAdapter<*, ValueInList>? = null
    override val mList = mutableListOf<ValueInList>()

    override fun initAdapterDataManager(adapter: BaseAdapter<*, ValueInList>) {
        mAdapter = adapter
    }

    override fun get(position: Int): ValueInList? {
        if (position < 0 || position >= mList.size) return null
        return mList[position]
    }

    override fun update(position: Int, newData: ValueInList) {
        mList[position] = newData
        mAdapter?.notifyItemChanged(position)
    }

    override fun addToStart(data: ValueInList) {
        add(0, data)
    }

    override fun addToEnd(data: ValueInList) {
        add(mList.size, data)
    }

    override fun add(position: Int, data: ValueInList) {
        data ?: return
        if (position < 0 || position > mList.size) return
        mList.add(position, data)
        mAdapter?.notifyItemInserted(position)
    }

    override fun addAllToStart(list: List<ValueInList>): Boolean {
        return addAll(0, list)
    }

    override fun addAllToEnd(list: List<ValueInList>): Boolean {
        return addAll(mList.size, list)
    }

    override fun addAll(position: Int, list: List<ValueInList>): Boolean {
        if (list.isEmpty()) return false
        if (position < 0 || position > mList.size) return false
        val result = mList.addAll(position, list)
        if (result) {
            mAdapter?.notifyItemRangeInserted(position, list.size)
        }
        return result
    }

    /**
     * List 的 removeAll()方法是没有对应的回调方法的，也就是说调用 List 的removeAll()方法批量删除一些元素，
     * 是不会自动反应在 Adapter 上的。通过仔细观察就能发现，ObservableList 和 Adapter 的 notify 相关的方法，
     * 都是对连续的元素生效，像removeAll()这种，其参数 List 的元素在数据源 List 里面有可能是分散的，
     * 所以不会回调OnListChangedCallback接口里的任何方法。但是我认为，其实可以回调该接口里的onChanged()方法，
     * 直接通知 Adapter 整体刷新就好。所以我现在对removeAll()方法的处理，就是用 for 循环挨个元素remove：
     */
    override fun removeAll(list: List<ValueInList>): Boolean {
        if (list.isEmpty()) return false
        list.reversed().forEach {
            if (!remove(it)) {
                return false
            }
        }
        return true
    }

    override fun remove(data: ValueInList): Boolean {
        data ?: return false
        val position = mList.indexOf(data)
        return remove(position) != null
    }

    override fun remove(position: Int): ValueInList? {
        if (position < 0 || position >= mList.size) return null
        val result = mList.removeAt(position)
        if (result != null) {
            mAdapter?.notifyItemRemoved(position)
        }
        return result
    }

    override fun clear() {
        mList.clear()
        mAdapter?.notifyDataSetChanged()
    }

    override fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return
        Collections.swap(mList, fromPosition, toPosition)
        mAdapter?.notifyItemMoved(fromPosition, toPosition)
    }
}
