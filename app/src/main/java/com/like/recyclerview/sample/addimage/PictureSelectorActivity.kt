package com.like.recyclerview.sample.addimage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
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
    private val mAddImageViewAdapter by lazy {
        MyAddImageViewAdapter(this, mBinding.rv, R.drawable.icon_take_photo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapGridLayoutManager(this, 4)
        mBinding.rv.adapter = mAddImageViewAdapter
    }

}
