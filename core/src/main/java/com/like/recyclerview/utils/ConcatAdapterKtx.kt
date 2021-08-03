package com.like.recyclerview.utils

import androidx.recyclerview.widget.ConcatAdapter

fun ConcatAdapter.clear() {
    adapters.forEach {
        this.removeAdapter(it)
    }
}