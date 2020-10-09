package com.like.recyclerview.sample.paging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.adapter.BaseAppendAdapter
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.model.IItem
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPagingBinding
import com.like.recyclerview.sample.util.bindProgress
import com.like.recyclerview.ui.bindRecyclerView
import kotlinx.coroutines.launch

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
    }
    private val mViewModel by lazy {
        ViewModelProvider(
            this, PagingViewModel.Factory(
                PagingRepository(
                    AppendPagingDataSource(),
                    PrependPagingDataSource()
                )
            )
        ).get(PagingViewModel::class.java)
    }
    private val mAdapter: BaseAdapter by lazy {
        BaseAppendAdapter {
            lifecycleScope.launch { mViewModel.getResult().append?.invoke() }
        }
//        BaseLoadBeforeAdapter(20) {
//            lifecycleScope.launch { mViewModel.getResult().prepend?.invoke() }
//        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(10))//添加分割线
        mBinding.rv.adapter = mAdapter

        lifecycleScope.launch {
            mViewModel.getResult().bindProgress(mBinding.swipeRefreshLayout)
            mViewModel.getResult().bindRecyclerView(mAdapter) { holder, position, data ->
                if (data is IItem) {
                    mAdapter.mAdapterDataManager.remove(position)
                }
            }
//            mViewModel.getResult().bindRecyclerView(mAdapter) { holder, position, data ->
//                if (data is IItem) {
//                    mAdapter.mAdapterDataManager.remove(position)
//                }
//            }
        }
        mViewModel.getResult().initial()

    }
}
