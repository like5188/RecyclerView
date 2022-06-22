package com.like.recyclerview.sample.paging3.dataSource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.like.common.util.Logger
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils

class PagingDataSource(
    private val bannerDataSource: BannerDataSource,
    private val topArticleDataSource: TopArticleDataSource
) : PagingSource<Int, Any>() {

    override fun getRefreshKey(state: PagingState<Int, Any>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Any> {
        return try {
            val key = params.key ?: 0
            val loadSize = params.loadSize
            Logger.d("MergePagingDataSource key=$key loadSize=$loadSize")
            val data: List<Any> = if (key == 0) {
                val mergedList = mutableListOf<Any>()
                bannerDataSource.load()?.apply {
                    mergedList.addAll(this)
                }
                topArticleDataSource.load()?.apply {
                    mergedList.addAll(this)
                }
                RetrofitUtils.retrofitApi.getArticle(key).getDataIfSuccess()?.datas?.apply {
                    mergedList.addAll(this)
                }
                mergedList
            } else {
                RetrofitUtils.retrofitApi.getArticle(key).getDataIfSuccess()?.datas ?: emptyList()
            }
            val nextPage = if (data.size < loadSize) {
                //没有更多数据
                null
            } else {
                key + 1
            }

            LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}