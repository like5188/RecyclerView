package com.like.recyclerview.model

/**
 * 请求状态
 */
sealed class LoadMoreState {

    object Loading : LoadMoreState() {
        override fun toString(): String {
            return "Loading"
        }
    }

    object Complete : LoadMoreState() {
        override fun toString(): String {
            return "Complete"
        }
    }

    object End : LoadMoreState() {
        override fun toString(): String {
            return "End"
        }
    }

    data class Error(val throwable: Throwable) : LoadMoreState() {
        override fun toString(): String {
            return "Error[$throwable]"
        }
    }
}