package com.like.recyclerview.sample.concat

import com.like.paging.RequestType
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import com.like.recyclerview.model.IRecyclerViewItem
import kotlinx.coroutines.delay

class LoadBeforeDataSource(pageSize: Int) : PageNoKeyedPagingDataSource<List<IRecyclerViewItem>?>(10, pageSize) {
    private var i = 0
    private var j = 0

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        delay(1000)
        if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
            i = 0
        }
        return getBefore(pageNo, pageSize)
    }

    private fun getBefore(pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        val start = pageNo * pageSize - 1
        val end = start - pageSize + 1
        return when (i++) {
            0 -> {
                when (j++) {
                    0 -> throw RuntimeException("load error 0")
                    1 -> throw RuntimeException("load error 1")
                    2 -> throw RuntimeException("load error 2")
                    3 -> emptyList()
                    4 -> throw RuntimeException("load error 4")
                    6 -> throw RuntimeException("load error 6")
                    else -> {
                        (end..start).map {
                            DataFactory.createItem(it)
                        }
                    }
                }
            }
            2 -> {
                throw RuntimeException("load more error")
            }
            4 -> {
                emptyList()
            }
            else -> {
                (end..start).map {
                    DataFactory.createItem(it)
                }
            }
        }
    }

}
