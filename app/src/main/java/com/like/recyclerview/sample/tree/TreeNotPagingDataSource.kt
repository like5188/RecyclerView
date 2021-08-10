package com.like.recyclerview.sample.tree

import com.like.recyclerview.sample.R
import kotlinx.coroutines.delay

class TreeNotPagingDataSource {

    suspend fun load(): List<TreeNode0>? {
        delay(1000)
        return listOf(
            TreeNode0(R.drawable.file, "第一人民医院", 8),
            TreeNode0(R.drawable.file, "第二人民医院", 10),
            TreeNode0(R.drawable.file, "第三人民医院", 6)
        )
    }
}