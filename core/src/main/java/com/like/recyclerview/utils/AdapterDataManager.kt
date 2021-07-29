package com.like.recyclerview.utils

import androidx.databinding.ObservableArrayList
import java.util.*

/**
 * Adapter中的数据管理。包括Header、Footer、Item的增删改查、交换位置。
 */
internal class AdapterDataManager<Data> : IAdapterDataManager<Data> {
    override val mList: ObservableArrayList<Data> = ObservableArrayList<Data>()

    override fun get(position: Int): Data {
        return mList[position]
    }

    override fun update(position: Int, newData: Data) {
        mList[position] = newData
    }

    override fun addToStart(data: Data) {
        add(0, data)
    }

    override fun addToEnd(data: Data) {
        add(mList.size, data)
    }

    override fun add(position: Int, data: Data) {
        data ?: return
        if (position < 0 || position > mList.size) return
        mList.add(position, data)
    }

    override fun addAllToStart(list: List<Data>) {
        addAll(0, list)
    }

    override fun addAllToEnd(list: List<Data>) {
        addAll(mList.size, list)
    }

    override fun addAll(position: Int, list: List<Data>) {
        if (list.isEmpty()) return
        if (position < 0 || position > mList.size) return
        mList.addAll(position, list)
    }

    override fun removeAll(list: List<Data>) {
        if (list.isEmpty()) return
        list.reversed().forEach {
            remove(it)
        }
    }

    override fun remove(data: Data) {
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
