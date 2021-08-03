package com.like.recyclerview.sample.model

import android.util.Log
import android.view.View

data class Item(val id: Int, val name: String, val des: String) {

    fun onClick(view: View) {
        Log.d("Item", "onClick $this")
    }
}