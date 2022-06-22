package com.like.recyclerview.sample.paging3.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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
import com.like.recyclerview.sample.paging3.dataSource.BannerDataSource
import com.like.recyclerview.sample.paging3.dataSource.PagingDataSource
import com.like.recyclerview.sample.paging3.dataSource.PagingRemoteMediator
import com.like.recyclerview.sample.paging3.dataSource.TopArticleDataSource
import com.like.recyclerview.sample.paging3.repository.PagingRepository
import com.like.recyclerview.sample.paging3.viewModel.PagingViewModel
import com.like.recyclerview.ui.loadstate.LoadStateAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PagingViewModel::class.java)) {
                    val bannerDataSource = BannerDataSource()
                    val topArticleDataSource = TopArticleDataSource()
                    val pagingDataSource = PagingDataSource(bannerDataSource, topArticleDataSource)
                    val db = Db.getInstance(application)
                    val pagingRemoteMediator = PagingRemoteMediator(db, bannerDataSource, topArticleDataSource)
                    val pagingRepository = PagingRepository(db, pagingDataSource, pagingRemoteMediator)
                    @Suppress("UNCHECKED_CAST")
                    return PagingViewModel(pagingRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(PagingViewModel::class.java)
    }

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
            Db.getInstance(application).bannerEntityDao().clear()
            Db.getInstance(application).topArticleEntityDao().clear()
            Db.getInstance(application).articleEntityDao().clear()
        }
    }

    fun queryDb(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            Db.getInstance(application).bannerEntityDao().getAll().collectLatest {
                Logger.i(it.toString())
            }
            Db.getInstance(application).topArticleEntityDao().getAll().collectLatest {
                Logger.i(it.toString())
            }
            Db.getInstance(application).articleEntityDao().getAll().collectLatest {
                Logger.i(it.toString())
            }
        }
    }
}
