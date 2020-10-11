package com.like.recyclerview.sample.tree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.like.common.util.repository.bindRecyclerViewForNotPaging
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.ext.adapter.BaseTreeRecyclerViewAdapter
import com.like.recyclerview.ext.decoration.PinnedItemDecoration
import com.like.recyclerview.ext.model.IPinnedItem
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityTreeBinding
import com.like.recyclerview.sample.databinding.TreeItem0Binding

class TreeActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityTreeBinding>(this, R.layout.activity_tree)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(TreeViewModel::class.java)
    }
    private val mAdapter: BaseAdapter by lazy { TreeRecyclerViewAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.adapter = mAdapter
        mBinding.rv.itemAnimator = null
        mBinding.rv.addItemDecoration(PinnedItemDecoration().apply {
            setOnPinnedHeaderRenderListener(object :
                PinnedItemDecoration.OnPinnedHeaderRenderListener {
                override fun onRender(
                    viewDataBinding: ViewDataBinding,
                    layoutId: Int,
                    item: IPinnedItem,
                    itemPosition: Int
                ) {
                    if (item is TreeNode0 && viewDataBinding is TreeItem0Binding && mAdapter is BaseTreeRecyclerViewAdapter) {
                        viewDataBinding.root.setOnClickListener {
                            (mAdapter as BaseTreeRecyclerViewAdapter).clickItem(
                                viewDataBinding,
                                itemPosition,
                                item
                            )
                        }
                        viewDataBinding.cb.setOnClickListener {
                            (mAdapter as BaseTreeRecyclerViewAdapter).clickCheckBox(
                                viewDataBinding.cb,
                                item
                            )
                        }
                    }
                }
            })
        })

        mViewModel.treeNotPagingResult.bindRecyclerViewForNotPaging(this, mAdapter)

        mViewModel.treeNotPagingResult.initial.invoke()
    }
}
