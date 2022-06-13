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
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.ui.footer.FooterAdapter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityConcatBinding>(this, R.layout.activity_concat)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(PagingViewModel::class.java)
    }
    private val mAdapter by lazy {
        ItemPagingDataAdapter()
    }
    private val mFooterAdapter by lazy {
        FooterAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        mBinding.rv.adapter = mAdapter.withLoadStateFooter(mFooterAdapter)

        mBinding.btnRefresh.setOnClickListener {
            mAdapter.refresh()
        }

        lifecycleScope.launch {
            mViewModel.itemFlow.collectLatest {
                mAdapter.submitData(it)
            }
        }

        lifecycleScope.launch {
            mAdapter.loadStateFlow.collectLatest {
                mFooterAdapter.handRefreshLoadState(it.refresh)
            }
        }

    }

}
