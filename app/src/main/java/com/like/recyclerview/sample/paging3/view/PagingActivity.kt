package com.like.recyclerview.sample.paging3.view

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPagingBinding
import com.like.recyclerview.sample.paging3.adapter.ArticleAdapter
import com.like.recyclerview.sample.paging3.adapter.HeaderAdapter
import com.like.recyclerview.sample.paging3.viewModel.PagingViewModel
import com.like.recyclerview.ui.loadstate.LoadStateAdapter
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
    }
    private val mViewModel: PagingViewModel by viewModel()
    private val inDb = false// true：使用数据库；false：不使用数据库；

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

        lifecycleScope.launchWhenCreated {
            mArticleAdapter.loadStateFlow.collectLatest {
                when (it.refresh) {
                    is LoadState.NotLoading -> mProgressDialog.dismiss()
                    is LoadState.Loading -> mProgressDialog.show()
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            mArticleAdapter.enableScrollToTopAfterRefresh()
        }

        mBinding.rv.adapter = mArticleAdapter.withLoadStateHeaderAndFooter(mBannerAdapter, mFooterAdapter)

        if (inDb) {
            getDbBannerInfo()
            getDbTopArticle()
            getDbArticle()
        } else {
            getMemoryBannerInfo()
            getMemoryTopArticle()
            getMemoryArticle()
        }
    }

    fun refresh(view: View) {
        if (inDb) {
            getDbBannerInfo()
            getDbTopArticle()
        } else {
            getMemoryBannerInfo()
            getMemoryTopArticle()
        }
        mArticleAdapter.refresh()
    }

    private fun getDbBannerInfo() {
        lifecycleScope.launchWhenCreated {
            mViewModel.getDbBannerInfoFlow(true).collectLatest {
                mBannerAdapter.bannerInfo = it
            }
        }
    }

    private fun getDbTopArticle() {
        lifecycleScope.launchWhenCreated {
            mViewModel.getDbTopArticleFlow(true).collectLatest {
                mBannerAdapter.topArticleList = it
            }
        }
    }

    private fun getDbArticle() {
        lifecycleScope.launchWhenCreated {
            mViewModel.dbArticleFlow.collectLatest {
                mArticleAdapter.submitData(it)
            }
        }
    }

    private fun getMemoryBannerInfo() {
        lifecycleScope.launchWhenCreated {
            mViewModel.getMemoryBannerInfoFlow().collectLatest {
                mBannerAdapter.bannerInfo = it
            }
        }
    }

    private fun getMemoryTopArticle() {
        lifecycleScope.launchWhenCreated {
            mViewModel.getMemoryTopArticleFlow().collectLatest {
                mBannerAdapter.topArticleList = it
            }
        }
    }

    private fun getMemoryArticle() {
        lifecycleScope.launchWhenCreated {
            mViewModel.memoryArticleFlow.collectLatest {
                mArticleAdapter.submitData(it)
            }
        }
    }

}
