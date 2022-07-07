package com.like.recyclerview.sample.concat.repository

import com.like.common.util.Logger
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.concat.vo.DataFactory
import kotlinx.coroutines.delay

class HeaderDataSource {

    suspend fun load(): List<IRecyclerViewItem>? {
        Logger.d("HeaderDataSource")
        delay(2000)
        return (0..5).map {
            DataFactory.createHeader(it)
        }
    }

}
