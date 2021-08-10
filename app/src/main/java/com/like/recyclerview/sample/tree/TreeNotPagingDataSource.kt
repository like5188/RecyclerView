package com.like.recyclerview.sample.tree

import com.like.recyclerview.sample.R
import kotlinx.coroutines.delay

class TreeNotPagingDataSource {
    private var i = 0

    suspend fun load(): List<TreeNode0>? {
        delay(1000)
        return when (i++) {
            0 -> throw RuntimeException("load error")
            1 -> emptyList()
            else -> listOf(
                TreeNode0(R.drawable.file, "第一人民医院", 8),
                TreeNode0(R.drawable.file, "第二人民医院", 10),
                TreeNode0(R.drawable.file, "第三人民医院", 6)
            )
        }
    }
}