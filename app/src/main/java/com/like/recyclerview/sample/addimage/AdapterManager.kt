package com.like.recyclerview.sample.addimage

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.sample.R
import com.like.recyclerview.utils.add
import com.like.recyclerview.utils.remove
import com.luck.picture.lib.entity.LocalMedia

class AdapterManager(activity: AppCompatActivity) {
    private val mItemAdapter: AbstractItemAdapter by lazy {
        object : AbstractItemAdapter(activity) {
            override fun notifyRemovePlus() {
                mAdapter.remove(mPlusAdapter)
            }

            override fun notifyAddPlus() {
                mAdapter.add(mPlusAdapter)
            }
        }
    }
    private val mPlusAdapter: AbstractPlusAdapter by lazy {
        object : AbstractPlusAdapter(activity, R.drawable.icon_take_photo) {
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

    fun getAdapter(): ConcatAdapter = mAdapter
}