package com.like.recyclerview.utils

import androidx.databinding.ObservableArrayList
import java.util.*

/**
 * Adapter中的数据管理。包括Header、Footer、Item的增删改查、交换位置。
 */
internal class AdapterDataManager<ValueInList> : IAdapterDataManager<ValueInList> {
    override val mList: ObservableArrayList<ValueInList> = ObservableArrayList<ValueInList>()

    override fun get(position: Int): ValueInList? {
        if (position < 0 || position > mList.size) return null
        return mList[position]
    }

    override fun update(position: Int, newData: ValueInList) {
        mList[position] = newData
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
        return mList.addAll(position, list)
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
        return mList.removeAt(position)
    }

    override fun clear() {
        mList.clear()
    }

    override fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return
        if (fromPosition < toPosition) {
            // 循环交换位置是为了避免数据错乱。这里不用notifyItemRangeChanged()，因为这个会导致拖拽的bug。
            for (i in fromPosition until toPosition) {
                Collections.swap(mList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mList, i, i - 1)
            }
        }
    }

}
