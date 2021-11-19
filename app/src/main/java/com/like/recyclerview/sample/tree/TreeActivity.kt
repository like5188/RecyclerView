package com.like.recyclerview.sample.tree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.ToastUtils
import com.like.recyclerview.ext.pinned.IPinnedItem
import com.like.recyclerview.ext.pinned.PinnedItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityTreeBinding
import com.like.recyclerview.sample.databinding.TreeItem0Binding
import com.like.recyclerview.ui.util.AdapterFactory
import com.like.recyclerview.utils.bindFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

class TreeActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTreeBinding>(this, R.layout.activity_tree)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(TreeViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.itemAnimator = null

        val itemAdapter = TreeRecyclerViewAdapter()

        mBinding.rv.addItemDecoration(PinnedItemDecoration(itemAdapter).apply {
            setOnPinnedHeaderRenderListener(object :
                PinnedItemDecoration.OnPinnedItemRenderListener {
                override fun onRender(
                    viewDataBinding: ViewDataBinding,
                    layoutId: Int,
                    item: IPinnedItem,
                    itemPosition: Int,
                ) {
                    if (item is TreeNode0 && viewDataBinding is TreeItem0Binding) {
                        viewDataBinding.root.setOnClickListener {
                            itemAdapter.clickItem(
                                viewDataBinding,
                                itemPosition,
                                item
                            )
                        }
                        viewDataBinding.cb.setOnClickListener {
                            itemAdapter.clickCheckBox(
                                viewDataBinding.cb,
                                item
                            )
                        }
                    }
                }
            })
        })

        val requestHandler = mBinding.rv.bindFlow(
            dataFlow = mViewModel::getItems.asFlow(),
            itemAdapter = itemAdapter,
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mBinding.swipeRefreshLayout.isRefreshing = true },
            hide = { mBinding.swipeRefreshLayout.isRefreshing = false },
            onError = { requestType, throwable ->
                ToastUtils.show(throwable.message)
            }
        )
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            lifecycleScope.launch {
                requestHandler.refresh()
            }
        }
        lifecycleScope.launch {
            requestHandler.initial()
        }
    }
}