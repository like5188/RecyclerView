package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.hjq.toast.ToastUtils
import com.like.common.util.Logger
import com.like.common.util.UiStatus
import com.like.common.util.UiStatusController
import com.like.paging.RequestType
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.sample.databinding.ViewEmptyBinding
import com.like.recyclerview.sample.databinding.ViewErrorBinding
import com.like.recyclerview.ui.util.AdapterFactory
import com.like.recyclerview.utils.bindAfterPagingResult
import com.like.recyclerview.utils.bindBeforePagingResult
import com.like.recyclerview.utils.bindFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.launch

class ConcatActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ConcatActivity"
        const val TAG_UI_STATUS_EMPTY = "tag_ui_status_empty"
        const val TAG_UI_STATUS_ERROR = "tag_ui_status_error"
        const val TAG_UI_STATUS_NETWORK_ERROR = "tag_ui_status_network_error"
        const val TAG_UI_STATUS_LOADING = "tag_ui_status_loading"
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
    private val uiStatusController by lazy {
        UiStatusController(mBinding.rv).apply {
            addUiStatus(TAG_UI_STATUS_EMPTY, UiStatus(R.layout.view_empty))
            addUiStatus(TAG_UI_STATUS_ERROR, UiStatus(R.layout.view_error))
            addUiStatus(TAG_UI_STATUS_NETWORK_ERROR, UiStatus(R.layout.view_network_error))
            addUiStatus(TAG_UI_STATUS_LOADING, UiStatus(R.layout.view_loading))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        mBinding.rv.adapter = mAdapter

//        lifecycleScope.launchWhenResumed {
//            (0..3).asFlow()
//                .map {
//                    delay(1000)
//                    if (it == 2) {
//                        throw RuntimeException("test error")
//                    }
//                    it
//                }.retryWhen { cause, attempt ->
//                    Logger.v("retryWhen cause=$cause attempt=$attempt")
//                    cause.message == "test error" && attempt == 0L
//                }.onStart {
//                    Logger.w("onStart")
//                }.onCompletion {
//                    Logger.w("onCompletion $it")
//                }.catch {
//                    Logger.e("catch $it")
//                    throw it
//                }.onStart {
//                    Logger.w("1 onStart")
//                }.onCompletion {
//                    Logger.w("1 onCompletion $it")
//                }.catch {
//                    Logger.e("1 catch $it")
//                }.collect {
//                    Logger.e("collect $it")
//                }
//        }

//        initItems()
//        initHeadersAndItems()
        initLoadAfter()
//        initLoadAfterWithHeaders()
//        initLoadBefore()
    }

    private fun initItems() {
        val requestHandler = mBinding.rv.bindFlow(
            dataFlow = mViewModel::getItems.asFlow().map {
                it?.take(3)
            }.retryWhen { cause, attempt ->
                Logger.e("retryWhen")
                cause.message == "load error 0" && attempt == 0L
            }.flowOn(Dispatchers.IO),
            concatAdapter = mAdapter,
            itemAdapter = ItemAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
        )
        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                requestHandler.refresh()
            }
        }
        lifecycleScope.launch {
            requestHandler.initial()
        }
    }

    private fun initHeadersAndItems() {
        val requestHandler = mBinding.rv.bindFlow(
            dataFlow = mViewModel::getHeadersAndItems.asFlow(),
            concatAdapter = mAdapter,
            headerAdapter = HeaderAdapter(),
            itemAdapter = ItemAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
        )

        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                requestHandler.refresh()
            }
        }

        lifecycleScope.launch {
            requestHandler.initial()
        }
    }

    private fun initLoadAfter() {
        var clickRefreshBtn = false
        val itemAdapter = ItemAdapter()
        val requestHandler = mBinding.rv.bindAfterPagingResult(
            pagingResult = mViewModel.loadAfterResult.apply {
                flow = flow.map {
                    it?.take(5)
                }.retryWhen { cause, attempt ->
                    Logger.e("retryWhen")
                    cause.message == "load error 0" && attempt == 0L
                }.flowOn(Dispatchers.IO)
            },
            concatAdapter = mAdapter,
            itemAdapter = itemAdapter,
            loadMoreAdapter = AdapterFactory.createLoadMoreAdapter(),
            show = {
                if (clickRefreshBtn) {
                    uiStatusController.showUiStatus(TAG_UI_STATUS_LOADING)
                } else {
                    mProgressDialog.show()
                }
            },
            hide = { mProgressDialog.hide() },
            onError = { requestType, throwable, requestHandler ->
                ToastUtils.show(throwable.message)
                if ((requestType is RequestType.Initial || requestType is RequestType.Refresh) && itemAdapter.itemCount <= 0) {
                    // 初始化或者刷新失败时，如果当前显示的是列表，则不处理，否则显示[errorAdapter]
                    uiStatusController.showUiStatus(TAG_UI_STATUS_ERROR)
                    uiStatusController.getDataBinding<ViewErrorBinding>(TAG_UI_STATUS_ERROR)?.apply {
                        tv.text = throwable.message
                        btn.setOnClickListener {
                            clickRefreshBtn = true
                            lifecycleScope.launch {
                                requestHandler.refresh()
                                clickRefreshBtn = false
                            }
                        }
                    }
                } else {
                    uiStatusController.showContent()
                }
            }
        ) { requestType, resultType, requestHandler ->
            if ((requestType is RequestType.Initial || requestType is RequestType.Refresh) && resultType.isNullOrEmpty()) {
                // 显示空视图
                uiStatusController.showUiStatus(TAG_UI_STATUS_EMPTY)
                uiStatusController.getDataBinding<ViewEmptyBinding>(TAG_UI_STATUS_EMPTY)?.apply {
                    tv.text = "没有菜啦~快上菜！"
                }
            } else {
                uiStatusController.showContent()
            }
        }
        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                requestHandler.refresh()
            }
        }
        lifecycleScope.launch {
            requestHandler.initial()
        }
    }

    private fun initLoadAfterWithHeaders() {
        val requestHandler = mBinding.rv.bindAfterPagingResult(
            pagingResult = mViewModel.LoadAfterWithHeadersResult,
            concatAdapter = mAdapter,
            headerAdapter = HeaderAdapter(),
            itemAdapter = ItemAdapter(),
            loadMoreAdapter = AdapterFactory.createLoadMoreAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
        )
        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                requestHandler.refresh()
            }
        }
        lifecycleScope.launch {
            requestHandler.initial()
        }
    }

    private fun initLoadBefore() {
        val requestHandler = mBinding.rv.bindBeforePagingResult(
            pagingResult = mViewModel.loadBeforeResult.apply {
                flow = flow.map {
                    it?.take(3)
                }.retryWhen { cause, attempt ->
                    Logger.e("retryWhen")
                    cause.message == "load error 0" && attempt == 0L
                }.flowOn(Dispatchers.IO)
            },
            concatAdapter = mAdapter,
            itemAdapter = ItemAdapter(),
            loadMoreAdapter = AdapterFactory.createLoadMoreAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
        )
        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                requestHandler.refresh()
            }
        }
        lifecycleScope.launch {
            requestHandler.initial()
        }
    }

}
