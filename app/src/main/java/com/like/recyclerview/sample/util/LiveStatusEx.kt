package com.like.recyclerview.sample.util

import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.like.common.base.BaseDialogFragment
import com.like.common.util.shortToastCenter
import com.like.repository.Result
import com.like.repository.requestHelper.*

fun <ResultType> Result<ResultType>.onFailedToast(fragment: Fragment) {
    val context = fragment.context ?: return
    this.onFailedToast(context, fragment)
}

fun <ResultType> Result<ResultType>.onFailedToast(activity: FragmentActivity) {
    this.onFailedToast(activity, activity)
}

fun <ResultType> Result<ResultType>.onFailedToast(context: Context, lifecycleOwner: LifecycleOwner) {
    this.liveStatus.observe(lifecycleOwner, Observer {
        if (it.type is Initial || it.type is Refresh) {
            val status = it.status
            if (status is Failed) {
                val errorMsg = status.throwable?.message
                context.shortToastCenter(
                    if (errorMsg.isNullOrEmpty()) {
                        "unknown error"
                    } else {
                        errorMsg
                    }
                )
            }
        }
    })
}

/**
 * 初始化 SwipeRefreshLayout。
 * 根据 liveStatus 在初始化或者刷新时控制 SwipeRefreshLayout 的显示隐藏
 */
fun <ResultType> Result<ResultType>.bindProgress(
    lifecycleOwner: LifecycleOwner,
    swipeRefreshLayout: SwipeRefreshLayout,
    @ColorInt vararg colors: Int = intArrayOf(Color.BLUE, Color.GREEN, Color.RED, Color.YELLOW)
) {
    swipeRefreshLayout.setColorSchemeColors(*colors)

    swipeRefreshLayout.setOnRefreshListener {
        this.refresh()
    }

    this.bindProgress(
        lifecycleOwner,
        { swipeRefreshLayout.isRefreshing = true },
        {
            swipeRefreshLayout.isRefreshing = false
            if (!it.isNullOrEmpty()) {
                swipeRefreshLayout.context.shortToastCenter(it)
            }
        }
    )
}

fun <ResultType> Result<ResultType>.bindProgress(
    fragment: Fragment,
    progressDialogFragment: BaseDialogFragment
) {
    val context = fragment.context ?: return
    this.bindProgress(
        fragment,
        { progressDialogFragment.show(fragment) },
        {
            progressDialogFragment.dismiss()
            if (!it.isNullOrEmpty()) {
                context.shortToastCenter(it)
            }
        }
    )
}

fun <ResultType> Result<ResultType>.bindProgress(
    activity: FragmentActivity,
    progressDialogFragment: BaseDialogFragment
) {
    this.bindProgress(
        activity,
        { progressDialogFragment.show(activity) },
        {
            progressDialogFragment.dismiss()
            if (!it.isNullOrEmpty()) {
                activity.shortToastCenter(it)
            }
        }
    )
}

private fun <ResultType> Result<ResultType>.bindProgress(
    lifecycleOwner: LifecycleOwner,
    show: () -> Unit,
    hide: (String?) -> Unit
) {
    this.liveStatus.observe(lifecycleOwner, Observer {
        if (it.type is Initial || it.type is Refresh) {
            when (val status = it.status) {
                is Running -> {
                    show()
                }
                is Success -> {
                    hide(null)
                }
                is Failed -> {
                    val errorMsg = status.throwable?.message
                    hide(
                        if (errorMsg.isNullOrEmpty()) {
                            "unknown error"
                        } else {
                            errorMsg
                        }
                    )
                }
            }
        }
    })
}