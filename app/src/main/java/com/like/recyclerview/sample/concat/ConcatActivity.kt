package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.adapter.bind
import com.like.recyclerview.adapter.bindLoadAfter
import com.like.recyclerview.adapter.bindLoadBefore
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.ui.util.AdapterFactory
import com.like.recyclerview.utils.add
import com.like.recyclerview.utils.clear
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
        val flow = mAdapter.bind(
            result = mViewModel::getItems,
            itemAdapter = ItemAdapter(),
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
        )

        fun getData() {
            lifecycleScope.launch {
                flow.collect()
            }
        }

        mBinding.btnRefresh.setOnClickListener {
            getData()
        }

        getData()
    }

    private fun initHeadersAndItems() {
        val flow = mAdapter.bind(
            result = mViewModel::getHeadersAndItems,
            headerAdapter = HeaderAdapter(),
            itemAdapter = ItemAdapter(),
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
        )

        fun getData() {
            lifecycleScope.launch {
                flow.collect()
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
            mAdapter.bindLoadAfter(
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
            mAdapter.bindLoadAfter(
                recyclerView = mBinding.rv,
                result = result,
                onInitialOrRefreshSuccess = {
                    if (it.isNullOrEmpty()) {
                        0
                    } else {
                        val headers = it.getOrNull(0)
                        val items = it.getOrNull(1)
                        contentAdapter.clear()
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
                onLoadMoreSuccess = {
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
            mAdapter.bindLoadBefore(
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
