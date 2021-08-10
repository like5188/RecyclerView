package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.ui.util.AdapterFactory
import com.like.recyclerview.utils.UIHelper
import com.like.recyclerview.utils.add
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
    private val mAdapter by lazy {
        ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    }
    private val mUIHelper by lazy {
        UIHelper(mAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        mBinding.rv.adapter = mAdapter

//        initItems()
//        initHeadersAndItems()
        initLoadAfter()
//        initLoadAfterWithHeaders()
//        initLoadBefore()
    }

    private fun initItems() {
        val listAdapter = ItemAdapter()
        val emptyAdapter = AdapterFactory.createEmptyAdapter()
        val errorAdapter = AdapterFactory.createErrorAdapter()

        fun getData() {
            lifecycleScope.launch {
                mUIHelper.bind(
                    result = mViewModel::getItems,
                    listAdapter = listAdapter,
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

    private fun initHeadersAndItems() {
        val headerAdapter = HeaderAdapter()
        val itemAdapter = ItemAdapter()
        val contentAdapter = ConcatAdapter()
        val emptyAdapter = AdapterFactory.createEmptyAdapter()
        val errorAdapter = AdapterFactory.createErrorAdapter()

        fun getData() {
            lifecycleScope.launch {
                mUIHelper.bind(
                    result = mViewModel::getHeadersAndItems,
                    onData = {
                        var isEmpty = it.isNullOrEmpty()
                        if (!isEmpty) {
                            val headers = it.getOrNull(0)
                            val items = it.getOrNull(1)
                            if (!headers.isNullOrEmpty()) {
                                contentAdapter.add(headerAdapter)
                                headerAdapter.clear()
                                headerAdapter.addAllToEnd(headers)
                            }
                            if (!items.isNullOrEmpty()) {
                                contentAdapter.add(itemAdapter)
                                itemAdapter.clear()
                                itemAdapter.addAllToEnd(items)
                            }
                            isEmpty = headers.isNullOrEmpty() && items.isNullOrEmpty()
                        }
                        isEmpty
                    },
                    contentAdapter = contentAdapter,
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

    private fun initLoadAfter() {
        val result = mViewModel.loadAfterResult

        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                result.refresh()
            }
        }

        val listAdapter = ItemAdapter()
        val emptyAdapter = AdapterFactory.createEmptyAdapter()
        val errorAdapter = AdapterFactory.createErrorAdapter()
        val loadMoreAdapter = AdapterFactory.createLoadMoreAdapter {
            lifecycleScope.launch {
                result.loadAfter?.invoke()
            }
        }

        lifecycleScope.launch {
            mUIHelper.bindLoadAfter(
                recyclerView = mBinding.rv,
                result = result,
                listAdapter = listAdapter,
                loadMoreAdapter = loadMoreAdapter,
                emptyAdapter = emptyAdapter,
                errorAdapter = errorAdapter,
                show = { mProgressDialog.show() },
                hide = { mProgressDialog.hide() },
            ).collect()
        }
        lifecycleScope.launch {
            result.initial()
        }
    }

    private fun initLoadAfterWithHeaders() {
        val result = mViewModel.LoadAfterWithHeadersResult

        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                result.refresh()
            }
        }

        val headerAdapter = HeaderAdapter()
        val itemAdapter = ItemAdapter()
        val contentAdapter = ConcatAdapter()
        val emptyAdapter = AdapterFactory.createEmptyAdapter()
        val errorAdapter = AdapterFactory.createErrorAdapter()
        val loadMoreAdapter = AdapterFactory.createLoadMoreAdapter {
            lifecycleScope.launch {
                result.loadAfter?.invoke()
            }
        }

        lifecycleScope.launch {
            mUIHelper.bindLoadAfter(
                recyclerView = mBinding.rv,
                result = result,
                onData = {
                    if (it.isNullOrEmpty()) {
                        0
                    } else {
                        val headers = it.getOrNull(0)
                        val items = it.getOrNull(1)
                        if (!headers.isNullOrEmpty()) {
                            contentAdapter.add(headerAdapter)
                            headerAdapter.clear()
                            headerAdapter.addAllToEnd(headers)
                        }
                        if (!items.isNullOrEmpty()) {
                            contentAdapter.add(itemAdapter)
                            itemAdapter.clear()
                            itemAdapter.addAllToEnd(items)
                        }
                        if (headers.isNullOrEmpty() && items.isNullOrEmpty()) {
                            0
                        } else if (!items.isNullOrEmpty()) {
                            2
                        } else {
                            1
                        }
                    }
                },
                onLoadMore = {
                    val items = it.getOrNull(1)
                    if (!items.isNullOrEmpty()) {
                        itemAdapter.addAllToEnd(items)
                    }
                    items.isNullOrEmpty()
                },
                contentAdapter = contentAdapter,
                loadMoreAdapter = loadMoreAdapter,
                emptyAdapter = emptyAdapter,
                errorAdapter = errorAdapter,
                show = { mProgressDialog.show() },
                hide = { mProgressDialog.hide() },
            ).collect()
        }
        lifecycleScope.launch {
            result.initial()
        }
    }

    private fun initLoadBefore() {
        val result = mViewModel.loadBeforeResult

        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                result.refresh()
            }
        }

        val listAdapter = ItemAdapter()
        val emptyAdapter = AdapterFactory.createEmptyAdapter()
        val errorAdapter = AdapterFactory.createErrorAdapter()
        val loadMoreAdapter = AdapterFactory.createLoadMoreAdapter {
            lifecycleScope.launch {
                result.loadBefore?.invoke()
            }
        }

        lifecycleScope.launch {
            mUIHelper.bindLoadBefore(
                recyclerView = mBinding.rv,
                result = result,
                listAdapter = listAdapter,
                loadMoreAdapter = loadMoreAdapter,
                emptyAdapter = emptyAdapter,
                errorAdapter = errorAdapter,
                show = { mProgressDialog.show() },
                hide = { mProgressDialog.hide() },
            ).collect()
        }
        lifecycleScope.launch {
            result.initial()
        }
    }

}
