package com.like.recyclerview.sample.paging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.like.common.util.datasource.RecyclerViewLoadType
import com.like.common.util.datasource.collectWithProgressForRecyclerView
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.adapter.BaseLoadAfterAdapter
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.model.IItem
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPagingBinding
import kotlinx.coroutines.launch

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(PagingViewModel::class.java)
    }
    private val mAdapter: BaseAdapter by lazy {
        BaseLoadAfterAdapter { mViewModel.getResult().loadAfter?.invoke() }
//        BaseLoadBeforeAdapter(PagingViewModel.PAGE_SIZE) { mViewModel.getResult().loadBefore?.invoke() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(10))//添加分割线
        mBinding.rv.adapter = mAdapter

        lifecycleScope.launch {
            mViewModel.getResult().collectWithProgressForRecyclerView(
                mAdapter,
                RecyclerViewLoadType.LoadAfter,
                mBinding.swipeRefreshLayout,
                {
                    it
                }
            ) { holder, position, data ->
                if (data is IItem) {
                    mAdapter.mAdapterDataManager.remove(position)
                }
            }
        }
        mViewModel.getResult().initial()
    }
}
