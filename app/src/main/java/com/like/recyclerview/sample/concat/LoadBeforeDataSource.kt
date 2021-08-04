package com.like.recyclerview.sample.concat

import com.like.paging.RequestType
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import kotlinx.coroutines.delay

class LoadBeforeDataSource(pageSize: Int) : PageNoKeyedPagingDataSource<List<Any>?>(pageSize, false) {
    private var i = 0
    private var j = 0

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<Any> {
        delay(1000)
        if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
            i = 0
        }
        return getBefore(pageNo, pageSize)
    }

    override fun getInitialPage(): Int {
        return 10
    }

    private fun getBefore(pageNo: Int, pageSize: Int): List<Any> {
        val start = pageNo * pageSize - 1
        val end = start - pageSize + 1
        return when (i++) {
            0 -> {
                when (j++) {
                    0 -> throw RuntimeException("initial error")
                    1 -> emptyList()
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
