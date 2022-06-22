package com.like.recyclerview.sample.paging3.data.local

import androidx.annotation.IntRange
import com.like.recyclerview.sample.paging3.data.model.ArticleEntity
import com.like.recyclerview.sample.paging3.data.model.ResultModel

object DataFactory {
    // 这里假设 id 范围为[MIN_ID]~[MAX_ID]
    private const val MIN_ID = 1
    private const val MAX_ID = 100

    /**
     * 返回指定[id]以前的[loadSize]条数据。
     *
     * @param id        上次加载的最前面一个数据的 id。
     * @param loadSize  上次加载的数据条数。
     */
    fun getBeforeResultById(
        @IntRange(from = (MIN_ID + 1).toLong(), to = (MAX_ID + 1).toLong()) id: Int?,
        @IntRange(from = 1) loadSize: Int
    ): ResultModel<List<ArticleEntity>> {
        val start = if (id == null) {
            MAX_ID
        } else {
            id - 1
        }
        var end = start - loadSize + 1
        if (end < MIN_ID) {
            end = MIN_ID
        }
        val list = (end..start).map { createArticleEntity(it) }
        return ResultModel(0, null, list)
    }

    /**
     * 返回指定[id]以后的[loadSize]条数据。
     *
     * @param id        上次加载的最后面一个数据的 id。
     * @param loadSize  上次加载的数据条数。
     */
    fun getAfterResultById(
        @IntRange(from = (MIN_ID - 1).toLong(), to = (MAX_ID - 1).toLong()) id: Int?,
        @IntRange(from = 1) loadSize: Int
    ): ResultModel<List<ArticleEntity>> {
        val start = if (id == null) {
            MIN_ID
        } else {
            id + 1
        }
        var end = start + loadSize - 1
        if (end > MAX_ID) {
            end = MAX_ID
        }
        val list = (start..end).map { createArticleEntity(it) }
        return ResultModel(0, null, list)
    }

    private fun createArticleEntity(id: Int) =
        ArticleEntity().apply {
            this.id = id
            title = "title $id"
        }

}