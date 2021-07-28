package com.like.recyclerview.sample.concat

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.decoration.ColorLineItemDecoration
import com.like.recyclerview.layoutmanager.WrapLinearLayoutManager
import com.like.recyclerview.listener.OnLoadMoreListener
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ActivityConcatBinding

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding.rv.layoutManager = WrapLinearLayoutManager(this)
        mBinding.rv.addItemDecoration(ColorLineItemDecoration(0, 1, Color.BLACK))//添加分割线
        var i = 0
        mBinding.rv.adapter = ConcatAdapter(
            ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build(),
            HeaderAdapter(),
            ContentAdapter(),
            FooterAdapter().apply {
                onLoadMore = {
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
        )
    }

}
