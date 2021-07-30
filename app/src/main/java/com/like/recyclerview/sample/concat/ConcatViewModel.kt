package com.like.recyclerview.sample.concat

import androidx.lifecycle.ViewModel

class ConcatViewModel : ViewModel() {
    companion object {
        const val PAGE_SIZE = 20
    }

    val loadAfterResult = LoadAfterDataSource(PAGE_SIZE).result()
    val loadBeforeResult = LoadBeforeDataSource(PAGE_SIZE).result()

}