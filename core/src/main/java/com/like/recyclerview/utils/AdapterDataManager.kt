package com.like.recyclerview.utils

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import com.like.recyclerview.model.*
import java.util.*

/**
 * Adapter中的数据管理。包括Header、Footer、Item的增加、删除、更新、查询、交换位置等。
 */
class AdapterDataManager {
    private val mList = ObservableArrayList<IRecyclerViewItem>()

    internal fun addOnListChangedCallback(listener: ObservableList.OnListChangedCallback<ObservableArrayList<IRecyclerViewItem>>) {
        mList.addOnListChangedCallback(listener)
    }

    fun getAll(): MutableList<IRecyclerViewItem> = mList
    fun getSize() = getAll().size
    fun get(position: Int) = if (position < getSize() && position >= 0) getAll()[position] else null
    fun getEmptyItem(): IEmptyItem? = if (isEmptyItemShow()) get(0) as IEmptyItem else null
    fun getErrorItem(): IErrorItem? = if (isErrorItemShow()) get(0) as IErrorItem else null

    fun getHeaders(): List<IHeader> {
        if (!hasHeader()) {
            return emptyList()
        }
        val result = mutableListOf<IHeader>()
        getAll().forEach {
            if (it is IHeader) {
                result.add(it)
            }
        }
        return result
    }

    fun getFooters(): List<IFooter> {
        if (!hasFooter()) {
            return emptyList()
        }
        val result = mutableListOf<IFooter>()
        getAll().forEach {
            if (it is IFooter) {
                result.add(it)
            }
        }
        return result
    }

    fun getItems(): List<IItem> {
        if (!hasItem()) {
            return emptyList()
        }
        val result = mutableListOf<IItem>()
        getAll().forEach {
            if (it is IItem) {
                result.add(it)
            }
        }
        return result
    }

    fun getHeader(headerPosition: Int): IHeader? {
        val headers = getHeaders()
        if (headers.isEmpty()) {
            return null
        }
        return if (headerPosition < headers.size && headerPosition >= 0) headers[headerPosition] else null
    }

    fun getItem(itemPosition: Int): IItem? {
        val items = getItems()
        if (items.isEmpty()) {
            return null
        }
        return if (itemPosition < items.size && itemPosition >= 0) items[itemPosition] else null
    }

    fun getFooter(footerPosition: Int): IFooter? {
        val footers = getFooters()
        if (footers.isEmpty()) {
            return null
        }
        return if (footerPosition < footers.size && footerPosition >= 0) footers[footerPosition] else null
    }

    fun getHeaderCount() = getAll().count { it is IHeader }
    fun getFooterCount() = getAll().count { it is IFooter }
    fun getItemCount() = getAll().count { it is IItem }

    fun isEmptyItemShow() = getSize() == 1 && get(0) is IEmptyItem
    fun isErrorItemShow() = getSize() == 1 && get(0) is IErrorItem
    fun isHeader(position: Int) = get(position) is IHeader
    fun isFooter(position: Int) = get(position) is IFooter
    fun isItem(position: Int) = get(position) is IItem

    fun hasHeader() = getHeaderCount() > 0
    fun hasFooter() = getFooterCount() > 0
    fun hasItem() = getItemCount() > 0

    fun containsHeader(layoutId: Int): Boolean {
        if (layoutId < 0) return false
        if (!hasHeader()) return false
        return getAll().any { it is IHeader && it.layoutId == layoutId }
    }

    fun containsFooter(layoutId: Int): Boolean {
        if (layoutId < 0) return false
        if (!hasFooter()) return false
        return getAll().any { it is IFooter && it.layoutId == layoutId }
    }

    fun containsItem(layoutId: Int): Boolean {
        if (layoutId < 0) return false
        if (!hasItem()) return false
        return getAll().any { it is IItem && it.layoutId == layoutId }
    }

    fun updateHeader(position: Int, newHeader: IHeader) {
        if (isHeader(position)) {
            getAll()[position] = newHeader
        }
    }

