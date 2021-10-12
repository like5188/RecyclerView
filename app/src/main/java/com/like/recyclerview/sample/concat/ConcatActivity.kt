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
//        initLoadAfter()
//        initLoadAfterWithHeaders()
        initLoadBefore()
    }

    private fun initItems() {
        val flow = mAdapter.bind(
            recyclerView = mBinding.rv,
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
            recyclerView = mBinding.rv,
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

        val flow = mAdapter.bindLoadAfter(
            recyclerView = mBinding.rv,
            result = result,
            itemAdapter = ItemAdapter(),
            loadMoreAdapter = AdapterFactory.createLoadMoreAdapter {
                result.loadAfter?.invoke()
            },
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
        )
        lifecycleScope.launch {
            flow.collect()
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

        val flow = mAdapter.bindLoadAfter(
            recyclerView = mBinding.rv,
            result = result,
            headerAdapter = HeaderAdapter(),
            itemAdapter = ItemAdapter(),
            loadMoreAdapter = AdapterFactory.createLoadMoreAdapter {
                result.loadAfter?.invoke()
            },
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
        )

        lifecycleScope.launch {
            flow.collect()
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

        val flow = mAdapter.bindLoadBefore(
            recyclerView = mBinding.rv,
            result = result,
            itemAdapter = ItemAdapter(),
            loadMoreAdapter = AdapterFactory.createLoadMoreAdapter {
                result.loadBefore?.invoke()
            },
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
        )
        lifecycleScope.launch {
            flow.collect()
        }
        lifecycleScope.launch {
            result.initial()
        }
    }

}
