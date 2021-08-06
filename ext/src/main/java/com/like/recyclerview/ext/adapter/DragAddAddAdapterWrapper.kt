package com.like.recyclerview.ext.adapter

import android.widget.Toast
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.utils.add
import com.like.recyclerview.utils.addAll
import com.like.recyclerview.utils.remove

/**
 * 添加图片的适配器。带+号，可拖拽交换位置，不使用分页
 */
class DragAddAddAdapterWrapper<ValueInList>(
    val plusAdapter: AbstractAdapter<*, *>,
    val dragAdapter: DragAdapter<*, ValueInList>,
    private val maxImageCount: Int = 9
) {
    val mAdapter = ConcatAdapter()

    init {
        mAdapter.addAll(dragAdapter, plusAdapter)
    }

    fun addItems(items: List<ValueInList>) {
        val curCount = dragAdapter.mList.size
        when {
            curCount + items.size < maxImageCount -> {
                dragAdapter.addAllToEnd(items)
            }
            curCount + items.size == maxImageCount -> {
                mAdapter.remove(plusAdapter)
                dragAdapter.addAllToEnd(items)
            }
            else -> {// 不能添加
                Toast.makeText(dragAdapter.context, "只能添加 $maxImageCount 张图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removeItem(item: ValueInList) {
        val curCount = dragAdapter.mList.size
        when {
            curCount < maxImageCount -> {// 不添加+号图片
                dragAdapter.remove(item)
            }
            curCount == maxImageCount -> {// 添加+号图片
                dragAdapter.remove(item)
                mAdapter.add(plusAdapter)
            }
        }
    }

}
