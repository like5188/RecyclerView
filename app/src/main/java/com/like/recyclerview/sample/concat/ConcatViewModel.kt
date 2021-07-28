package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    val loadAfterPagingResult = LoadAfterPagingDataSource(PAGE_SIZE).result()

}