package com.like.recyclerview.sample.paging3.dataSource.memory

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.like.common.util.Logger
import com.like.recyclerview.sample.paging3.data.model.Article
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils

class ArticlePagingSource : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        return try {
            val page = params.key ?: 0
            val pageSize = params.loadSize
            val pagingModel = RetrofitUtils.retrofitApi.getArticle(page, pageSize).getDataIfSuccess()
            val endOfPaginationReached = (pagingModel?.curPage ?: 0) >= (pagingModel?.pageCount ?: 0)
            Logger.d("ArticlePagingSource page=$page pageSize=$pageSize endOfPaginationReached=$endOfPaginationReached")
            Logger.printCollection(pagingModel?.datas)
            LoadResult.Page(
                data = pagingModel?.datas ?: emptyList(),
                prevKey = null,
                nextKey = if (endOfPaginationReached) null else page + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}