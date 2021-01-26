package com.like.recyclerview.sample.paging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.like.common.util.Logger
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.adapter.BaseLoadAfterAdapter
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPagingBinding
import com.like.recyclerview.ui.util.bindData
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

        mAdapter.addOnItemClickListener { holder, position, data ->
            Logger.e("单击 position=$position")
        }
        mAdapter.addOnItemLongClickListener { holder, position, data ->
            Logger.e("长按 position=$position")
        }

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mViewModel.getResult().refresh()
        }
        lifecycleScope.launch {
            mAdapter.bindData(
                mViewModel.getResult(),
                show = {
                    mBinding.swipeRefreshLayout.isRefreshing = true
                },
                hide = {
                    mBinding.swipeRefreshLayout.isRefreshing = false
                }
            )
        }
        mViewModel.getResult().initial()
    }
}
