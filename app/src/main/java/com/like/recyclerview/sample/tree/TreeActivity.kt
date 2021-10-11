package com.like.recyclerview.sample.tree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.adapter.bind
import com.like.recyclerview.ext.pinned.IPinnedItem
import com.like.recyclerview.ext.pinned.PinnedItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityTreeBinding
import com.like.recyclerview.sample.databinding.TreeItem0Binding
import com.like.recyclerview.ui.util.AdapterFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TreeActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTreeBinding>(this, R.layout.activity_tree)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(TreeViewModel::class.java)
    }
    private val mAdapter by lazy {
        ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.adapter = mAdapter
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

        val flow = mAdapter.bind(
            result = mViewModel::getItems,
            itemAdapter = itemAdapter,
            emptyAdapter = AdapterFactory.createEmptyAdapter(),
            errorAdapter = AdapterFactory.createErrorAdapter(),
            show = { mBinding.swipeRefreshLayout.isRefreshing = true },
            hide = { mBinding.swipeRefreshLayout.isRefreshing = false },
        )

        fun getData() {
            lifecycleScope.launch {
                flow.collect()
            }
        }

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            getData()
        }

        getData()
    }
}