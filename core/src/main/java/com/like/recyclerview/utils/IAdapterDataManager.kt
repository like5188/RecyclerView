package com.like.recyclerview.utils

import androidx.databinding.ObservableArrayList

internal interface IAdapterDataManager<ValueInList> {
    val mList: ObservableArrayList<ValueInList>
    fun get(position: Int): ValueInList?
    fun update(position: Int, newData: ValueInList)
    fun addToStart(data: ValueInList)
    fun addToEnd(data: ValueInList)
    fun add(position: Int, data: ValueInList)
    fun addAllToStart(list: List<ValueInList>): Boolean
    fun addAllToEnd(list: List<ValueInList>): Boolean
    fun addAll(position: Int, list: List<ValueInList>): Boolean
    fun removeAll(list: List<ValueInList>): Boolean
    fun remove(data: ValueInList): Boolean
    fun remove(position: Int): ValueInList?
    fun clear()
}