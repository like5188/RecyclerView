package com.like.recyclerview.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.listener.OnItemLongClickListener
import com.like.recyclerview.model.IFooter
import com.like.recyclerview.model.IHeader
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.utils.AdapterDataManager
import com.like.recyclerview.utils.IAdapterDataManager
import com.like.recyclerview.viewholder.CommonViewHolder

/**
 * 当不需要分页加载时使用。
 */
@Suppress("LeakingThis")
open class BaseAdapter : RecyclerView.Adapter<CommonViewHolder>(), IAdapterDataManager by AdapterDataManager() {
    companion object {
        private const val TAG = "BaseAdapter"
    }

    var recyclerView: RecyclerView? = null
    private val mOnItemClickListeners = mutableListOf<OnItemClickListener>()
    private val mOnItemLongClickListeners = mutableListOf<OnItemLongClickListener>()

    init {
        (getAll() as ObservableArrayList).addOnListChangedCallback(
            object : ObservableList.OnListChangedCallback<ObservableArrayList<IRecyclerViewItem>>() {
                override fun onChanged(sender: ObservableArrayList<IRecyclerViewItem>?) {
                    Log.d(TAG, "onChanged")
                    // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
                    fun update() {
                        notifyDataSetChanged()
                    }
                    if (recyclerView?.isComputingLayout == true) {
                        recyclerView?.post {
                            update()
                        }
                    } else {
                        update()
                    }
                }

                override fun onItemRangeRemoved(
                    sender: ObservableArrayList<IRecyclerViewItem>?,
                    positionStart: Int,
                    itemCount: Int
                ) {
                    Log.d(TAG, "onItemRangeRemoved")
                    fun update() {
                        if (sender?.isEmpty() == true) {
                            notifyDataSetChanged()
                        } else {
                            notifyItemRangeRemoved(positionStart, itemCount)
                            notifyItemRangeChanged(positionStart, getItemCount() - positionStart)
                        }
                    }
                    if (recyclerView?.isComputingLayout == true) {
                        recyclerView?.post {
                            update()
                        }
                    } else {
                        update()
                    }
                }

                override fun onItemRangeMoved(
                    sender: ObservableArrayList<IRecyclerViewItem>?,
                    fromPosition: Int,
                    toPosition: Int,
                    itemCount: Int
                ) {
                    Log.d(TAG, "onItemRangeMoved")
                    fun update() {
                        // 这个回调是在 List 里的连续的元素整个移动的情况下会进行的回调，然而 RecyclerView 的 Adapter 里并没有对应的方法，
                        // 只有单个元素移动时的方法，所以需要在回调方法中做一个判断，如果移动的元素只有一个，就调用 Adapter 对应的方法，
                        // 如果超过一个，就直接调用notifyDataSetChanged()方法即可。
                        if (itemCount == 1) {
                            notifyItemMoved(fromPosition, toPosition)
                        } else {
                            notifyDataSetChanged()
                        }
                    }
                    if (recyclerView?.isComputingLayout == true) {
                        recyclerView?.post {
                            update()
                        }
                    } else {
                        update()
                    }
                }

                override fun onItemRangeInserted(
                    sender: ObservableArrayList<IRecyclerViewItem>?,
                    positionStart: Int,
                    itemCount: Int
                ) {
                    Log.d(TAG, "onItemRangeInserted")
                    fun update() {
                        notifyItemRangeInserted(positionStart, itemCount)
                        notifyItemRangeChanged(positionStart, getItemCount() - positionStart)
                        onItemRangeInsertedForLoadMore(positionStart, itemCount)
                    }
                    if (recyclerView?.isComputingLayout == true) {
                        recyclerView?.post {
                            update()
                        }
                    } else {
                        update()
                    }
                }

                override fun onItemRangeChanged(
                    sender: ObservableArrayList<IRecyclerViewItem>?,
                    positionStart: Int,
                    itemCount: Int
                ) {
                    Log.d(TAG, "onItemRangeChanged")
                    fun update() {
                        notifyItemRangeChanged(positionStart, itemCount)
                    }
                    if (recyclerView?.isComputingLayout == true) {
                        recyclerView?.post {
                            update()
                        }
                    } else {
                        update()
                    }
                }

            }
        )
    }

    fun addOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListeners.add(listener)
    }

    fun addOnItemLongClickListener(listener: OnItemLongClickListener) {
        mOnItemLongClickListeners.add(listener)
    }

    fun removeOnItemClickListener(listener: OnItemClickListener) {
        mOnItemClickListeners.remove(listener)
    }

    fun removeOnItemLongClickListener(listener: OnItemLongClickListener) {
        mOnItemLongClickListeners.remove(listener)
    }

    fun clearOnItemClickListeners() {
        mOnItemClickListeners.clear()
    }

    fun clearOnItemLongClickListeners() {
        mOnItemLongClickListeners.clear()
    }

    final override fun getItemCount(): Int {
        return getAll().size
    }

    final override fun getItemViewType(position: Int): Int {
        return get(position)?.layoutId ?: -1
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return CommonViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), viewType, parent, false)).apply {
            // 为list添加Item的点击事件监听
            if (mOnItemClickListeners.isNotEmpty()) {
                itemView.setOnClickListener {
                    val position = recyclerView?.getChildAdapterPosition(itemView) ?: -1
                    val item = get(position)
                    mOnItemClickListeners.forEach {
                        it.onItemClick(this, position, item)
                    }
                }
            }
            // 为list添加Item的长按事件监听
            if (mOnItemLongClickListeners.isNotEmpty()) {
                itemView.setOnLongClickListener {
                    val position = recyclerView?.getChildAdapterPosition(itemView) ?: -1
                    val item = get(position)
                    mOnItemLongClickListeners.forEach {
                        it.onItemLongClick(this, position, item)
                    }
                    true
                }
            }
        }
    }

    final override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        // 绑定变量
        val item = get(position)
        val variableId = item?.variableId() ?: IRecyclerViewItem.INVALID_VARIABLE_ID
        if (variableId >= 0) {
            try {
                holder.binding.setVariable(variableId, item)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        // 绑定指定位置item的其他变量。如果一个布局中有多个变量的话。
        onBindViewHolder(holder, position, item)
    }

    /**
     * 当为GridLayoutManager时，合并header和footer视图
     */
    final override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
        val layoutManager = recyclerView.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanCount = layoutManager.spanCount
            if (spanCount > 1) {
                layoutManager.spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            var ss = 1
                            val item = get(position)
                            if (item is IHeader || item is IFooter) {
                                ss = spanCount
                            }
                            return ss
                        }
                    }
            }
        }
    }

    /**
     * 当为StaggeredGridLayoutManager时，合并header和footer视图
     */
    final override fun onViewAttachedToWindow(holder: CommonViewHolder) {
        val lp = holder.binding.root.layoutParams
        if (lp != null && lp is StaggeredGridLayoutManager.LayoutParams) {
            val item = get(holder.adapterPosition)
            if (item is IHeader || item is IFooter) {
                lp.isFullSpan = true
            }
        }
    }

    /**
     * item插入成功时回调。用于[BaseLoadAfterAdapter]、[BaseLoadBeforeAdapter]处理加载更多逻辑
     */
    protected open fun onItemRangeInsertedForLoadMore(positionStart: Int, itemCount: Int) {}

    /**
     * 绑定指定位置item的其他变量。如果一个布局中有除了[IRecyclerViewItem.variableId]之外的其它变量的话。
     *
     * @param holder
     * @param position
     * @param item
     */
    protected open fun onBindViewHolder(holder: CommonViewHolder, position: Int, item: IRecyclerViewItem?) {}

}
