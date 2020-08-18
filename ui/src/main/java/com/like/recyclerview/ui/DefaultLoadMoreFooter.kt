package com.like.recyclerview.ui

import android.view.View
import androidx.annotation.ColorRes
import androidx.databinding.ObservableInt
import com.like.recyclerview.ui.BR
import com.like.recyclerview.ui.R
import com.like.recyclerview.model.ILoadMoreFooter

/**
 * 用于 RecyclerView 的往后加载更多的视图
 *
 * @param loadingTip        加载中的描述
 * @param noMoreDataTip     没有数据了的描述
 * @param retryTip          加载失败需要重试的描述
 * @param retry             加载失败重试操作
 */
data class DefaultLoadMoreFooter(
    val loadingTip: String = "加载中……",
    val noMoreDataTip: String = "到底啦",
    val retryTip: String = "加载失败，点击重试！",
    @ColorRes val bgColor: Int = R.color.recyclerview_bg_white_0,
    @ColorRes val progressBarBgColor: Int = R.color.recyclerview_bg_gray_0,
    @ColorRes val textColor: Int = R.color.recyclerview_text_gray_0,
    val textSize: Float = 16f,
    val retry: () -> Unit
) : ILoadMoreFooter {
    override val layoutId: Int = R.layout.recyclerview_default_loadmore_footer
    override fun variableId(): Int = BR.loadMoreFooter
    val status: ObservableInt = ObservableInt(0)

    fun click(view: View) {
        retry()
        onLoading()
    }

    override fun onLoading() {
        status.set(0)
    }

    override fun onEnd() {
        status.set(1)
    }

    override fun onError() {
        status.set(2)
    }

}