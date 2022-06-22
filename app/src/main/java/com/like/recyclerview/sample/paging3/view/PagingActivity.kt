package com.like.recyclerview.sample.paging3.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.like.common.util.Logger
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPagingBinding
import com.like.recyclerview.sample.paging3.adapter.PagingDataAdapter
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.viewModel.PagingViewModel
import com.like.recyclerview.ui.loadstate.LoadStateAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
    }
    private val mViewModel: PagingViewModel by viewModels()

    private val mAdapter by lazy {
        PagingDataAdapter()
    }
    private val mLoadStateAdapter by lazy {
        LoadStateAdapter()
    }
    private val mProgressDialog by lazy {
        ProgressDialog(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线

        mBinding.btnRefresh.setOnClickListener {
            mAdapter.refresh()
        }

        lifecycleScope.launch {
            mAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
                    is LoadState.NotLoading -> mProgressDialog.dismiss()
                    is LoadState.Loading -> mProgressDialog.show()
                }
            }
        }

        mBinding.rv.adapter = mAdapter.withLoadStateFooter(mLoadStateAdapter)

        lifecycleScope.launch {
            mViewModel.pagingFlow.collectLatest {
                mAdapter.submitData(it)
            }
        }
    }

    fun clearDb(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            Db.getInstance(application).bannerEntityDao().deleteAll()
            Db.getInstance(application).topArticleEntityDao().deleteAll()
            Db.getInstance(application).articleEntityDao().deleteAll()
        }
    }

    fun queryDb(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            Db.getInstance(application).bannerEntityDao().getAll().forEach {
                Logger.i(it.toString())
            }
            Db.getInstance(application).topArticleEntityDao().getAll().forEach {
                Logger.i(it.toString())
            }
            Db.getInstance(application).articleEntityDao().getAll().forEach {
                Logger.i(it.toString())
            }
        }
    }
}
