package com.like.recyclerview.sample.concat.repository

import com.like.common.util.Logger
import com.like.paging.RequestType
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.concat.vo.DataFactory
import kotlinx.coroutines.delay

class LoadAfterDataSource(pageSize: Int) : PageNoKeyedPagingDataSource<List<IRecyclerViewItem>?>(0, pageSize) {
    private var i = 0
    private var j = 0

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        Logger.d("LoadAfterDataSource requestType=$requestType pageNo=$pageNo pageSize=$pageSize i=$i j=$j")
        if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
            i = 0
        }
        delay(1000)
        return getAfter(pageNo, pageSize)
    }

    private fun getAfter(pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        val start = pageNo * pageSize + 1
        val end = start + pageSize
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
                        (start until end).map {
                            DataFactory.createItem(it)
                        }
                    }
                }
            }
            3 -> {
                throw RuntimeException("load more error")
            }
            5 -> {
                emptyList()
            }
            else -> {
                (start until end).map {
                    DataFactory.createItem(it)
                }
            }
        }
    }

}
