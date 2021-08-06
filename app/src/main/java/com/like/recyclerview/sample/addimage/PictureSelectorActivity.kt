package com.like.recyclerview.sample.addimage

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.layoutmanager.WrapGridLayoutManager
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPictureSelectorBinding
import com.like.recyclerview.utils.add
import com.like.recyclerview.utils.remove
import com.luck.picture.lib.entity.LocalMedia

class PictureSelectorActivity : AppCompatActivity() {
    companion object {
        const val TAG = "PictureSelectorActivity"
    }

    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPictureSelectorBinding>(this, R.layout.activity_picture_selector)
    }
    private val mItemAdapter: AbstractItemAdapter by lazy {
        object : AbstractItemAdapter(this@PictureSelectorActivity) {
            override fun notifyRemovePlus() {
                mAdapter.remove(mPlusAdapter)
            }

            override fun notifyAddPlus() {
                mAdapter.add(mPlusAdapter)
            }
        }
    }
    private val mPlusAdapter: AbstractPlusAdapter by lazy {
        object : AbstractPlusAdapter(this@PictureSelectorActivity, R.drawable.icon_take_photo) {
            override fun getLocalMedias(): List<LocalMedia> {
                return mItemAdapter.getLocalMedias()
            }

            override fun addItems(list: List<LocalMedia>) {
                mItemAdapter.addLocalMedias(list)
            }

            override fun onAddClicked() {
                mItemAdapter.showDeleteButton.set(false)
            }
        }
    }
    private val mAdapter: ConcatAdapter by lazy {
        ConcatAdapter(mItemAdapter, mPlusAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapGridLayoutManager(this, 4)
        mBinding.rv.adapter = mAdapter
    }

}
