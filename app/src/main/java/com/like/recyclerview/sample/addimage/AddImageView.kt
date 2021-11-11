package com.like.recyclerview.sample.addimage

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.ext.addimage.AdapterManager
import com.like.recyclerview.layoutmanager.WrapGridLayoutManager
import com.like.recyclerview.sample.R

/**
 *  添加图片视图
 */
class AddImageView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private lateinit var myItemAdapter: MyItemAdapter
    private lateinit var myPlusAdapter: MyPlusAdapter
    private var i = 0

    init {
        layoutManager = WrapGridLayoutManager(context, 4)
    }

    fun init(maxSelectNum: Int = Int.MAX_VALUE) {
        myItemAdapter = MyItemAdapter(maxSelectNum)
        myPlusAdapter = MyPlusAdapter(R.drawable.icon_add, maxSelectNum)
        adapter = AdapterManager(
            activity = context as AppCompatActivity,
            itemAdapter = myItemAdapter,
            plusAdapter = myPlusAdapter,
            getLocalMedias = {
                myItemAdapter.mList.map {
                    it.localMedia
                }
            },
            itemCreator = {
                AddImageViewInfo(it, "des ${i++}")
            },
            onPlusItemClicked = {
                myItemAdapter.showDeleteButton.set(false)
            }
        ).getAdapter()
    }

    fun getSelectedImages() = myItemAdapter.mList

}