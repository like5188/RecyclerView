package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.hjq.toast.ToastUtils
import com.like.paging.util.bind
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.ui.LoadMoreAdapter
import com.like.recyclerview.ui.LoadMoreItem
import com.like.recyclerview.utils.keepPosition
import com.like.recyclerview.utils.scrollToBottom
import com.like.recyclerview.utils.scrollToTop
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ConcatActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ConcatActivity"
    }

    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityConcatBinding>(this, R.layout.activity_concat)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(ConcatViewModel::class.java)
    }
    private val mProgressDialog by lazy {
        ProgressDialog(this)
    }
    private val mAdapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        mBinding.rv.adapter = mAdapter

        initLoadAfter()
//        initLoadBefore()
    }

    private fun initLoadAfter() {
        val contentAdapter = ContentAdapter()
        val loadMoreAdapter = LoadMoreAdapter {
            lifecycleScope.launch {
                mViewModel.loadAfterResult.loadAfter?.invoke()
            }
        }
        mAdapter.addAdapter(contentAdapter)
        mAdapter.addAdapter(loadMoreAdapter)

        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                mViewModel.loadAfterResult.refresh()
            }
        }

        lifecycleScope.launch {
            mViewModel.loadAfterResult.bind(
                onInitialOrRefresh = {
                    contentAdapter.clear()
                    contentAdapter.addAllToEnd(it)
                    mBinding.rv.scrollToTop()
                    loadMoreAdapter.clear()
                    loadMoreAdapter.addToEnd(LoadMoreItem())
                },
                onLoadMore = {
                    contentAdapter.addAllToEnd(it)
                    loadMoreAdapter.onComplete()
                },
                onLoadMoreEnd = { loadMoreAdapter.onEnd() },
                onLoadMoreError = { loadMoreAdapter.onError(it) },
                onInitialOrRefreshEmpty = {
                    ToastUtils.show("onInitialOrRefreshEmpty")
                },
                onInitialError = {
                    ToastUtils.show("onInitialError ${it.message}")
                },
                show = { mProgressDialog.show() },
                hide = { mProgressDialog.hide() },
                onFailed = { requestType, throwable ->
                    ToastUtils.show("onFailed ${throwable.message}")
                },
                onSuccess = { requestType, list ->
                    ToastUtils.show("onSuccess")
                },
            ).collect()
        }

        lifecycleScope.launch {
            mViewModel.loadAfterResult.initial()
        }
    }

    private fun initLoadBefore() {
        val contentAdapter = ContentAdapter()
        val loadMoreAdapter = LoadMoreAdapter {
            lifecycleScope.launch {
                mViewModel.loadBeforeResult.loadBefore?.invoke()
            }
        }
        mAdapter.addAdapter(loadMoreAdapter)
        mAdapter.addAdapter(contentAdapter)

        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                mViewModel.loadBeforeResult.refresh()
            }
        }

        lifecycleScope.launch {
            mViewModel.loadBeforeResult.bind(
                onInitialOrRefresh = {
                    contentAdapter.clear()
                    contentAdapter.addAllToEnd(it)
                    mBinding.rv.scrollToBottom()
                    loadMoreAdapter.clear()
                    loadMoreAdapter.addToEnd(LoadMoreItem())
                },
                onLoadMore = {
                    contentAdapter.addAllToStart(it)
                    mBinding.rv.keepPosition(it.size, 1)
                    loadMoreAdapter.onComplete()
                },
                onLoadMoreEnd = { loadMoreAdapter.onEnd() },
                onLoadMoreError = { loadMoreAdapter.onError(it) },
                onInitialOrRefreshEmpty = {
                    ToastUtils.show("onInitialOrRefreshEmpty")
                },
                onInitialError = {
                    ToastUtils.show("onInitialError ${it.message}")
                },
                show = { mProgressDialog.show() },
                hide = { mProgressDialog.hide() },
                onFailed = { requestType, throwable ->
                    ToastUtils.show("onFailed ${throwable.message}")
                },
                onSuccess = { requestType, list ->
                    ToastUtils.show("onSuccess")
                },
            ).collect()
        }
        lifecycleScope.launch {
            mViewModel.loadBeforeResult.initial()
        }
    }
}
