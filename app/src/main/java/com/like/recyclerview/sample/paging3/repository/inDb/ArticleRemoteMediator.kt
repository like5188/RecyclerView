package com.like.recyclerview.sample.paging3.repository.inDb

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.like.common.util.Logger
import com.like.recyclerview.sample.paging3.db.Db
import com.like.recyclerview.sample.paging3.vo.Article
import com.like.recyclerview.sample.paging3.vo.RemoteKeysEntity
import com.like.recyclerview.sample.paging3.api.RetrofitUtils
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(private val db: Db) : RemoteMediator<Int, Article>() {
    private val articleDao = db.articleDao()
    private val remoteKeysDao = db.remoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Article>): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val remoteKey = db.withTransaction {
                        remoteKeysDao.getRemoteKeys(remoteArticle)
                    }
                    if (remoteKey?.nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextKey
                }
            } ?: 0
            val pageSize = when (loadType) {
                LoadType.REFRESH -> state.config.initialLoadSize
                else -> state.config.pageSize
            }
            val pagingModel = RetrofitUtils.retrofitApi.getArticle(page, pageSize).getDataIfSuccess()
            val endOfPaginationReached = (pagingModel?.curPage ?: 0) >= (pagingModel?.pageCount ?: 0)
            Logger.d("ArticleRemoteMediator page=$page pageSize=$pageSize endOfPaginationReached=$endOfPaginationReached")
            Logger.printCollection(pagingModel?.datas)
            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clearRemoteKeys(remoteArticle)
                    articleDao.clear()
                    Logger.e("articleDao clear")
                }
                val articleList = pagingModel?.datas
                if (!articleList.isNullOrEmpty()) {
                    articleDao.insert(*articleList.toTypedArray())
                    Logger.e("articleDao insert")
                }
                remoteKeysDao.insert(
                    RemoteKeysEntity(
                        remoteName = remoteArticle,
                        nextKey = if (endOfPaginationReached) null else page + 1
                    )
                )
            }
            MediatorResult.Success(
                endOfPaginationReached = endOfPaginationReached
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        private const val remoteArticle = "ArticleRemoteMediator"
    }
}