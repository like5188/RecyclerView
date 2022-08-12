package com.like.recyclerview.ui.adapter

import android.content.Context
import android.view.View
import com.like.common.util.UiStatusController

/**
 * 对加载中视图进行特殊处理
 */
abstract class BaseUiStatusController(contentView: View) : UiStatusController(contentView) {
    // 是否点击刷新按钮
    private var clickRefreshBtn = false
    var show: (() -> Unit)? = null
    var refresh: (suspend () -> Unit)? = null

    final override fun showUiStatus(tag: String) {// 加载中需要特殊处理
        if (tag == getLoadingStatusTag() && show != null && !clickRefreshBtn) {
            show?.invoke()// 如果不是点击的刷新按钮，并且 show 不为 null，那么就显示 show
        } else {
            super.showUiStatus(tag)// 如果点击的刷新按钮，或者 show 为 null，那么就显示 loadingStatusUi
        }
    }

    init {
        this.addUiStatus(contentView.context) {
            clickRefreshBtn = true
            refresh?.invoke()
            clickRefreshBtn = false
        }
    }

    abstract fun addUiStatus(context: Context, refresh: suspend () -> Unit)
    abstract fun getEmptyStatusTag(): String
    abstract fun getLoadingStatusTag(): String
    abstract fun getErrorStatusTag(throwable: Throwable): String

}