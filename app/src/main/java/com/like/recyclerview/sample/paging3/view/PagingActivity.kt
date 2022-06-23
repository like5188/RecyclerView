package com.like.recyclerview.sample.paging3.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
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
import com.like.recyclerview.sample.paging3.adapter.ArticleAdapter
import com.like.recyclerview.sample.paging3.adapter.HeaderAdapter
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.viewModel.PagingViewModel
import com.like.recyclerview.ui.loadstate.LoadStateAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
    }
    private val mViewModel: PagingViewModel by viewModel()
    private val db: Db by inject()

    private val mArticleAdapter by lazy {
        ArticleAdapter()
    }
    private val mBannerAdapter by lazy {
        HeaderAdapter()
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

        lifecycleScope.launch {
            mArticleAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
                    is LoadState.NotLoading -> mProgressDialog.dismiss()
                    is LoadState.Loading -> mProgressDialog.show()
                }
            }
        }

        mBinding.rv.adapter = mArticleAdapter

        lifecycleScope.launch {
            mViewModel.dbArticleFlowFlow.collectLatest {
                mArticleAdapter.submitData(it)
            }
        }
        getBannerInfo()
        getTopArticle()
    }

    private fun getBannerInfo() {
        lifecycleScope.launch {
            mViewModel.getBannerInfoFlow(true).collectLatest {
                mBannerAdapter.bannerInfo = it
            }
        }
    }

    private fun getTopArticle() {
        lifecycleScope.launch {
            mViewModel.getTopArticleFlow(true).collectLatest {
                mBannerAdapter.topArticleList = it
            }
        }
    }

    fun refresh(view: View) {
        getBannerInfo()
        getTopArticle()
        mArticleAdapter.refresh()
    }

    fun queryDb(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.bannerDao().getAll().apply {
                Logger.i("size=${this.size} $this")
            }
            db.topArticleDao().getAll().apply {
                Logger.i("size=${this.size} $this")
            }
            db.articleDao().getAll().apply {
                Logger.i("size=${this.size} $this")
            }
        }
    }

    fun clearDb(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.bannerDao().clear()
            db.topArticleDao().clear()
            db.articleDao().clear()
        }
    }
}
