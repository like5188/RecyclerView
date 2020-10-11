package com.like.recyclerview.ui

inline fun <T, reified V> List<T>?.map(): List<V> {
    val result = mutableListOf<V>()
    this?.forEach {
        if (it is V) {
            result.add(it)
        }
    }
    return result
}