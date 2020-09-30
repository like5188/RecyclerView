package com.like.recyclerview.sample.paging

import com.like.datasource.paging.byPageNo.PageNoKeyedPagingDataSource
import com.like.datasource.util.LoadType
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.model.*
import kotlinx.coroutines.delay

class LoadBeforePagingDataSource : PageNoKeyedPagingDataSource<List<IRecyclerViewItem>?>(20, false) {
    private var i = 0
    private var j = 0

    override suspend fun load(loadType: LoadType, pageNo: Int, pageSize: Int): List<IRecyclerViewItem>? {
        delay(1000)
        return when (loadType) {
            LoadType.INITIAL, LoadType.REFRESH -> {
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
                    getBefore(pageNo, pageSize)
                }
            }
        }
    }

    override fun getInitialPage(): Int {
        return 10
    }

    private fun getInitialData(pageNo: Int, pageSize: Int): List<IRecyclerViewItem> {
        val start = pageNo * pageSize - 1
        val end = start - pageSize + 1
        val result = mutableListOf<IRecyclerViewItem>()
        val items = (end..start).map {
            Item(
                id = it,
                name = "name $it",
                des = "des $it"
            )
        }
        val headers = listOf(Header1(1, "Header1"), Header2(2, "Header2"))
        val footers = listOf(Footer1(1, "Footer1"), Footer2(2, "Footer2"))
        result.addAll(headers)
        result.addAll(items)
        result.addAll(footers)
        i = 0
        return result
    }

    private fun getBefore(pageNo: Int, pageSize: Int): List<IRecyclerViewItem> {
        val start = pageNo * pageSize - 1
        val end = start - pageSize + 1
        val result = if (i == 3) {
            emptyList()
        } else {
            (end..start).map {
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