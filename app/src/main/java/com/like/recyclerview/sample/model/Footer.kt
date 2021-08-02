package com.like.recyclerview.sample.model

import androidx.databinding.ObservableField

data class Footer(val name: ObservableField<String> = ObservableField("onLoading")) {
    fun onComplete() {
        name.set("onLoading")
    }

    fun onEnd() {
        name.set("onEnd")
    }

    fun onError(throwable: Throwable) {
        name.set("onError ${throwable.message} 点击重试")
    }
}