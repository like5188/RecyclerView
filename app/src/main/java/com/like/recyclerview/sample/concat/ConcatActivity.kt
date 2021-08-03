package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.ToastUtils
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.ui.adapter.EmptyAdapter
import com.like.recyclerview.ui.adapter.ErrorAdapter
import com.like.recyclerview.ui.adapter.LoadMoreAdapter
import com.like.recyclerview.ui.model.EmptyItem
import com.like.recyclerview.ui.model.ErrorItem
import com.like.recyclerview.ui.model.LoadMoreItem
import com.like.recyclerview.ui.util.LoadMoreAdapterManager
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
    private val mLoadMoreAdapterManager by lazy {
        LoadMoreAdapterManager(lifecycleScope, mBinding.rv)
    }
    private val isLoadAfter = true
    private val mResult by lazy {
        if (isLoadAfter) {
            mViewModel.loadAfterResult
        } else {
            mViewModel.loadBeforeResult
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        mBinding.rv.adapter = mLoadMoreAdapterManager.getAdapter()

        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                mResult.refresh()
            }
        }

        val contentAdapters = listOf(ContentAdapter())
        val loadMoreAdapter = LoadMoreAdapter {
            lifecycleScope.launch {
                if (isLoadAfter) {
                    mResult.loadAfter?.invoke()
                } else {
                    mResult.loadBefore?.invoke()
                }
            }
        }.apply {
            addToEnd(LoadMoreItem())
        }
        val emptyAdapter = EmptyAdapter().apply {
            addToEnd(EmptyItem())
        }
        val errorAdapter = ErrorAdapter().apply {
            addToEnd(ErrorItem())
        }

        mLoadMoreAdapterManager.collect(
            isLoadAfter = isLoadAfter,
            result = mResult,
            contentAdapters = contentAdapters,
            loadMoreAdapter = loadMoreAdapter,
            emptyAdapter = emptyAdapter,
            errorAdapter = errorAdapter,
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
            onFailed = { requestType, throwable ->
                ToastUtils.show("onFailed ${throwable.message}")
            },
            onSuccess = { requestType, list ->
                ToastUtils.show("onSuccess")
            },
        )
    }

}
