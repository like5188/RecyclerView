package com.like.recyclerview.sample.concat

import com.like.common.util.successIfAllSuccess
import com.like.paging.RequestType
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import com.like.recyclerview.model.IRecyclerViewItem
import kotlinx.coroutines.delay

class LoadAfterWithHeadersDataSource(pageSize: Int) : PageNoKeyedPagingDataSource<List<List<IRecyclerViewItem>?>>(pageSize) {
    private var i = 0
    private var j = 0

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<List<IRecyclerViewItem>?> {
        delay(1000)
        if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
            i = 0
        }
        return successIfAllSuccess(::getHeader, { getAfter(pageNo, pageSize) })
    }

    override fun getInitialPage(): Int {
        return 0
    }

    private fun getHeader(): List<IRecyclerViewItem>? {
        return (0..5).map {
            DataFactory.createHeader(it)
        }
    }

    private fun getAfter(pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        val start = pageNo * pageSize + 1
        val end = start + pageSize
        return when (i++) {
            0 -> {
                when (j++) {
                    0 -> throw RuntimeException("initial error")
                    1 -> emptyList()
                    else -> {
                        (start until end).map {
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
                (start until end).map {
                    DataFactory.createItem(it)
                }
            }
        }
    }

}