    fun updateFooter(position: Int, newFooter: IFooter) {
        if (isFooter(position)) {
            getAll()[position] = newFooter
        }
    }

    fun updateItem(position: Int, newItem: IItem) {
        if (isItem(position)) {
            getAll()[position] = newItem
        }
    }

    fun setEmptyItem(emptyItem: IEmptyItem) {
        clear()
        addItemToStart(emptyItem)
    }

    fun setErrorItem(errorItem: IErrorItem) {
        clear()
        addItemToStart(errorItem)
    }

    /**
     * 添加footer到footers的开始位置
     *
     */
    fun addFooterToStart(footer: IFooter?) {
        addFooter(0, footer)
    }

    /**
     * 添加footer到footers的末尾
     *
     */
    fun addFooterToEnd(footer: IFooter?) {
        addFooter(getFooterCount(), footer)
    }

    /**
     * 添加footer
     *
     * @param positionInFooters 在footers中的位置
     */
    fun addFooter(positionInFooters: Int, footer: IFooter?) {
        add(convertFooterPositionToListPosition(positionInFooters), footer)
    }

    /**
     * 添加footers到footers的开始位置
     *
     */
    fun addFootersToStart(footers: List<IFooter>?) {
        addFooters(0, footers)
    }

    /**
     * 添加footers到footers的末尾
     *
     */
    fun addFootersToEnd(footers: List<IFooter>?) {
        addFooters(getFooterCount(), footers)
    }

    /**
     * 添加footers
     *
     * @param positionInFooters 在footers中的位置
     */
    fun addFooters(positionInFooters: Int, footers: List<IFooter>?) {
        addAll(convertFooterPositionToListPosition(positionInFooters), footers)
    }

    /**
     * 添加header到headers的开始位置
     *
     */
    fun addHeaderToStart(header: IHeader?) {
        addHeader(0, header)
    }

    /**
     * 添加header到headers的末尾
     *
     */
    fun addHeaderToEnd(header: IHeader?) {
        addHeader(getHeaderCount(), header)
    }

    /**
     * 添加header
     *
     * @param positionInHeaders 在headers中的位置
     */
    fun addHeader(positionInHeaders: Int, header: IHeader?) {
        add(convertHeaderPositionToListPosition(positionInHeaders), header)
    }

    /**
     * 添加headers到headers的开始位置
     *
     */
    fun addHeadersToStart(headers: List<IHeader>?) {
        addHeaders(0, headers)
    }

    /**
     * 添加headers到headers的末尾
     *
     */
    fun addHeadersToEnd(headers: List<IHeader>?) {
        addHeaders(getHeaderCount(), headers)
    }

    /**
     * 添加headers
     *
     * @param positionInHeaders 在headers中的位置
     */
    fun addHeaders(positionInHeaders: Int, headers: List<IHeader>?) {
        addAll(convertHeaderPositionToListPosition(positionInHeaders), headers)
    }

    /**
     * 添加item到items的开始位置
     *
     */
    fun addItemToStart(item: IItem?) {
        addItem(0, item)
    }

    /**
     * 添加item到items的末尾
     *
     */
    fun addItemToEnd(item: IItem?) {
        addItem(getItemCount(), item)
    }

    /**
     * 添加item
     *
     * @param positionInItems 在items中的位置
     */
    fun addItem(positionInItems: Int, item: IItem?) {
        add(convertItemPositionToListPosition(positionInItems), item)
    }

    /**
     * 添加items到items的开始位置
     *
     */
    fun addItemsToStart(items: List<IItem>?) {
        addItems(0, items)
    }

    /**
     * 添加items到items的末尾
     *
     */
    fun addItemsToEnd(items: List<IItem>?) {
        addItems(getItemCount(), items)
    }

    /**
     * 添加items
     *
     * @param positionInItems 在items中的位置
     */
    fun addItems(positionInItems: Int, items: List<IItem>?) {
        addAll(convertItemPositionToListPosition(positionInItems), items)
    }

