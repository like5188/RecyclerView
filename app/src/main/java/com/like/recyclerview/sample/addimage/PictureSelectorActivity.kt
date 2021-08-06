package com.like.recyclerview.sample.addimage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.like.recyclerview.ext.adapter.addimage.AdapterManager
import com.like.recyclerview.layoutmanager.WrapGridLayoutManager
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPictureSelectorBinding

class PictureSelectorActivity : AppCompatActivity() {
    companion object {
        const val TAG = "PictureSelectorActivity"
    }

    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPictureSelectorBinding>(this, R.layout.activity_picture_selector)
    }
    private val myItemAdapter = MyItemAdapter()
    private val myPlusAdapter = MyPlusAdapter(R.drawable.icon_take_photo)

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
                AddImageViewInfo(it, "des")
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
