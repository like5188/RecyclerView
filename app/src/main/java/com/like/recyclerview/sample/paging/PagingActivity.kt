package com.like.recyclerview.sample.paging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.hjq.toast.ToastUtils
import com.like.paging.Result
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.adapter.BaseLoadAfterAdapter
import com.like.recyclerview.adapter.BaseLoadBeforeAdapter
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPagingBinding
import com.like.recyclerview.ui.util.bindData
import kotlinx.coroutines.launch

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(PagingViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initRecyclerView(
            mViewModel.loadAfterPagingResult,
            BaseLoadAfterAdapter { mViewModel.loadAfterPagingResult.loadAfter?.invoke() },
            true,
            mBinding.rv1,
            mBinding.swipeRefreshLayout1
        )
        initRecyclerView(
            mViewModel.loadBeforePagingResult,
            BaseLoadBeforeAdapter(PagingViewModel.PAGE_SIZE) { mViewModel.loadBeforePagingResult.loadBefore?.invoke() },
            false,
            mBinding.rv2,
            mBinding.swipeRefreshLayout2
        )
    }

    private fun initRecyclerView(
        result: Result<List<IRecyclerViewItem>?>,
        adapter: BaseAdapter,
        isLoadAfter: Boolean,
        recyclerView: RecyclerView,
        swipeRefreshLayout: SwipeRefreshLayout,
    ) {
        recyclerView.layoutManager = WrapLinearLayoutManager(this)
        recyclerView.addItemDecoration(ColorLineItemDecoration(10))//添加分割线
        recyclerView.adapter = adapter

        adapter.addOnItemClickListener { holder, position, data ->
            ToastUtils.show("单击 position=$position")
        }
        adapter.addOnItemLongClickListener { holder, position, data ->
            ToastUtils.show("长按 position=$position")
            adapter.remove(data)
        }

        swipeRefreshLayout.setOnRefreshListener {
            result.refresh()
        }
        lifecycleScope.launch {
            adapter.bindData(
                result,
                isLoadAfter,
                show = {
                    swipeRefreshLayout.isRefreshing = true
                },
                hide = {
                    swipeRefreshLayout.isRefreshing = false
                }
            )
        }
        result.initial()
    }
}
