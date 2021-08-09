package com.like.recyclerview.utils

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView

class NotifyOnListChangedCallback(
    private val recyclerView: RecyclerView,
    private val adapter: RecyclerView.Adapter<*>
) :
    ObservableList.OnListChangedCallback<ObservableArrayList<*>>() {
    companion object {
        private const val TAG = "NotifyOnListChanged"
    }

    private fun update(block: () -> Unit) {
        // java.lang.IllegalStateException: Cannot call this method while RecyclerView is computing a layout or scrolling
        if (recyclerView.isComputingLayout) {
            recyclerView.post {
                block()
            }
        } else {
            block()
        }
    }

    override fun onChanged(sender: ObservableArrayList<*>?) {
        Log.d(TAG, "onChanged adapter=$adapter")
        update {
            adapter.notifyDataSetChanged()
        }
    }

    override fun onItemRangeChanged(sender: ObservableArrayList<*>?, positionStart: Int, itemCount: Int) {
        Log.d(TAG, "onItemRangeChanged positionStart=$positionStart itemCount=$itemCount adapter=$adapter")
        update {
            adapter.notifyItemRangeChanged(positionStart, itemCount)
        }
    }

    override fun onItemRangeInserted(sender: ObservableArrayList<*>?, positionStart: Int, itemCount: Int) {
        Log.d(TAG, "onItemRangeInserted positionStart=$positionStart itemCount=$itemCount adapter=$adapter")
        update {
            adapter.notifyItemRangeInserted(positionStart, itemCount)
        }
    }

    override fun onItemRangeMoved(sender: ObservableArrayList<*>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
        Log.d(
            TAG,
            "onItemRangeMoved fromPosition=$fromPosition toPosition=$toPosition itemCount=$itemCount adapter=$adapter"
        )
        update {
            // 这个回调是在 List 里的连续的元素整个移动的情况下会进行的回调，然而 RecyclerView 的 Adapter 里并没有对应的方法，
            // 只有单个元素移动时的方法，所以需要在回调方法中做一个判断，如果移动的元素只有一个，就调用 Adapter 对应的方法，
            // 如果超过一个，就直接调用notifyDataSetChanged()方法即可。
            if (itemCount == 1) {
                adapter.notifyItemMoved(fromPosition, toPosition)
            } else {
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onItemRangeRemoved(sender: ObservableArrayList<*>?, positionStart: Int, itemCount: Int) {
        Log.d(TAG, "onItemRangeRemoved positionStart=$positionStart itemCount=$itemCount adapter=$adapter")
        update {
            adapter.notifyItemRangeRemoved(positionStart, itemCount)
        }
    }
}