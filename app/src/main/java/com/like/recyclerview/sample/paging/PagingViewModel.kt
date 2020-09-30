package com.like.recyclerview.sample.paging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PagingViewModel(private val pagingRepository: PagingRepository) : ViewModel() {

    fun getResult() = pagingRepository.getResult()

    /**
     * 如果获取ViewModel需要参数，就自定义一个Factory类。或者通过定义公共方法传参。
     */
    class Factory(private val pagingRepository: PagingRepository) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PagingViewModel::class.java)) {
                return PagingViewModel(pagingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class：$modelClass")
        }
    }
}