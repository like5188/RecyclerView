package com.like.recyclerview.sample.concat

import com.like.paging.RequestType
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import com.like.recyclerview.sample.model.Item
import kotlinx.coroutines.delay

class LoadBeforeDataSource(pageSize: Int) : PageNoKeyedPagingDataSource<List<Item>?>(pageSize, false) {
    private var i = 0

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<Item> {
        delay(1000)
        if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
            i = 0
        }
        return getBefore(pageNo, pageSize)
    }

    override fun getInitialPage(): Int {
        return 10
    }

    private fun getBefore(pageNo: Int, pageSize: Int): List<Item> {
        val start = pageNo * pageSize - 1
        val end = start - pageSize + 1
        return when (i++) {
            2 -> {
                throw RuntimeException("test error")
            }
            4 -> {
                emptyList()
            }
            else -> {
                (end..start).map {
                    Item(
                        id = it,
                        name = "name $it",
                        des = "des $it"
                    )
                }
            }
        }
    }

}
