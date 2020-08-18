package com.like.recyclerview.sample.paging

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.adapter.BaseLoadAfterAdapter
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.ui.bindRecyclerViewForLoadAfterPaging
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.listener.OnItemClickListener
import com.like.recyclerview.model.IItem
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityPagingBinding
import com.like.recyclerview.sample.util.bindProgress
import com.like.recyclerview.viewholder.CommonViewHolder

class PagingActivity : AppCompatActivity() {
    private val mBinding by lazy {
        DataBindingUtil.setContentView<ActivityPagingBinding>(this, R.layout.activity_paging)
    }
    private val mViewModel by lazy {
        ViewModelProvider(this).get(PagingViewModel::class.java)
    }
    private val mAdapter: BaseAdapter by lazy {
        BaseLoadAfterAdapter { mViewModel.getResult().loadAfter?.invoke() }
//        BaseLoadBeforeAdapter(PagingViewModel.PAGE_SIZE) { mViewModel.getResult().loadBefore?.invoke() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(10))//添加分割线
        mBinding.rv.adapter = mAdapter

        mViewModel.getResult().bindProgress(this, mBinding.swipeRefreshLayout)
        mViewModel.getResult().bindRecyclerViewForLoadAfterPaging(
            this, mAdapter,
            listener = object : OnItemClickListener {
                override fun onItemClick(holder: CommonViewHolder, position: Int, data: IRecyclerViewItem?) {
                    if (data is IItem) {
                        mAdapter.mAdapterDataManager.remove(position)
                    }
                }
            }
        )
//        mViewModel.getResult().bindRecyclerViewForLoadBeforePaging(
//            this, mAdapter,
//            listener = object : OnItemClickListener {
//                override fun onItemClick(holder: CommonViewHolder, position: Int, data: IRecyclerViewItem?) {
//                    if (data is IItem) {
//                        mAdapter.mAdapterDataManager.remove(position)
//                    }
//                }
//            }
//        )

        mViewModel.getResult().loadInitial.invoke()
    }
}
