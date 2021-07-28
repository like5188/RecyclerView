package com.like.recyclerview.sample.concat

import com.like.paging.RequestType
import com.like.paging.RequestType.Initial
import com.like.paging.RequestType.Refresh
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import com.like.recyclerview.sample.model.Item
import kotlinx.coroutines.delay

class LoadAfterPagingDataSource(pageSize: Int) : PageNoKeyedPagingDataSource<List<Item>?>(pageSize) {
    private var i = 0
    private var j = 0

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<Item>? {
        delay(1000)
        return when (requestType) {
            is Initial, is Refresh -> {
                when (++j) {
                    1 -> throw RuntimeException("hahaha")
                    2 -> emptyList()
                    else -> getInitialData(pageNo, pageSize)
                }
            }
            else -> {
                if (i == 1) {
                    i = 2
                    throw RuntimeException("哈哈哈出错啦！！")
                } else {
                    getAfter(pageNo, pageSize)
                }
            }
        }

    }

    override fun getInitialPage(): Int {
        return 0
    }

    private fun getInitialData(pageNo: Int, pageSize: Int): List<Item> {
        val start = pageNo * pageSize + 1
        val end = start + pageSize
        val items = (start until end).map {
            Item(
                id = it,
                name = "name $it",
                des = "des $it"
            )
        }
        i = 0
        return items
    }

    private fun getAfter(pageNo: Int, pageSize: Int): List<Item> {
        val start = pageNo * pageSize + 1
        val end = start + pageSize
        val result = if (i == 3) {
            emptyList()
        } else {
            (start until end).map {
                Item(
                    id = it,
                    name = "name $it",
                    des = "des $it"
                )
            }
        }
        i++
        return result
    }

}