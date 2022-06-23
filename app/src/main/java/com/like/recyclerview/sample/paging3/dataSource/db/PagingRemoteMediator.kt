package com.like.recyclerview.sample.paging3.dataSource.db

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.data.model.Article
import com.like.recyclerview.sample.paging3.data.netWork.RetrofitUtils
import com.like.recyclerview.sample.paging3.dataSource.memory.BannerDataSource
import com.like.recyclerview.sample.paging3.dataSource.memory.TopArticleDataSource
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PagingRemoteMediator(
    private val db: Db,
    private val bannerDataSource: BannerDataSource,
    private val topArticleDataSource: TopArticleDataSource
) : RemoteMediator<Int, Article>() {
    private val bannerDao = db.bannerDao()
    private val topArticleDao = db.topArticleDao()
    private val articleDao = db.articleDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Article>): MediatorResult {
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
            val articleList = RetrofitUtils.retrofitApi.getArticle(loadKey).getDataIfSuccess()?.datas

            if (loadType == LoadType.REFRESH) {
                val bannerInfo = bannerDataSource.load()
                val topArticleList = topArticleDataSource.load()
                db.withTransaction {
                    bannerDao.clear()
                    topArticleDao.clear()
                    articleDao.clear()
                    // Insert new users into database, which invalidates the
                    // current PagingData, allowing Paging to present the updates
                    // in the DB.
                    val banners = bannerInfo?.banners
                    if (!banners.isNullOrEmpty()) {
                        bannerDao.insert(*banners.toTypedArray())
                    }
                    if (!topArticleList.isNullOrEmpty()) {
                        topArticleDao.insert(*topArticleList.toTypedArray())
                    }
                    if (!articleList.isNullOrEmpty()) {
                        articleDao.insert(*articleList.toTypedArray())
                    }
                }
            } else {
                db.withTransaction {
                    if (!articleList.isNullOrEmpty()) {
                        articleDao.insert(*articleList.toTypedArray())
                    }
                }
            }
            MediatorResult.Success(
                endOfPaginationReached = (articleList?.size ?: 0) < state.config.pageSize
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}