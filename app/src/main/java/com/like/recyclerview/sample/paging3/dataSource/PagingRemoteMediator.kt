package com.like.recyclerview.sample.paging3.dataSource

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PagingRemoteMediator(
    private val db: Db,
    private val bannerDbDataSource: BannerDbDataSource,
    private val topArticleDbDataSource: TopArticleDbDataSource
) : RemoteMediator<Int, Any>() {
    private val bannerEntityDao = db.bannerEntityDao()
    private val topArticleEntityDao = db.topArticleEntityDao()
    private val articleEntityDao = db.articleEntityDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Any>): MediatorResult {
        return try {
            // The network load method takes an optional after=<user.id>
            // parameter. For every page after the first, pass the last user
            // ID to let it continue from where it left off. For REFRESH,
            // pass null to load the first page.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> 0
                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                LoadType.PREPEND ->
                    return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()

                    // You must explicitly check if the last item is null when
                    // appending, since passing null to networkService is only
                    // valid for initial load. If lastItem is null it means no
                    // items were loaded after the initial REFRESH and there are
                    // no more items to load.
                    if (lastItem == null) {
                        return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    }

                    state.pages.size
                }
            }

            // Suspending network load via Retrofit. This doesn't need to be
            // wrapped in a withContext(Dispatcher.IO) { ... } block since
            // Retrofit's Coroutine CallAdapter dispatches on a worker
            // thread.
            val bannerInfoList = bannerDbDataSource.load(loadType == LoadType.REFRESH) ?: emptyList()
            val topArticleEntityList = topArticleDbDataSource.load(loadType == LoadType.REFRESH) ?: emptyList()
            val articleEntityList = RetrofitUtils.retrofitApi.getArticle(loadKey).getDataIfSuccess()?.datas ?: emptyList()

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    articleEntityDao.clear()
                }

                // Insert new users into database, which invalidates the
                // current PagingData, allowing Paging to present the updates
                // in the DB.
                articleEntityDao.insert(*articleEntityList.toTypedArray())
            }

            MediatorResult.Success(
                endOfPaginationReached = articleEntityList.size < state.config.pageSize
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}