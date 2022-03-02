package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.hjq.toast.ToastUtils
import com.like.common.util.Logger
import com.like.common.util.gone
import com.like.common.util.visible
import com.like.paging.RequestType
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.sample.databinding.ViewUiStatusBinding
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
        const val TAG_UI_STATUS_NOT_FOUND_ERROR = "tag_ui_status_not_found_error"
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
        val uiStatusController: DefaultUiStatusController? by lazy {
            DefaultUiStatusController(mBinding.rv)
        }
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
                if (uiStatusController == null) {
                    mProgressDialog.show()
                } else {
                    uiStatusController?.show = { mProgressDialog.show() }
                    uiStatusController?.showUiStatus(TAG_UI_STATUS_LOADING)
                }
            },
            hide = { mProgressDialog.hide() },
            onError = { requestType, throwable, requestHandler ->
                ToastUtils.show(throwable.message)
                uiStatusController?.apply {
                    this.refresh = {
                        requestHandler.refresh()
                    }
                    if ((requestType is RequestType.Initial || requestType is RequestType.Refresh) && itemAdapter.itemCount <= 0) {
                        // 初始化或者刷新失败时，如果当前显示的是列表，则不处理，否则显示[errorAdapter]
                        showUiStatus(TAG_UI_STATUS_ERROR)
                        getDataBinding<ViewUiStatusBinding>(TAG_UI_STATUS_ERROR)?.apply {
                            tvDes.text = throwable.message
                        }
                    } else {
                        showContent()
                    }
                }
            }
        ) { requestType, resultType, requestHandler ->
            uiStatusController?.apply {
                this.refresh = {
                    requestHandler.refresh()
                }
                if ((requestType is RequestType.Initial || requestType is RequestType.Refresh) &&
                    (resultType == null || (resultType is List<*> && resultType.isEmpty()))
                ) {
                    // 显示空视图
                    showUiStatus(TAG_UI_STATUS_EMPTY)
                } else {
                    showContent()
                }
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

    class DefaultUiStatusController(view: View) : UiStatusController(view) {
        var clickRefreshBtn = false
        var show: (() -> Unit)? = null
        var refresh: (suspend () -> Unit)? = null

        override fun showUiStatus(tag: String) {// 加载中需要特殊处理
            if (tag == TAG_UI_STATUS_LOADING && !clickRefreshBtn) {
                show?.invoke()
            } else {
                super.showUiStatus(tag)
            }
        }

        init {
            addUiStatus(TAG_UI_STATUS_EMPTY, UiStatus<ViewUiStatusBinding>(view.context, R.layout.view_ui_status).apply {
                dataBinding.iv.setImageResource(R.drawable.common_back)
                dataBinding.tvDes.text = "暂无数据~"
                dataBinding.tvFun.gone()
                dataBinding.tvTitle.gone()
            })
            addUiStatus(TAG_UI_STATUS_ERROR, UiStatus<ViewUiStatusBinding>(view.context, R.layout.view_ui_status).apply {
                dataBinding.iv.setImageResource(R.drawable.common_back)
                dataBinding.tvDes.text = "加载失败"
                dataBinding.tvFun.text = "刷新试试"
                dataBinding.tvFun.setOnClickListener {
                    if (view.context is LifecycleOwner) {
                        (view.context as LifecycleOwner).lifecycleScope.launch {
                            clickRefreshBtn = true
                            refresh?.invoke()
                            clickRefreshBtn = false
                        }
                    }
                }
                dataBinding.tvTitle.gone()
            })
            addUiStatus(
                TAG_UI_STATUS_NETWORK_ERROR,
                UiStatus<ViewUiStatusBinding>(view.context, R.layout.view_ui_status).apply {
                    dataBinding.iv.setImageResource(R.drawable.common_back)
                    dataBinding.tvDes.text = "你目前暂无网络"
                    dataBinding.tvFun.text = "刷新试试"
                    dataBinding.tvFun.visible()
                    dataBinding.tvFun.setOnClickListener {
                        if (view.context is LifecycleOwner) {
                            (view.context as LifecycleOwner).lifecycleScope.launch {
                                clickRefreshBtn = true
                                refresh?.invoke()
                                clickRefreshBtn = false
                            }
                        }
                    }
                    dataBinding.tvTitle.gone()
                })
            addUiStatus(TAG_UI_STATUS_LOADING, UiStatus<ViewUiStatusBinding>(view.context, R.layout.view_ui_status).apply {
                dataBinding.iv.setImageResource(R.drawable.common_back)
                dataBinding.tvDes.text = "正在奋力加载中..."
                dataBinding.tvFun.gone()
                dataBinding.tvTitle.gone()
            })
            addUiStatus(
                TAG_UI_STATUS_NOT_FOUND_ERROR,
                UiStatus<ViewUiStatusBinding>(view.context, R.layout.view_ui_status).apply {
                    dataBinding.iv.setImageResource(R.drawable.common_back)
                    dataBinding.tvTitle.text = "404错误页面"
                    dataBinding.tvDes.text = "Sorry您访问的页面不见了~"
                    dataBinding.tvFun.text = "刷新试试"
                    dataBinding.tvFun.setOnClickListener {
                        if (view.context is LifecycleOwner) {
                            (view.context as LifecycleOwner).lifecycleScope.launch {
                                clickRefreshBtn = true
                                refresh?.invoke()
                                clickRefreshBtn = false
                            }
                        }
                    }
                })
        }
    }
}
