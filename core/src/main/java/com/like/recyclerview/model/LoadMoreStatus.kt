package com.like.recyclerview.model

import android.util.Log

sealed class LoadMoreStatus {
    init {
        Log.d("LoadMoreStatus", this.toString())
    }
}

class LoadMoreLoading : LoadMoreStatus()
class LoadMoreComplete : LoadMoreStatus()
class LoadMoreEnd : LoadMoreStatus()
class LoadMoreError : LoadMoreStatus()