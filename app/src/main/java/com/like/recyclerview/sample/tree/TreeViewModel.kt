package com.like.recyclerview.sample.tree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TreeViewModel(private val treeRepository: TreeRepository) : ViewModel() {
    fun getResult() = treeRepository.getResult()

    /**
     * 如果获取ViewModel需要参数，就自定义一个Factory类。或者通过定义公共方法传参。
     */
    class Factory(private val treeRepository: TreeRepository) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TreeViewModel::class.java)) {
                return TreeViewModel(treeRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class：$modelClass")
        }
    }
}