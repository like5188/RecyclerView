package com.like.recyclerview.adapter

import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.like.paging.PagingResult
import com.like.paging.RequestType
import com.like.recyclerview.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

/*
使用方法：
val adapter = CombineAdapter<IRecyclerViewItem>(mBinding.rv)
adapter.apply {
    withItemAdapter(ItemAdapter())
    bindData(mViewModel::getItems.asFlow())
}
lifecycleScope.launch {
    adapter.initial()
}
 */
/**
 * 对 Header、Footer、Item 三种 Adapter 进行组合。并绑定[PagingResult]或者[Flow]类型的数据。
 * 功能：
 * 1、支持添加 Header、Footer。
 * 2、支持初始化、刷新时进度条的显示隐藏。
 * 3、支持成功失败回调。
 * 4、封装了初始化、刷新、往后加载更多、往前加载更多操作。并对这些操作做了并发处理，并发处理规则如下：
 * ①、初始化、刷新：如果有操作正在执行，则取消正在执行的操作，执行新操作。
 * ②、往后加载更多、往前加载更多：如果有操作正在执行，则放弃新操作，否则执行新操作。
 */
open class CombineAdapter<ValueInList>(private val recyclerView: RecyclerView) {
    private val adapter = ConcatAdapter(ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build())
    private var headerAdapter: BaseAdapter<*, ValueInList>? = null
    private var itemAdapter: BaseAdapter<*, ValueInList>? = null
    private var loadMoreAdapter: BaseLoadMoreAdapter<*, *>? = null
    private val concurrencyHelper = ConcurrencyHelper()
    private var pagingResult: PagingResult<List<ValueInList>?>? = null
    private var flow: Flow<List<ValueInList>?>? = null

    // 是否是往后加载更多。true：往后加载更多；false：往前加载更多；默认为 null，表示不分页。
    private var isAfter: Boolean? = null

    /**
     * 把 Header 的数据[flow]及列表的数据[pagingResult]组合为 List<List<ValueInList>?>?
     * 通过以下方式获取数据：
     * val headers = resultType.getOrNull(0)
     * val items = resultType.getOrNull(1)
     */
    private val initialOrRefreshFlow: Flow<List<List<ValueInList>?>?>?
        get() {
            val listFlow = pagingResult?.flow
            val headerFlow = flow
            return when {
                headerFlow != null && listFlow != null -> {
                    headerFlow.zip(listFlow) { h, l -> listOf(h, l) }
                }
                headerFlow != null -> {
                    headerFlow.map { listOf(it, null) }
                }
                listFlow != null -> {
                    listFlow.map { listOf(null, it) }
                }
                else -> {
                    null
                }
            }
        }

    init {
        recyclerView.adapter = adapter
    }

    /**
     * 初始化或者刷新开始时显示进度条
     */
    var show: (() -> Unit)? = null

    /**
     * 初始化或者刷新完成时隐藏进度条
     */
    var hide: (() -> Unit)? = null

    /**
     * 请求失败时回调
     */
    var onError: (suspend (RequestType, Throwable) -> Unit)? = null

    /**
     * 初始化或者刷新成功时回调
     * 返回值为一个集合 List<List<ValueInList>?>?，其中包括两个数据，按照顺序分别表示 [headerAdapter]数据、[itemAdapter]数据。
     * 通过以下方式获取数据：
     * val headers = resultType.getOrNull(0)
     * val items = resultType.getOrNull(1)
     */
    var onInitialOrRefreshSuccess: (suspend (RequestType, List<List<ValueInList>?>?) -> Unit)? = null

    /**
     * 加载更多成功时回调
     */
    var onLoadMoreSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null

    /**
     * 设置 Footer 往后加载更多，固定于 [RecyclerView] 底部
     * @param adapter    往后加载更多视图 的 adapter
     */
    fun withFooterAdapter(adapter: BaseLoadMoreAdapter<*, *>) {
        adapter.onLoadMore = ::after
        this.isAfter = true
        this.loadMoreAdapter = adapter
    }

    /**
     * 设置 Header 往前加载更多，固定于 [RecyclerView] 顶部
     * @param adapter    往前加载更多视图 的 adapter
     */
    fun withHeaderAdapter(adapter: BaseLoadMoreAdapter<*, *>) {
        adapter.onLoadMore = ::before
        this.isAfter = false
        this.loadMoreAdapter = adapter
    }

    /**
     * 设置 Header 数据，固定于 [RecyclerView] 顶部
     * @param flow  Header 需要的数据。
     */
    fun withHeaderAdapter(adapter: BaseAdapter<*, ValueInList>, flow: Flow<List<ValueInList>?>) {
        this.headerAdapter = adapter
        this.flow = flow
    }

    /**
     * 设置（不分页）列表数据，固定于 [RecyclerView] 中部。
     */
    fun withItemAdapter(adapter: BaseAdapter<*, ValueInList>, flow: Flow<List<ValueInList>?>) {
        this.itemAdapter = adapter
        this.pagingResult = PagingResult(flow) {}
    }

