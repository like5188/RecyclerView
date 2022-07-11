package com.like.recyclerview.ui.util

import com.like.paging.RequestType
import com.like.recyclerview.adapter.CombineAdapter

/**
 * 封装了3种状态视图：加载中、加载失败、空
 */
open class UiStatusControllerCombineAdapter<ValueInList>(private val uiStatusController: BaseUiStatusController) :
    CombineAdapter<ValueInList>() {

    final override var show: (() -> Unit)? = null
        get() = {
            uiStatusController.apply {
                show = field
                showUiStatus(getLoadingStatusTag())
                onLoadingStatusShown()
            }
        }

    final override var hide: (() -> Unit)? = null

    final override var onError: (suspend (RequestType, Throwable) -> Unit)? = null
        get() = { requestType, throwable ->
            uiStatusController.apply {
                refresh = {
                    refresh()
                }
                if ((requestType is RequestType.Initial || requestType is RequestType.Refresh) && itemCount() <= 0) {
                    // 初始化或者刷新失败时，如果当前显示的是列表，则不处理，否则显示[errorAdapter]
                    showUiStatus(getErrorStatusTag(throwable))
                    onErrorStatusShown(throwable)
                } else {
                    showContent()
                }
            }
            field?.invoke(requestType, throwable)
        }

    final override var onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null
        get() = { requestType, items ->
            uiStatusController.apply {
                refresh = {
                    refresh()
                }
                if ((requestType is RequestType.Initial || requestType is RequestType.Refresh) && items.isNullOrEmpty()) {
                    // 显示空视图
                    showUiStatus(getEmptyStatusTag())
                    onEmptyStatusShown()
                } else {
                    showContent()
                }
            }
            field?.invoke(requestType, items)
        }

    open fun onEmptyStatusShown() {

    }

    open fun onLoadingStatusShown() {

    }

    open fun onErrorStatusShown(throwable: Throwable) {

    }
}