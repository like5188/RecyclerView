package com.like.recyclerview.sample.util

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.common.util.shortToastCenter
import com.like.repository.Result

/**
 * 把 [Result] 与 [SwipeRefreshLayout] 进行绑定
 * 根据 [Result.liveState] 的值，在初始化或者刷新时控制进度条的显示隐藏，错误时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bindProgress(
    lifecycleOwner: LifecycleOwner,
    swipeRefreshLayout: SwipeRefreshLayout,
    @ColorInt vararg colors: Int = intArrayOf(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW),
    onSuccess: ((ResultType?) -> Unit)? = null
) {
    swipeRefreshLayout.setColorSchemeColors(*colors)

    swipeRefreshLayout.setOnRefreshListener {
        this.refresh()
    }

    this.bind(
        lifecycleOwner,
        onSuccess,
        { requestType, throwable ->
            val errorMsg = if (throwable?.message.isNullOrEmpty()) {
                "unknown error"
            } else {
                throwable?.message
            }
            swipeRefreshLayout.context?.shortToastCenter(errorMsg)
        },
        { swipeRefreshLayout.isRefreshing = true },
        { swipeRefreshLayout.isRefreshing = false }
    )
}

/**
 * 把 [Result] 与 [com.like.common.base.BaseDialogFragment] 进行绑定
 * 根据 [Result.liveState] 的值，在初始化或者刷新时控制进度条的显示隐藏，错误时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bindProgress(
    fragment: Fragment,
    progressDialogFragment: BaseDialogFragment,
    onSuccess: ((ResultType?) -> Unit)? = null
) {
    this.bind(
        fragment,
        onSuccess,
        { requestType, throwable ->
            val errorMsg = if (throwable?.message.isNullOrEmpty()) {
                "unknown error"
            } else {
                throwable?.message
            }
            fragment.context?.shortToastCenter(errorMsg)
        },
        { progressDialogFragment.show(fragment) },
        { progressDialogFragment.dismiss() }
    )
}

/**
 * 把 [Result] 与 [com.like.common.base.BaseDialogFragment] 进行绑定
 * 根据 [Result.liveState] 的值，在初始化或者刷新时控制进度条的显示隐藏，错误时进行 [android.widget.Toast] 提示
 */
fun <ResultType> Result<ResultType>.bindProgress(
    activity: FragmentActivity,
    progressDialogFragment: BaseDialogFragment,
    onSuccess: ((ResultType?) -> Unit)? = null
) {
    this.bind(
        activity,
        onSuccess,
        { requestType, throwable ->
            val errorMsg = if (throwable?.message.isNullOrEmpty()) {
                "unknown error"
            } else {
                throwable?.message
            }
            activity.shortToastCenter(errorMsg)
        },
        { progressDialogFragment.show(activity) },
        { progressDialogFragment.dismiss() }
    )
}