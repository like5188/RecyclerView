package com.like.recyclerview.sample.concat

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.ToastUtils
import com.like.common.util.Logger
import com.like.common.util.UiStatus
import com.like.common.util.gone
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.concat.vo.Item1
import com.like.recyclerview.sample.concat.vo.Item2
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.sample.databinding.ViewUiStatusBinding
import com.like.recyclerview.ui.adapter.BaseUiStatusController
import com.like.recyclerview.ui.adapter.UiStatusCombineAdapter
import com.like.recyclerview.ui.loadstate.LoadStateAdapter
import com.like.recyclerview.ui.loadstate.LoadStateItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ConcatActivity : AppCompatActivity() {
    companion object {
        const val TAG_UI_STATUS_EMPTY = "tag_ui_status_empty"
        const val TAG_UI_STATUS_ERROR = "tag_ui_status_error"
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
    private val listAdapter by lazy {
        ItemAdapter()
    }
    private val loadStateAdapter by lazy {
        LoadStateAdapter(LoadStateItem())
    }

    private val adapter by lazy {
        val uiStatusController = object : BaseUiStatusController(mBinding.rv) {
            override fun addUiStatus(context: Context, refresh: suspend () -> Unit) {
                addUiStatus(TAG_UI_STATUS_EMPTY, UiStatus<ViewUiStatusBinding>(context, R.layout.view_ui_status).apply {
                    dataBinding.iv.setImageResource(R.drawable.common_back)
                    dataBinding.tvDes.text = "暂无数据~"
                    dataBinding.tvFun.gone()
                    dataBinding.tvTitle.gone()
                })
                addUiStatus(TAG_UI_STATUS_ERROR, UiStatus<ViewUiStatusBinding>(context, R.layout.view_ui_status).apply {
                    dataBinding.iv.setImageResource(R.drawable.common_back)
                    dataBinding.tvDes.text = "加载失败"
                    dataBinding.tvFun.text = "刷新试试"
                    dataBinding.tvFun.setOnClickListener {
                        if (context is LifecycleOwner) {
                            (context as LifecycleOwner).lifecycleScope.launch {
                                refresh()
                            }
                        }
                    }
                    dataBinding.tvTitle.gone()
                })
                addUiStatus(TAG_UI_STATUS_LOADING, UiStatus<ViewUiStatusBinding>(context, R.layout.view_ui_status).apply {
                    dataBinding.iv.setImageResource(R.drawable.common_back)
                    dataBinding.tvDes.text = "正在奋力加载中..."
                    dataBinding.tvFun.gone()
                    dataBinding.tvTitle.gone()
                })
            }

            override fun getEmptyStatusTag(): String {
                return TAG_UI_STATUS_EMPTY
            }

            override fun getLoadingStatusTag(): String {
                return TAG_UI_STATUS_LOADING
            }

            override fun getErrorStatusTag(throwable: Throwable): String {
                return TAG_UI_STATUS_ERROR
            }
        }
        object : UiStatusCombineAdapter<IRecyclerViewItem>(uiStatusController) {
            override fun getItems(list: List<IRecyclerViewItem>?): List<IRecyclerViewItem>? {
                return list?.filter { it is Item1 || it is Item2 }
            }

            override fun onErrorStatusShown(throwable: Throwable) {
                uiStatusController.getDataBinding<ViewUiStatusBinding>(TAG_UI_STATUS_ERROR)?.apply {
                    tvDes.text = throwable.message
                }
            }
        }.apply {
            attachedToRecyclerView(mBinding.rv)
            withListAdapter(listAdapter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        mBinding.rv.adapter = adapter.concatAdapter
//        initItems()
//        initHeadersAndItems()
        initLoadAfter()
//        initLoadAfterWithHeaders()
//        initLoadBefore()
        mBinding.btnRefresh.setOnClickListener {
            lifecycleScope.launch {
                adapter.refresh()
            }
        }
//        testFlow()
    }

    private fun testFlow() {
        lifecycleScope.launchWhenResumed {
            (0..3).asFlow()
                .map {
                    delay(1000)
                    if (it == 2) {
                        throw RuntimeException("test error")
                    }
                    it
                }.retryWhen { cause, attempt ->
                    Logger.v("retryWhen cause=$cause attempt=$attempt")
                    cause.message == "test error" && attempt == 0L
                }.onStart {
                    Logger.w("onStart")
                }.onCompletion {
                    Logger.w("onCompletion $it")
                }.catch {
                    Logger.e("catch $it")
                    throw it
                }.onStart {
                    Logger.w("1 onStart")
                }.onCompletion {
                    Logger.w("1 onCompletion $it")
                }.catch {
                    Logger.e("1 catch $it")
                }.collect {
                    Logger.e("collect $it")
                }
        }
    }

    private fun initItems() {
        adapter.apply {
            show = { mProgressDialog.show() }
            hide = { mProgressDialog.hide() }
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }

        }
        lifecycleScope.launchWhenResumed {
            adapter.submitData(mViewModel::getItems.asFlow().map {
                it?.take(3)
            }.retryWhen { cause, attempt ->
                Logger.e("retryWhen")
                cause.message == "load error 0" && attempt == 0L
            }.flowOn(Dispatchers.IO))
        }
    }

    private fun initHeadersAndItems() {
        adapter.apply {
            show = { mProgressDialog.show() }
            hide = { mProgressDialog.hide() }
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
        }
        lifecycleScope.launchWhenResumed {
            adapter.submitData(mViewModel::getHeadersAndItems.asFlow())
        }
    }

    private fun initLoadAfter() {
        adapter.apply {
            show = { mProgressDialog.show() }
            hide = { mProgressDialog.hide() }
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
            withLoadStateFooter(loadStateAdapter)
        }
        lifecycleScope.launchWhenResumed {
            adapter.submitData(mViewModel.loadAfterResult.apply {
                flow = flow.map {
                    it?.take(5)
                }.retryWhen { cause, attempt ->
                    Logger.e("retryWhen")
                    cause.message == "load error 0" && attempt == 0L
                }.flowOn(Dispatchers.IO)
            })
        }
    }

    private fun initLoadAfterWithHeaders() {
        adapter.apply {
            show = { mProgressDialog.show() }
            hide = { mProgressDialog.hide() }
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
            withLoadStateFooter(loadStateAdapter)
        }
        lifecycleScope.launchWhenResumed {
            adapter.submitData(mViewModel.LoadAfterWithHeadersResult)
        }
    }

    private fun initLoadBefore() {
        adapter.apply {
            show = { mProgressDialog.show() }
            hide = { mProgressDialog.hide() }
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
            withLoadStateHeader(loadStateAdapter)
        }
        lifecycleScope.launchWhenResumed {
            adapter.submitData(mViewModel.loadBeforeResult.apply {
                flow = flow.map {
                    it?.take(20)
                }.retryWhen { cause, attempt ->
                    Logger.e("retryWhen")
                    cause.message == "load error 0" && attempt == 0L
                }.flowOn(Dispatchers.IO)
            })
        }
    }

}
