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
            val key = params.key ?: 0
            val loadSize = params.loadSize
            Logger.d("ArticlePagingSource key=$key loadSize=$loadSize")
            val pagingModel = RetrofitUtils.retrofitApi.getArticle(key, loadSize).getDataIfSuccess()
            val nextPage = if ((pagingModel?.curPage ?: 0) >= (pagingModel?.pageCount ?: 0)) {
                //没有更多数据
                null
            } else {
                key + 1
            }

            LoadResult.Page(
                data = pagingModel?.datas ?: emptyList(),
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}