package com.like.recyclerview.sample.tree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class TreeViewModel : ViewModel() {
    val treeNotPagingResult = TreeNotPagingDataSource(viewModelScope).result()
}