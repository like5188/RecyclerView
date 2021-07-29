package com.like.recyclerview.utils

import androidx.databinding.ObservableArrayList

internal interface IAdapterDataManager<Data> {
    val mList: ObservableArrayList<Data>
    fun get(position: Int): Data
    fun update(position: Int, newData: Data)
    fun addToStart(data: Data)
    fun addToEnd(data: Data)
    fun add(position: Int, data: Data)
    fun addAllToStart(list: List<Data>)
    fun addAllToEnd(list: List<Data>)
    fun addAll(position: Int, list: List<Data>)
    fun removeAll(list: List<Data>)
    fun remove(data: Data)
    fun remove(position: Int)
    fun clear()

    /**
     * 移动item
     * 当item从fromPosition移动到toPosition时，fromPosition和toPosition之间的所有item都会后移，类似于插入。
     *
     * [AdapterDataManager] 没有提供swapItem()方法，是因为Adapter没有提供相关动画。只有notifyItemMoved()这个类似插入动作的动画。
     * <p>
     * 注意：这里没有调用notifyItemRangeChangedFromPosition()或者notifyDataSetChanged()方法更新position，
     * 因为有可能是连续移动item，比如在ItemTouchHelper的onMove()方法中，如果每次调用本方法都更新一下的话，
     * 会导致拖拽的bug，不能连续拖拽多个位置。
     * 所以使用的时候，需要在ItemTouchHelper的clearView()方法中调用notifyDataSetChanged()方法更新position。
     *
     * @param fromPosition  移动的开始位置
     * @param toPosition    移动的结束位置
     */
    fun moveItem(fromPosition: Int, toPosition: Int)
}