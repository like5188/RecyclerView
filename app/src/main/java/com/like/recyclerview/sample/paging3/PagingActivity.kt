package com.like.recyclerview.sample.paging3

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPagingBinding
import com.like.recyclerview.ui.loadstate.LoadStateAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(PagingViewModel::class.java)
    }
    private val mAdapter by lazy {
        PagingDataAdapter()
    }
    private val mLoadStateAdapter by lazy {
        LoadStateAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线

        mBinding.btnRefresh.setOnClickListener {
            mAdapter.refresh()
        }

        initAfter()
    }

    private fun initAfter() {
        mBinding.rv.adapter = mAdapter.withLoadStateFooter(mLoadStateAdapter)

        lifecycleScope.launch {
            mViewModel.afterFlow.collectLatest {
                mAdapter.submitData(it)
            }
        }
    }

    private fun initBefore() {
        mBinding.rv.adapter = mAdapter.withLoadStateHeader(mLoadStateAdapter)

        lifecycleScope.launch {
            mViewModel.beforeFlow.collectLatest {
                mAdapter.submitData(it)
            }
        }
    }

}
