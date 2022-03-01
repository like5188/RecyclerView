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
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.ProgressDialog
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding
import com.like.recyclerview.ui.util.AdapterFactory
import com.like.recyclerview.utils.bindAfterPagingResult
import com.like.recyclerview.utils.bindBeforePagingResult
import com.like.recyclerview.utils.bindFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
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
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
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
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
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
            itemAdapter = ItemAdapter(),
            loadMoreAdapter = AdapterFactory.createLoadMoreAdapter(),
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
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

    private fun initLoadAfterWithHeaders() {
        val requestHandler = mBinding.rv.bindAfterPagingResult(
            pagingResult = mViewModel.LoadAfterWithHeadersResult,
            concatAdapter = mAdapter,
            headerAdapter = HeaderAdapter(),
            itemAdapter = ItemAdapter(),
            loadMoreAdapter = AdapterFactory.createLoadMoreAdapter(),
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
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
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mProgressDialog.show() },
            hide = { mProgressDialog.hide() },
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
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
