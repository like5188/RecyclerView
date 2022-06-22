package com.like.recyclerview.sample.paging3.util

/**
 * 不分页数据需要存储数据库时，可以使用此接口，此接口的[load]方法实现了一种存储策略。
 */
interface IDbHelper<ResultType> {

    suspend fun load(isRefresh: Boolean = false): ResultType {
        var data = loadFromDb(isRefresh)
        if (shouldFetch(isRefresh, data)) {
            //即将从网络获取数据并存入数据库中
            fetchFromNetworkAndSaveToDb(isRefresh)
            //即将重新从数据库获取数据
            data = loadFromDb(isRefresh)
        }
        //从数据库获取到了数据
        return data
    }

    /**
     * 从数据库中获取数据
     *
     * @param isRefresh     是否刷新操作。true：刷新操作；false：初始化操作
     */
    suspend fun loadFromDb(isRefresh: Boolean): ResultType

    /**
     * 是否应该从网络获取数据。
     */
    fun shouldFetch(isRefresh: Boolean, result: ResultType): Boolean

    /**
     * 从网络获取数据并存储数据库中。
     */
    suspend fun fetchFromNetworkAndSaveToDb(isRefresh: Boolean)

}