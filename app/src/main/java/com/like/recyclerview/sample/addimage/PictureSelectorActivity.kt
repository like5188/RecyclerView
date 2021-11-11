package com.like.recyclerview.sample.addimage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.recyclerview.ext.addimage.AdapterManager
import com.like.recyclerview.layoutmanager.WrapGridLayoutManager
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPictureSelectorBinding

class PictureSelectorActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPictureSelectorBinding>(this, R.layout.activity_picture_selector)
    }
    private val maxSelectNum = 2
    private val myItemAdapter = MyItemAdapter(maxSelectNum)
    private val myPlusAdapter = MyPlusAdapter(R.drawable.icon_add, maxSelectNum)
    private var i = 0

    private val mAdapterManager: AdapterManager<AddImageViewInfo> by lazy {
        AdapterManager(
            activity = this,
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
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapGridLayoutManager(this, 4)
        mBinding.rv.adapter = mAdapterManager.getAdapter()
    }

}
