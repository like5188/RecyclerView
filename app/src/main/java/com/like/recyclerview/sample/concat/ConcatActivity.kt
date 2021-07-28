package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.like.paging.RequestState
import com.like.paging.RequestType
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.listener.OnLoadMoreListener
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import kotlinx.coroutines.flow.onEach
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
    private val mAdapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        var i = 0
        mBinding.rv.adapter = mAdapter

        val contentAdapter = ContentAdapter()
        val footerAdapter = FooterAdapter().apply {
            onLoadMore = {
                mViewModel.loadAfterPagingResult.loadAfter?.invoke()
                Log.e(TAG, "onLoadMore")
                i++ > 3
            }
            onLoadMoreListener = object : OnLoadMoreListener {
                override fun onLoading() {
                    Log.e(TAG, "onLoading")
                }

                override fun onComplete() {
                    Log.e(TAG, "onComplete")
                }

                override fun onEnd() {
                    Log.e(TAG, "onEnd")
                }

                override fun onError(throwable: Throwable) {
                    Log.e(TAG, "onError $throwable")
                }
            }
        }
        mAdapter.addAdapter(contentAdapter)
        mAdapter.addAdapter(footerAdapter)

        lifecycleScope.launch {
            mViewModel.loadAfterPagingResult.resultReportFlow.onEach { resultReport ->
                val state = resultReport.state
                val type = resultReport.type
                when {
                    (type is RequestType.Initial || type is RequestType.Refresh) && state is RequestState.Success -> {
                        val list = state.data
                        if (list.isNullOrEmpty()) {
                        } else {
                            contentAdapter.addItems(list)
                        }
                    }
                    type is RequestType.Initial && state is RequestState.Failed -> {
                    }
                    type is RequestType.After && state is RequestState.Success -> {
                        val list = state.data
                        if (list.isNullOrEmpty()) {
                            // 到底了
                        } else {
                            contentAdapter.addItems(list)
                        }
                    }
                    type is RequestType.After && state is RequestState.Failed -> {
                    }
                    type is RequestType.Before && state is RequestState.Success -> {
                    }
                    type is RequestType.Before && state is RequestState.Failed -> {
                    }
                }
            }
        }
    }
}
