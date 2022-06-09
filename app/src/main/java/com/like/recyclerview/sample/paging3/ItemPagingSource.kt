package com.like.recyclerview.sample.paging3

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.concat.DataFactory
import kotlinx.coroutines.delay

class ItemPagingSource : PagingSource<Int, IRecyclerViewItem>() {
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
            val key = params.key ?: 0
            val loadSize = params.loadSize
            Log.d("tag", "ItemDataSource")
            delay(1000)
            val data = (key * loadSize until (key * loadSize + loadSize)).map {
                DataFactory.createItem(it)
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