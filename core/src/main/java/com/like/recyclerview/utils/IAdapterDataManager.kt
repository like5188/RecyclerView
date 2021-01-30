package com.like.recyclerview.utils

import com.like.recyclerview.model.*

internal interface IAdapterDataManager {
    fun getAll(): List<IRecyclerViewItem>
    fun get(position: Int): IRecyclerViewItem?
    fun getEmptyItem(): IEmptyItem?
    fun getErrorItem(): IErrorItem?
    fun getHeaders(): List<IHeader>
    fun getFooters(): List<IFooter>
    fun getItems(): List<IItem>
    fun getHeader(headerPosition: Int): IHeader?
    fun getItem(itemPosition: Int): IItem?
    fun getFooter(footerPosition: Int): IFooter?
    fun isEmptyItemShow(): Boolean
    fun isErrorItemShow(): Boolean
    fun isHeader(position: Int): Boolean
    fun isFooter(position: Int): Boolean
    fun isItem(position: Int): Boolean
    fun containsHeader(layoutId: Int): Boolean
    fun containsFooter(layoutId: Int): Boolean
    fun containsItem(layoutId: Int): Boolean
    fun updateHeader(position: Int, newHeader: IHeader)
    fun updateFooter(position: Int, newFooter: IFooter)
    fun updateItem(position: Int, newItem: IItem)
    fun setEmptyItem(emptyItem: IEmptyItem)
    fun setErrorItem(errorItem: IErrorItem)

    /**
     * 添加footer到footers的开始位置
     *
     */
    fun addFooterToStart(footer: IFooter?)

    /**
     * 添加footer到footers的末尾
     *
     */
    fun addFooterToEnd(footer: IFooter?)

    /**
     * 添加footer
     *
     * @param positionInFooters 在footers中的位置
     */
    fun addFooter(positionInFooters: Int, footer: IFooter?)

    /**
     * 添加footers到footers的开始位置
     *
     */
    fun addFootersToStart(footers: List<IFooter>?)

    /**
     * 添加footers到footers的末尾
     *
     */
    fun addFootersToEnd(footers: List<IFooter>?)

    /**
     * 添加footers
     *
     * @param positionInFooters 在footers中的位置
     */
    fun addFooters(positionInFooters: Int, footers: List<IFooter>?)

    /**
     * 添加header到headers的开始位置
     *
     */
    fun addHeaderToStart(header: IHeader?)

    /**
     * 添加header到headers的末尾
     *
     */
    fun addHeaderToEnd(header: IHeader?)

    /**
     * 添加header
     *
     * @param positionInHeaders 在headers中的位置
     */
    fun addHeader(positionInHeaders: Int, header: IHeader?)

    /**
     * 添加headers到headers的开始位置
     *
     */
    fun addHeadersToStart(headers: List<IHeader>?)

    /**
     * 添加headers到headers的末尾
     *
     */
    fun addHeadersToEnd(headers: List<IHeader>?)

    /**
     * 添加headers
     *
     * @param positionInHeaders 在headers中的位置
     */
    fun addHeaders(positionInHeaders: Int, headers: List<IHeader>?)

    /**
     * 添加item到items的开始位置
     *
     */
    fun addItemToStart(item: IItem?)

    /**
     * 添加item到items的末尾
     *
     */
    fun addItemToEnd(item: IItem?)

    /**
     * 添加item
     *
     * @param positionInItems 在items中的位置
     */
    fun addItem(positionInItems: Int, item: IItem?)

    /**
     * 添加items到items的开始位置
     *
     */
    fun addItemsToStart(items: List<IItem>?)

    /**
     * 添加items到items的末尾
     *
     */
    fun addItemsToEnd(items: List<IItem>?)

    /**
     * 添加items
     *
     * @param positionInItems 在items中的位置
     */
    fun addItems(positionInItems: Int, items: List<IItem>?)

    /**
     * 清除所有数据，并添加新的数据集合。
     *
     * 注意：此方法传入的list会自动重新排序，规则为：
     * [IHeader]->[IItem]->[IFooter]，其中这三种类型的集合内部排序是根据 [IRecyclerViewItem.sortTag] 标记来确定的。
     */
    fun clearAndAddAll(list: List<IRecyclerViewItem>?)
    fun removeAll(list: List<IRecyclerViewItem>?)
    fun remove(data: IRecyclerViewItem?)
    fun remove(position: Int)
    fun clearHeaders()
    fun clearFooters()
    fun clearItems()
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