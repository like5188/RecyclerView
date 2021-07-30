package com.like.recyclerview.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * 滚动到最底部
 */
fun RecyclerView.scrollToBottom() {
    val adapter = this.adapter ?: return
    scrollToPosition(adapter.itemCount - 1)
}

/**
 * 保持位置（类似于聊天界面的处理，实际上就是使得往前加载更多和往后加载更多的效果一致）
 */
fun RecyclerView.keepPosition(insertedItemCount: Int, headerCount: Int) {
    // 做类似于聊天界面的处理
    val layoutManager = this.layoutManager
    if (layoutManager is LinearLayoutManager) {
        // 第一个item的视图
        layoutManager.getChildAt(headerCount)?.let {
            val offset = it.top
            val position = layoutManager.getPosition(it)
            layoutManager.scrollToPositionWithOffset(insertedItemCount + position, offset)
        }
    }
}
