package com.like.recyclerview.sample.util

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.common.util.shortToastCenter
import com.like.datasource.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 初始化 SwipeRefreshLayout。
 * 根据 liveStatus 在初始化或者刷新时控制 SwipeRefreshLayout 的显示隐藏，错误时 Toast 提示
 */
suspend fun <ResultType> Result<ResultType>.bindProgress(
    swipeRefreshLayout: SwipeRefreshLayout,
    @ColorInt vararg colors: Int = intArrayOf(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW),
    onSuccess: ((ResultType?) -> Unit)? = null
) = withContext(Dispatchers.Main) {
    swipeRefreshLayout.setColorSchemeColors(*colors)

    swipeRefreshLayout.setOnRefreshListener {
        this.launch {
            this@bindProgress.refresh()
        }
    }

    this@bindProgress.bind(
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

suspend fun <ResultType> Result<ResultType>.bindProgress(
    fragment: Fragment,
    progressDialogFragment: BaseDialogFragment,
    onSuccess: ((ResultType?) -> Unit)? = null
) = withContext(Dispatchers.Main) {
    this@bindProgress.bind(
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

suspend fun <ResultType> Result<ResultType>.bindProgress(
    activity: FragmentActivity,
    progressDialogFragment: BaseDialogFragment,
    onSuccess: ((ResultType?) -> Unit)? = null
) = withContext(Dispatchers.Main) {
    this@bindProgress.bind(
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