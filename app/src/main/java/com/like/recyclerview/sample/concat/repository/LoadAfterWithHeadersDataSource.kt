package com.like.recyclerview.sample.concat.repository

import com.like.common.util.Logger
import com.like.common.util.successIfAllSuccess
import com.like.paging.RequestType
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.concat.vo.DataFactory
import kotlinx.coroutines.delay

class LoadAfterWithHeadersDataSource(pageSize: Int) : PageNoKeyedPagingDataSource<List<IRecyclerViewItem>?>(0, pageSize) {
    private var i = 0
    private var j = 0

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
            i = 0
        }
        return if (i == 0) {
            val result = mutableListOf<IRecyclerViewItem>()
            successIfAllSuccess(::getHeader, { getAfter(pageNo, pageSize) }).forEach {
                if (!it.isNullOrEmpty()) {
                    result.addAll(it)
                }
            }
            result
        } else {
            getAfter(pageNo, pageSize)
        }
    }

    private suspend fun getHeader(): List<IRecyclerViewItem>? {
        Logger.d("LoadAfterWithHeadersDataSource getHeader")
        delay(1000)
        return (0..5).map {
            DataFactory.createHeader(it)
        }
    }

    private suspend fun getAfter(pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        Logger.d("LoadAfterWithHeadersDataSource getAfter pageNo=$pageNo pageSize=$pageSize i=$i j=$j")
        delay(1000)
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
