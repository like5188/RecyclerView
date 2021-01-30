package com.like.recyclerview.utils

import androidx.databinding.ObservableArrayList
import com.like.recyclerview.model.*
import java.util.*

/**
 * Adapter中的数据管理。包括Header、Footer、Item的增删改查、交换位置。
 */
internal class AdapterDataManager : IAdapterDataManager {
    private val mList = ObservableArrayList<IRecyclerViewItem>()
    override fun getAll(): List<IRecyclerViewItem> = mList
    override fun get(position: Int): IRecyclerViewItem? = if (position < mList.size && position >= 0) mList[position] else null
    override fun getEmptyItem(): IEmptyItem? = if (isEmptyItemShow()) get(0) as IEmptyItem else null
    override fun getErrorItem(): IErrorItem? = if (isErrorItemShow()) get(0) as IErrorItem else null

    override fun getHeaders(): List<IHeader> {
        val result = mutableListOf<IHeader>()
        mList.forEach {
            if (it is IHeader) {
                result.add(it)
            }
        }
        return result
    }

    override fun getFooters(): List<IFooter> {
        val result = mutableListOf<IFooter>()
        mList.forEach {
            if (it is IFooter) {
                result.add(it)
            }
        }
        return result
    }

    override fun getItems(): List<IItem> {
        val result = mutableListOf<IItem>()
        mList.forEach {
            if (it is IItem) {
                result.add(it)
            }
        }
        return result
    }

    override fun getHeader(headerPosition: Int): IHeader? {
        val headers = getHeaders()
        if (headers.isEmpty()) {
            return null
        }
        return if (headerPosition < headers.size && headerPosition >= 0) headers[headerPosition] else null
    }

    override fun getItem(itemPosition: Int): IItem? {
        val items = getItems()
        if (items.isEmpty()) {
            return null
        }
        return if (itemPosition < items.size && itemPosition >= 0) items[itemPosition] else null
    }

    override fun getFooter(footerPosition: Int): IFooter? {
        val footers = getFooters()
        if (footers.isEmpty()) {
            return null
        }
        return if (footerPosition < footers.size && footerPosition >= 0) footers[footerPosition] else null
    }

    override fun isEmptyItemShow() = mList.size == 1 && get(0) is IEmptyItem
    override fun isErrorItemShow() = mList.size == 1 && get(0) is IErrorItem
    override fun isHeader(position: Int) = get(position) is IHeader
    override fun isFooter(position: Int) = get(position) is IFooter
    override fun isItem(position: Int) = get(position) is IItem

    override fun containsHeader(layoutId: Int): Boolean {
        if (layoutId < 0) return false
        return mList.any { it is IHeader && it.layoutId == layoutId }
    }

    override fun containsFooter(layoutId: Int): Boolean {
        if (layoutId < 0) return false
        return mList.any { it is IFooter && it.layoutId == layoutId }
    }

    override fun containsItem(layoutId: Int): Boolean {
        if (layoutId < 0) return false
        return mList.any { it is IItem && it.layoutId == layoutId }
    }

    override fun updateHeader(position: Int, newHeader: IHeader) {
        if (isHeader(position)) {
            mList[position] = newHeader
        }
    }

    override fun updateFooter(position: Int, newFooter: IFooter) {
        if (isFooter(position)) {
            mList[position] = newFooter
        }
    }

    override fun updateItem(position: Int, newItem: IItem) {
        if (isItem(position)) {
            mList[position] = newItem
        }
    }

    override fun setEmptyItem(emptyItem: IEmptyItem) {
        clear()
        addItemToStart(emptyItem)
    }

    override fun setErrorItem(errorItem: IErrorItem) {
        clear()
        addItemToStart(errorItem)
    }

    /**
     * 添加footer到footers的开始位置
     *
     */
    override fun addFooterToStart(footer: IFooter?) {
        addFooter(0, footer)
    }

    /**
     * 添加footer到footers的末尾
     *
     */
    override fun addFooterToEnd(footer: IFooter?) {
        addFooter(getFooters().size, footer)
    }

    /**
     * 添加footer
     *
     * @param positionInFooters 在footers中的位置
     */
    override fun addFooter(positionInFooters: Int, footer: IFooter?) {
        add(convertFooterPositionToListPosition(positionInFooters), footer)
    }

    /**
     * 添加footers到footers的开始位置
     *
     */
    override fun addFootersToStart(footers: List<IFooter>?) {
        addFooters(0, footers)
    }

    /**
     * 添加footers到footers的末尾
     *
     */
    override fun addFootersToEnd(footers: List<IFooter>?) {
        addFooters(getFooters().size, footers)
    }

    /**
     * 添加footers
     *
     * @param positionInFooters 在footers中的位置
     */
    override fun addFooters(positionInFooters: Int, footers: List<IFooter>?) {
        addAll(convertFooterPositionToListPosition(positionInFooters), footers)
    }

