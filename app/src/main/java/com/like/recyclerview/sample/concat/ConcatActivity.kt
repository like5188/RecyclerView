package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
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
import com.like.recyclerview.utils.UIHelper
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
    private val mAdapter by lazy {
        ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    }
    private val mUIHelper by lazy {
        UIHelper(mAdapter)
    }
    private val isLoadAfter = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        mBinding.rv.adapter = mAdapter

        initLoad()
//        initLoadMore()
    }

    private fun initLoad() {
        val headerAdapter = HeaderAdapter()
        val listAdapter = ListAdapter()
        val emptyAdapter = EmptyAdapter().apply {
            addToEnd(EmptyItem())
        }
        val errorAdapter = ErrorAdapter().apply {
            addToEnd(ErrorItem())
        }

        fun getData() {
            lifecycleScope.launch {
                mUIHelper.collect(
                    result = mViewModel::getData,
                    listAdapter = listAdapter,
                    emptyAdapter = emptyAdapter,
                    errorAdapter = errorAdapter,
                    show = { mProgressDialog.show() },
                    hide = { mProgressDialog.hide() },
                )
            }
            lifecycleScope.launch {
                mUIHelper.collect(
                    result = mViewModel::getHeader,
                    listAdapter = headerAdapter,
                    emptyAdapter = emptyAdapter,
                    errorAdapter = errorAdapter,
                    show = { mProgressDialog.show() },
                    hide = { mProgressDialog.hide() },
                )
            }
        }

        mBinding.btnRefresh.setOnClickListener {
            getData()
        }

        getData()
    }

    private fun initLoadMore() {
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

        val listAdapter = ListAdapter()
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

        lifecycleScope.launch {
            mUIHelper.collect(
                recyclerView = mBinding.rv,
                isLoadAfter = isLoadAfter,
                result = result,
                listAdapter = listAdapter,
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

}