    /**
     * 清除所有数据，并添加新的数据集合。
     *
     * 注意：此方法传入的list会自动重新排序，规则为：
     * [IHeader]->[IItem]->[IFooter]，其中这三种类型的集合内部排序是根据 [IRecyclerViewItem.sortTag] 标记来确定的。
     */
    fun clearAndAddAll(list: List<IRecyclerViewItem>?) {
        clear()
        // 为了防止header、item、footer顺序错乱导致出错。
        if (list.isNullOrEmpty()) return
        val sortedList = mutableListOf<IRecyclerViewItem>()
        val headers = mutableListOf<IHeader>()
        val footers = mutableListOf<IFooter>()
        val items = mutableListOf<IItem>()
        list.forEach {
            when (it) {
                is IHeader -> headers.add(it)
                is IFooter -> footers.add(it)
                is IItem -> items.add(it)
            }
        }
        sortedList.addAll(headers.sortedBy { it.sortTag() })
        sortedList.addAll(items.sortedBy { it.sortTag() })
        sortedList.addAll(footers.sortedBy { it.sortTag() })
        addAll(0, sortedList)
    }

    /**
     * 添加数据到指定位置
     *
     * @param position 在整个List中的位置
     * @param data
     */
    private fun add(position: Int, data: IRecyclerViewItem?) {
        data ?: return
        if (position < 0 || position > getSize()) return
        if (isEmptyItemShow() || isErrorItemShow()) clear()
        getAll().add(position, data)
    }

    /**
     * 添加数据集合到指定位置
     *
     * @param position 在整个List中的位置
     * @param list
     */
    private fun addAll(position: Int, list: List<IRecyclerViewItem>?) {
        if (list.isNullOrEmpty()) return
        if (position < 0 || position > getSize()) return
        if (isEmptyItemShow() || isErrorItemShow()) clear()
        getAll().addAll(position, list)
    }

    fun removeAll(list: List<IRecyclerViewItem>?) {
        if (list.isNullOrEmpty()) return
        list.reversed().forEach {
            remove(it)
        }
    }

    fun remove(data: IRecyclerViewItem?) {
        data ?: return
        val position = getAll().indexOf(data)
        remove(position)
    }

    fun remove(position: Int) {
        if (position < 0 || position >= getSize()) return
        getAll().removeAt(position)
    }

    fun clearHeaders() {
        if (!hasHeader()) return
        getAll().removeAll { it is IHeader }
    }

    fun clearFooters() {
        if (!hasFooter()) return
        getAll().removeAll { it is IFooter }
    }

    fun clearItems() {
        if (!hasItem()) return
        getAll().removeAll { it is IItem }
    }

    fun clear() {
        if (getSize() > 0) {
            getAll().clear()
        }
    }

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
    fun moveItem(fromPosition: Int, toPosition: Int) {
        if (fromPosition == toPosition) return
        if (fromPosition < toPosition) {
            // 循环交换位置是为了避免数据错乱。这里不用notifyItemRangeChanged()，因为这个会导致拖拽的bug。
            for (i in fromPosition until toPosition) {
                Collections.swap(getAll(), i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(getAll(), i, i - 1)
            }
        }
    }

    private fun convertFooterPositionToListPosition(positionInFooters: Int): Int {
        val footerCount = getFooterCount()
        return getSize() - footerCount + when {
            positionInFooters < 0 -> 0
            positionInFooters > footerCount -> footerCount
            else -> positionInFooters
        }
    }

    private fun convertHeaderPositionToListPosition(positionInHeaders: Int): Int {
        val headerCount = getHeaderCount()
        return when {
            positionInHeaders < 0 -> 0
            positionInHeaders > headerCount -> headerCount
            else -> positionInHeaders
        }
    }

    private fun convertItemPositionToListPosition(positionInItems: Int): Int {
        val itemCount = getItemCount()
        return getHeaderCount() + when {
            positionInItems < 0 -> 0
            positionInItems > itemCount -> itemCount
            else -> positionInItems
        }
    }

}