package com.like.recyclerview.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.listener.OnItemLongClickListener
import com.like.recyclerview.utils.AdapterDataManager
import com.like.recyclerview.utils.IAdapterDataManager
import com.like.recyclerview.viewholder.BindingViewHolder

/**
 * 封装了
 * 1：单击、长按监听；
 * 2：数据处理；
 * 3：界面更新；
 */
abstract class AbstractAdapter<VB : ViewDataBinding, ValueInList>
    : RecyclerView.Adapter<BindingViewHolder<VB>>(),
    IAdapterDataManager<ValueInList> by AdapterDataManager() {
    companion object {
        private const val TAG = "AbstractAdapter"
    }

    protected lateinit var recyclerView: RecyclerView
    private val mOnItemClickListeners = mutableListOf<OnItemClickListener<VB>>()
    private val mOnItemLongClickListeners = mutableListOf<OnItemLongClickListener<VB>>()

    init {
        mList.addOnListChangedCallback(
            object : ObservableList.OnListChangedCallback<ObservableArrayList<ValueInList>>() {
                private fun update(block: () -> Unit) {
                    if (::recyclerView.isInitialized && recyclerView.isComputingLayout) {
                        recyclerView.post {
                            block()
                        }
                    } else {
                        block()
                    }
                }

                override fun onChanged(sender: ObservableArrayList<ValueInList>?) {
                    Log.d(TAG, "onChanged")
                    // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
                    update {
                        notifyDataSetChanged()
                    }
                }

                override fun onItemRangeRemoved(
                    sender: ObservableArrayList<ValueInList>?,
                    positionStart: Int,
                    itemCount: Int,
                ) {
                    Log.d(TAG, "onItemRangeRemoved positionStart=$positionStart itemCount=$itemCount")
                    update {
                        if (sender?.isEmpty() == true) {
                            notifyDataSetChanged()
                        } else {
                            notifyItemRangeRemoved(positionStart, itemCount)
                            notifyItemRangeChanged(positionStart, getItemCount() - positionStart)
                        }
                    }
                }

                override fun onItemRangeMoved(
                    sender: ObservableArrayList<ValueInList>?,
                    fromPosition: Int,
                    toPosition: Int,
                    itemCount: Int,
                ) {
                    Log.d(TAG, "onItemRangeMoved fromPosition=$fromPosition toPosition=$toPosition itemCount=$itemCount")
                    update {
                        // 这个回调是在 List 里的连续的元素整个移动的情况下会进行的回调，然而 RecyclerView 的 Adapter 里并没有对应的方法，
                        // 只有单个元素移动时的方法，所以需要在回调方法中做一个判断，如果移动的元素只有一个，就调用 Adapter 对应的方法，
                        // 如果超过一个，就直接调用notifyDataSetChanged()方法即可。
                        if (itemCount == 1) {
                            notifyItemMoved(fromPosition, toPosition)
                        } else {
                            notifyDataSetChanged()
                        }
                    }
                }

                override fun onItemRangeInserted(
                    sender: ObservableArrayList<ValueInList>?,
                    positionStart: Int,
                    itemCount: Int,
                ) {
                    Log.d(TAG, "onItemRangeInserted positionStart=$positionStart itemCount=$itemCount")
                    update {
                        notifyItemRangeInserted(positionStart, itemCount)
                        notifyItemRangeChanged(positionStart, getItemCount() - positionStart)
                    }
                }

                override fun onItemRangeChanged(
                    sender: ObservableArrayList<ValueInList>?,
                    positionStart: Int,
                    itemCount: Int,
                ) {
                    Log.d(TAG, "onItemRangeChanged positionStart=$positionStart itemCount=$itemCount")
                    update {
                        notifyItemRangeChanged(positionStart, itemCount)
                    }
                }

            }
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<VB> {
        return BindingViewHolder(DataBindingUtil.inflate<VB>(LayoutInflater.from(parent.context), viewType, parent, false)).apply {
            // 为list添加Item的点击事件监听
            if (mOnItemClickListeners.isNotEmpty()) {
                itemView.setOnClickListener {
                    mOnItemClickListeners.forEach {
                        it.onItemClick(this)
                    }
                }
            }
            // 为list添加Item的长按事件监听
            if (mOnItemLongClickListeners.isNotEmpty()) {
                itemView.setOnLongClickListener {
                    mOnItemLongClickListeners.forEach {
                        it.onItemLongClick(this)
                    }
                    true
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    fun addOnItemClickListener(listener: OnItemClickListener<VB>) {
        mOnItemClickListeners.add(listener)
    }

    fun addOnItemLongClickListener(listener: OnItemLongClickListener<VB>) {
        mOnItemLongClickListeners.add(listener)
    }

    fun removeOnItemClickListener(listener: OnItemClickListener<VB>) {
        mOnItemClickListeners.remove(listener)
    }

    fun removeOnItemLongClickListener(listener: OnItemLongClickListener<VB>) {
        mOnItemLongClickListeners.remove(listener)
    }

    fun clearOnItemClickListeners() {
        mOnItemClickListeners.clear()
    }

    fun clearOnItemLongClickListeners() {
        mOnItemLongClickListeners.clear()
    }

}
