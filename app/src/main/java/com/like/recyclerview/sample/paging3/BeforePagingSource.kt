package com.like.recyclerview.sample.paging3

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.like.common.util.Logger
import com.like.recyclerview.model.IRecyclerViewItem
import kotlinx.coroutines.delay

class BeforePagingSource : PagingSource<Int, IRecyclerViewItem>() {
    override fun getRefreshKey(state: PagingState<Int, IRecyclerViewItem>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, IRecyclerViewItem> {
        return try {
            val key = params.key ?: 10
            val loadSize = params.loadSize
            delay(3000)
            val start = key * loadSize - 1
            val end = start - loadSize + 1
            Logger.i("BeforePagingSource load key=$key loadSize=$loadSize start=$start end=$end")
            val data = (end..start).map {
                DataFactory.createItem(it)
            }
            val prePage = if (data.size < loadSize || key < 9) {
                //没有更多数据
                null
            } else {
                key - 1
            }

            LoadResult.Page(
                data = data,
                prevKey = prePage,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}