    /**
     * 设置（分页）列表数据，固定于 [RecyclerView] 中部，并且加载更多的数据是添加到其中。
     * @param pagingResult  列表需要的数据。使用了 [com.github.like5188:Paging:x.x.x] 库，得到的返回结果。
     */
    fun withItemAdapter(adapter: BaseAdapter<*, ValueInList>, pagingResult: PagingResult<List<ValueInList>?>) {
        this.itemAdapter = adapter
        this.pagingResult = pagingResult
    }

    /**
     * 初始化操作（线程安全）
     */
    suspend fun initial() {
        val f = initialOrRefreshFlow ?: return
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult?.setRequestType?.invoke(RequestType.Initial)
            collectInitialOrRefresh(RequestType.Initial, f, show, hide, onError, onInitialOrRefreshSuccess)
        }
    }

    /**
     * 刷新操作（线程安全）
     */
    suspend fun refresh() {
        val f = initialOrRefreshFlow ?: return
        concurrencyHelper.cancelPreviousThenRun {
            pagingResult?.setRequestType?.invoke(RequestType.Refresh)
            collectInitialOrRefresh(RequestType.Refresh, f, show, hide, onError, onInitialOrRefreshSuccess)
        }
    }

    /**
     * 往后加载更多操作（线程安全）
     */
    private suspend fun after() {
        val f = pagingResult?.flow ?: return
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.After)
            collectMore(RequestType.After, f, onError, onLoadMoreSuccess)
        }
    }

    /**
     * 往前加载更多操作（线程安全）
     */
    private suspend fun before() {
        val f = pagingResult?.flow ?: return
        concurrencyHelper.dropIfPreviousRunning {
            pagingResult?.setRequestType?.invoke(RequestType.Before)
            collectMore(RequestType.Before, f, onError, onLoadMoreSuccess)
        }
    }

    private suspend fun collectInitialOrRefresh(
        requestType: RequestType,
        flow: Flow<List<List<ValueInList>?>?>,
        show: (() -> Unit)? = null,
        hide: (() -> Unit)? = null,
        onError: (suspend (RequestType, Throwable) -> Unit)? = null,
        onSuccess: (suspend (RequestType, List<List<ValueInList>?>?) -> Unit)? = null,
    ) {
        flow.flowOn(Dispatchers.IO)
            .onStart {
                show?.invoke()
            }.onCompletion {
                hide?.invoke()
            }.catch {
                onError?.invoke(requestType, it)
            }.flowOn(Dispatchers.Main)
            .collect { resultType ->
                adapter.clear()
                if (!resultType.isNullOrEmpty()) {
                    val headers = resultType.getOrNull(0)
                    val items = resultType.getOrNull(1)

                    val hasItem = itemAdapter != null && !items.isNullOrEmpty()

                    if (hasItem) {
                        loadMoreAdapter?.apply {
                            if (isAfter == false) {// 往前加载更多
                                adapter.add(this)
                            }
                        }
                    }

                    headerAdapter?.apply {
                        if (!headers.isNullOrEmpty()) {
                            clear()
                            addAllToEnd(headers)
                            adapter.add(this)
                        }
                    }

                    itemAdapter?.apply {
                        if (!items.isNullOrEmpty()) {
                            clear()
                            if (isAfter == false) {// 往前加载更多
                                addAllToStart(items)
                            } else {// 不分页或者往后加载更多
                                addAllToEnd(items)
                            }
                            adapter.add(this)
                        }
                    }

                    if (hasItem) {
                        loadMoreAdapter?.apply {
                            if (isAfter != false) {// 不分页或者往后加载更多
                                adapter.add(loadMoreAdapter)
                            }
                        }
                    }

                    if (isAfter == false) {// 往前加载更多
                        recyclerView.scrollToBottom()
                    } else {
                        recyclerView.scrollToTop()
                    }

                    if (!items.isNullOrEmpty()) {
                        loadMoreAdapter?.hasMore()
                    }
                }
                onSuccess?.invoke(requestType, resultType)
            }
    }

    private suspend fun collectMore(
        requestType: RequestType,
        flow: Flow<List<ValueInList>?>,
        onError: (suspend (RequestType, Throwable) -> Unit)? = null,
        onSuccess: (suspend (RequestType, List<ValueInList>?) -> Unit)? = null,
    ) {
        flow.flowOn(Dispatchers.IO)
            .catch {
                // 加载更多失败时，直接更新[loadMoreAdapter]
                loadMoreAdapter?.error(it)
                onError?.invoke(requestType, it)
            }.flowOn(Dispatchers.Main)
            .collect { items ->
                itemAdapter?.apply {
                    if (!items.isNullOrEmpty()) {
                        if (isAfter == false) {// 往前加载更多
                            addAllToStart(items)
                            recyclerView.keepPosition(items.size, (headerAdapter?.itemCount ?: 0) + 1)
                        } else {
                            addAllToEnd(items)
                        }
                    }
                }
                if (items.isNullOrEmpty()) {
                    // 没有更多数据需要加载
                    loadMoreAdapter?.end()
                } else {
                    // 还有更多数据需要加载
                    loadMoreAdapter?.hasMore()
                }
                onSuccess?.invoke(requestType, items)
            }
    }

}