    /**
     * 添加header到headers的开始位置
     *
     */
    override fun addHeaderToStart(header: IHeader?) {
        addHeader(0, header)
    }

    /**
     * 添加header到headers的末尾
     *
     */
    override fun addHeaderToEnd(header: IHeader?) {
        addHeader(getHeaders().size, header)
    }

    /**
     * 添加header
     *
     * @param positionInHeaders 在headers中的位置
     */
    override fun addHeader(positionInHeaders: Int, header: IHeader?) {
        add(convertHeaderPositionToListPosition(positionInHeaders), header)
    }

    /**
     * 添加headers到headers的开始位置
     *
     */
    override fun addHeadersToStart(headers: List<IHeader>?) {
        addHeaders(0, headers)
    }

    /**
     * 添加headers到headers的末尾
     *
     */
    override fun addHeadersToEnd(headers: List<IHeader>?) {
        addHeaders(getHeaders().size, headers)
    }

    /**
     * 添加headers
     *
     * @param positionInHeaders 在headers中的位置
     */
    override fun addHeaders(positionInHeaders: Int, headers: List<IHeader>?) {
        addAll(convertHeaderPositionToListPosition(positionInHeaders), headers)
    }

    /**
     * 添加item到items的开始位置
     *
     */
    override fun addItemToStart(item: IItem?) {
        addItem(0, item)
    }

    /**
     * 添加item到items的末尾
     *
     */
    override fun addItemToEnd(item: IItem?) {
        addItem(getItems().size, item)
    }

    /**
     * 添加item
     *
     * @param positionInItems 在items中的位置
     */
    override fun addItem(positionInItems: Int, item: IItem?) {
        add(convertItemPositionToListPosition(positionInItems), item)
    }

    /**
     * 添加items到items的开始位置
     *
     */
    override fun addItemsToStart(items: List<IItem>?) {
        addItems(0, items)
    }

    /**
     * 添加items到items的末尾
     *
     */
    override fun addItemsToEnd(items: List<IItem>?) {
        addItems(getItems().size, items)
    }

    /**
     * 添加items
     *
     * @param positionInItems 在items中的位置
     */
    override fun addItems(positionInItems: Int, items: List<IItem>?) {
        addAll(convertItemPositionToListPosition(positionInItems), items)
    }

    /**
     * 清除所有数据，并添加新的数据集合。
     *
     * 注意：此方法传入的list会自动重新排序，规则为：
     * [IHeader]->[IItem]->[IFooter]，其中这三种类型的集合内部排序是根据 [IRecyclerViewItem.sortTag] 标记来确定的。
     */
    override fun clearAndAddAll(list: List<IRecyclerViewItem>?) {
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
        if (position < 0 || position > mList.size) return
        if (isEmptyItemShow() || isErrorItemShow()) clear()
        mList.add(position, data)
    }

    /**
     * 添加数据集合到指定位置
     *
     * @param position 在整个List中的位置
     * @param list
     */
    private fun addAll(position: Int, list: List<IRecyclerViewItem>?) {
        if (list.isNullOrEmpty()) return
        if (position < 0 || position > mList.size) return
        if (isEmptyItemShow() || isErrorItemShow()) clear()
        mList.addAll(position, list)
    }

    override fun removeAll(list: List<IRecyclerViewItem>?) {
        if (list.isNullOrEmpty()) return
        list.reversed().forEach {
            remove(it)
        }
    }

    override fun remove(data: IRecyclerViewItem?) {
        data ?: return
        val position = mList.indexOf(data)
        remove(position)
    }

    override fun remove(position: Int) {
        if (position < 0 || position >= mList.size) return
        mList.removeAt(position)
    }

    override fun clearHeaders() {
        mList.removeAll { it is IHeader }
    }

    override fun clearFooters() {
        mList.removeAll { it is IFooter }
    }

    override fun clearItems() {
        mList.removeAll { it is IItem }
    }

    override fun clear() {
        mList.clear()
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

    private fun convertFooterPositionToListPosition(positionInFooters: Int): Int {
        val footerCount = getFooters().size
        return mList.size - footerCount + when {
            positionInFooters < 0 -> 0
            positionInFooters > footerCount -> footerCount
            else -> positionInFooters
        }
    }

    private fun convertHeaderPositionToListPosition(positionInHeaders: Int): Int {
        val headerCount = getHeaders().size
        return when {
            positionInHeaders < 0 -> 0
            positionInHeaders > headerCount -> headerCount
            else -> positionInHeaders
        }
    }

    private fun convertItemPositionToListPosition(positionInItems: Int): Int {
        val itemCount = getItems().size
        return getHeaders().size + when {
            positionInItems < 0 -> 0
            positionInItems > itemCount -> itemCount
            else -> positionInItems
        }
    }

}