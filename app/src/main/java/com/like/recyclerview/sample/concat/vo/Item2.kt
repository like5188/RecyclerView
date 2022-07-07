package com.like.recyclerview.sample.concat.vo

import android.util.Log
import android.view.View
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.BR
import com.like.recyclerview.sample.R

data class Item2(val id: Int, val name: String, val des: String)  : IRecyclerViewItem {
    override val layoutId: Int = R.layout.item2
    override val variableId: Int = BR.item2

    fun onClick(view: View) {
        Log.d("Item", "onClick $this")
    }
}