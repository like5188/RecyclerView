package com.like.recyclerview.utils

import com.like.paging.PagingResult
import com.like.paging.RequestType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/**
 * [PagingResult]类型的数据搜集
 * 功能：
 * 1、支持初始化、刷新时进度条的显示隐藏。
 * 2、支持成功失败回调。
 * 3、封装了初始化、刷新、往后加载更多、往前加载更多操作。并对这些操作做了并发处理，并发处理规则如下：
 * ①、初始化、刷新：如果有操作正在执行，则取消正在执行的操作，执行新操作。
 * ②、往后加载更多、往前加载更多：如果有操作正在执行，则放弃新操作，否则执行新操作。
 */
internal class PagingResultCollector<ValueInList> {
    private val concurrencyHelper = ConcurrencyHelper()
    private lateinit var pagingResult: PagingResult<List<ValueInList>?>
    private var callback: Callback<ValueInList>? = null

    /**
     * 从[pagingResult]搜集数据，触发初始化操作。
     * @param pagingResult  列表需要的数据。使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
     */
    suspend fun collectFrom(pagingResult: PagingResult<List<ValueInList>?>, callback: Callback<ValueInList>) {
        this.pagingResult = pagingResult
        this.callback = callback
        initial()
    }

    /**
     * 初始化操作（线程安全）
     */
    private suspend fun initial() {
        val requestType = RequestType.Initial
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult.setRequestType.invoke(requestType)
            collect(requestType)
        }
    }

    /**
     * 刷新操作（线程安全）
     */
    suspend fun refresh() {
        val requestType = RequestType.Refresh
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult.setRequestType.invoke(requestType)
            collect(requestType)
        }
    }

    /**
     * 往后加载更多操作（线程安全）
     */
    suspend fun after() {
        val requestType = RequestType.After
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult.setRequestType.invoke(requestType)
            collect(requestType)
        }
    }

    /**
     * 往前加载更多操作（线程安全）
     */
    suspend fun before() {
        val requestType = RequestType.Before
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult.setRequestType.invoke(requestType)
            collect(requestType)
        }
    }

    private suspend fun collect(requestType: RequestType) {
        pagingResult.flow.flowOn(Dispatchers.IO)
            .onStart {
                if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
                    callback?.onShow()
                }
            }.onCompletion {
                if (requestType is RequestType.Initial || requestType is RequestType.Refresh) {
                    callback?.onHide()
                }
            }.catch {
                callback?.onError(requestType, it)
            }.flowOn(Dispatchers.Main)
            .collect {
                callback?.onSuccess(requestType, it)
            }
    }

    interface Callback<ValueInList> {
        /**
         * 初始化或者刷新开始时显示进度条
         */
        fun onShow()

        /**
         * 初始化或者刷新完成时隐藏进度条
         */
        fun onHide()

        /**
         * 请求失败时回调
         */
        suspend fun onError(requestType: RequestType, throwable: Throwable)

        /**
         * 请求成功时回调
         */
        suspend fun onSuccess(requestType: RequestType, list: List<ValueInList>?)
    }

}
