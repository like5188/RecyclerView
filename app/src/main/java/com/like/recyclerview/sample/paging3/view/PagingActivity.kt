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
import com.like.recyclerview.sample.paging3.adapter.ArticleAdapter
import com.like.recyclerview.sample.paging3.adapter.BannerAdapter
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.dataSource.BannerDataSource
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
                    val db = Db.getInstance(application)
                    val bannerDataSource = BannerDataSource()
                    val topArticleDataSource = TopArticleDataSource()
                    val pagingRepository = PagingRepository(db, bannerDataSource, topArticleDataSource)
                    @Suppress("UNCHECKED_CAST")
                    return PagingViewModel(pagingRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }).get(PagingViewModel::class.java)
    }

    private val mArticleAdapter by lazy {
        ArticleAdapter()
    }
    private val mBannerAdapter by lazy {
        BannerAdapter()
    }
    private val mFooterAdapter by lazy {
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
            mArticleAdapter.refresh()
            getBannerInfo()
        }

        lifecycleScope.launch {
            mArticleAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
                    is LoadState.NotLoading -> mProgressDialog.dismiss()
                    is LoadState.Loading -> mProgressDialog.show()
                }
            }
        }

        mBinding.rv.adapter = mArticleAdapter.withLoadStateHeaderAndFooter(mBannerAdapter, mFooterAdapter)

        lifecycleScope.launch {
            mViewModel.articleFlow.collectLatest {
                mArticleAdapter.submitData(it)
            }
        }
        getBannerInfo()
    }

    private fun getBannerInfo() {
        lifecycleScope.launch {
            mViewModel.bannerInfoFlow.collectLatest {
                mBannerAdapter.bannerInfo = it
            }
        }
    }

    fun clearDb(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            Db.getInstance(application).bannerDao().clear()
            Db.getInstance(application).topArticleDao().clear()
            Db.getInstance(application).articleDao().clear()
        }
    }

    fun queryDb(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            Db.getInstance(application).bannerDao().getAll().collectLatest {
                Logger.i(it.toString())
            }
            Db.getInstance(application).topArticleDao().getAll().collectLatest {
                Logger.i(it.toString())
            }
            Db.getInstance(application).articleDao().getAll().collectLatest {
                Logger.i(it.toString())
            }
        }
    }
}
