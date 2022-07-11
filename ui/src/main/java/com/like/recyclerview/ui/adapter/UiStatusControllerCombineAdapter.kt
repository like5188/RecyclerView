package com.like.recyclerview.ui.adapter

import com.like.paging.RequestType
import com.like.recyclerview.adapter.CombineAdapter

/**
 * 封装了3种状态视图：加载中、加载失败、空
 * 如果不需要状态视图，可以直接使用[CombineAdapter]，或者设置[uiStatusController] 参数为 null
 */
open class UiStatusControllerCombineAdapter<ValueInList>(private val uiStatusController: BaseUiStatusController?) :
    CombineAdapter<ValueInList>() {

    override var show: (() -> Unit)? = null
        get() = if (uiStatusController == null) {
            field
        } else {
            {
                with(uiStatusController) {
                    show = field
                    showUiStatus(getLoadingStatusTag())
                    onLoadingStatusShown()
                }
            }
        }

    override var onError: (suspend (RequestType, Throwable) -> Unit)? = null
        get() = if (uiStatusController == null) {
            field
        } else {
            { requestType, throwable ->
                with(uiStatusController) {
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
        }

    override var onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null
        get() = if (uiStatusController == null) {
            field
        } else {
            { requestType, items ->
                with(uiStatusController) {
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
        }

    open fun onEmptyStatusShown() {

    }

    open fun onLoadingStatusShown() {

    }

    open fun onErrorStatusShown(throwable: Throwable) {

    }
}