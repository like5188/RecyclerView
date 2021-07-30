package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.like.paging.RequestState
import com.like.paging.RequestType
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.sample.model.Footer
import com.like.recyclerview.utils.keepPosition
import com.like.recyclerview.utils.scrollToBottom
import kotlinx.coroutines.flow.collect
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
            mViewModel.loadAfterResult.loadAfter?.invoke()
        }
        mAdapter.addAdapter(contentAdapter)
        mAdapter.addAdapter(loadMoreAdapter)

        mBinding.btnRefresh.setOnClickListener {
            mViewModel.loadAfterResult.refresh()
        }

        lifecycleScope.launch {
            mViewModel.loadAfterResult.resultReportFlow
                .onEach { resultReport ->
                    val state = resultReport.state
                    val type = resultReport.type
                    if (type is RequestType.Initial || type is RequestType.Refresh) {
                        if (state is RequestState.Running) {
                            mProgressDialog.show()
                        } else {
                            mProgressDialog.hide()
                        }
                    }
                    when {
                        (type is RequestType.Initial || type is RequestType.Refresh) && state is RequestState.Success -> {
                            val list = state.data
                            if (list.isNullOrEmpty()) {
                                // 空视图
                            } else {
                                contentAdapter.clear()
                                contentAdapter.addAllToEnd(list)
                                loadMoreAdapter.clear()
                                loadMoreAdapter.addToEnd(Footer(1, ObservableField("onLoading")))
                            }
                        }
                        type is RequestType.Initial && state is RequestState.Failed -> {
                            // 错误视图
                        }
                        type is RequestType.After && state is RequestState.Success -> {
                            val list = state.data
                            if (list.isNullOrEmpty()) {
                                // 到底了
                                loadMoreAdapter.onEnd()
                            } else {
                                contentAdapter.addAllToEnd(list)
                                loadMoreAdapter.onComplete()
                            }
                        }
                        type is RequestType.After && state is RequestState.Failed -> {
                            loadMoreAdapter.onError(state.throwable)
                        }
                    }
                }
                .collect()
        }
        mViewModel.loadAfterResult.initial()
    }

    private fun initLoadBefore() {
        val contentAdapter = ContentAdapter()
        val loadMoreAdapter = LoadMoreAdapter {
            mViewModel.loadBeforeResult.loadBefore?.invoke()
        }
        mAdapter.addAdapter(loadMoreAdapter)
        mAdapter.addAdapter(contentAdapter)

        mBinding.btnRefresh.setOnClickListener {
            mViewModel.loadBeforeResult.refresh()
        }

        lifecycleScope.launch {
            mViewModel.loadBeforeResult.resultReportFlow
                .onEach { resultReport ->
                    val state = resultReport.state
                    val type = resultReport.type
                    if (type is RequestType.Initial || type is RequestType.Refresh) {
                        if (state is RequestState.Running) {
                            mProgressDialog.show()
                        } else {
                            mProgressDialog.hide()
                        }
                    }
                    when {
                        (type is RequestType.Initial || type is RequestType.Refresh) && state is RequestState.Success -> {
                            val list = state.data
                            if (list.isNullOrEmpty()) {
                                // 空视图
                            } else {
                                contentAdapter.clear()
                                contentAdapter.addAllToEnd(list)
                                mBinding.rv.scrollToBottom()
                                loadMoreAdapter.clear()
                                loadMoreAdapter.addToEnd(Footer(1, ObservableField("onLoading")))
                            }
                        }
                        type is RequestType.Initial && state is RequestState.Failed -> {
                            // 错误视图
                        }
                        type is RequestType.Before && state is RequestState.Success -> {
                            val list = state.data
                            if (list.isNullOrEmpty()) {
                                // 到底了
                                loadMoreAdapter.onEnd()
                            } else {
                                contentAdapter.addAllToStart(list)
                                mBinding.rv.keepPosition(list.size, 1)
                                loadMoreAdapter.onComplete()
                            }
                        }
                        type is RequestType.Before && state is RequestState.Failed -> {
                            loadMoreAdapter.onError(state.throwable)
                        }
                    }
                }
                .collect()
        }
        mViewModel.loadBeforeResult.initial()
    }
}
