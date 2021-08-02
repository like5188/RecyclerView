package com.like.recyclerview.utils

import androidx.databinding.ObservableArrayList
import java.util.*

/**
 * Adapter中的数据管理。包括Header、Footer、Item的增删改查、交换位置。
 */
internal class AdapterDataManager<ValueInList> : IAdapterDataManager<ValueInList> {
    override val mList: ObservableArrayList<ValueInList> = ObservableArrayList<ValueInList>()

    override fun get(position: Int): ValueInList {
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

    override fun addAllToStart(list: List<ValueInList>) {
        addAll(0, list)
    }

    override fun addAllToEnd(list: List<ValueInList>) {
        addAll(mList.size, list)
    }

    override fun addAll(position: Int, list: List<ValueInList>) {
        if (list.isEmpty()) return
        if (position < 0 || position > mList.size) return
        mList.addAll(position, list)
    }

    override fun removeAll(list: List<ValueInList>) {
        if (list.isEmpty()) return
        list.reversed().forEach {
            remove(it)
        }
    }

    override fun remove(data: ValueInList) {
        data ?: return
        val position = mList.indexOf(data)
        remove(position)
    }

    override fun remove(position: Int) {
        if (position < 0 || position >= mList.size) return
        mList.removeAt(position)
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
