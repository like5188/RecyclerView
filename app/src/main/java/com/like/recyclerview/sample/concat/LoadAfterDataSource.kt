package com.like.recyclerview.sample.concat

import com.like.paging.RequestType
import com.like.paging.dataSource.byPageNoKeyed.PageNoKeyedPagingDataSource
import com.like.recyclerview.sample.model.Item
import kotlinx.coroutines.delay

class LoadAfterDataSource(pageSize: Int) : PageNoKeyedPagingDataSource<List<Item>?>(pageSize) {

    override suspend fun load(requestType: RequestType, pageNo: Int, pageSize: Int): List<Item> {
        delay(2000)
        return getAfter(pageNo, pageSize)
    }

    override fun getInitialPage(): Int {
        return 0
    }

    private fun getAfter(pageNo: Int, pageSize: Int): List<Item> {
        val start = pageNo * pageSize + 1
        val end = start + pageSize
        return (start until end).map {
            Item(
                id = it,
                name = "name $it",
                des = "des $it"
            )
        }
    }

}
