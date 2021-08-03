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
    private val isLoadAfter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        mBinding.rv.adapter = mLoadMoreAdapterManager.getAdapter()

        val result = if (isLoadAfter) {
            mViewModel.loadAfterResult
        } else {
            mViewModel.loadBeforeResult
        }

        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                result.refresh()
            }
        }

        val contentAdapters = listOf(ContentAdapter())
        val emptyAdapter = EmptyAdapter().apply {
            addToEnd(EmptyItem())
        }
        val errorAdapter = ErrorAdapter().apply {
            addToEnd(ErrorItem())
        }
        val loadMoreAdapter = LoadMoreAdapter {
            lifecycleScope.launch {
                if (isLoadAfter) {
                    result.loadAfter?.invoke()
                } else {
                    result.loadBefore?.invoke()
                }
            }
        }.apply {
            addToEnd(LoadMoreItem())
        }

        mLoadMoreAdapterManager.collect(
            isLoadAfter = isLoadAfter,
            result = result,
            contentAdapters = contentAdapters,
            emptyAdapter = emptyAdapter,
            errorAdapter = errorAdapter,
            loadMoreAdapter = loadMoreAdapter,
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
