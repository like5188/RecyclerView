package com.like.recyclerview.sample.paging3.util

import androidx.paging.PagingConfig
import com.like.recyclerview.sample.paging3.api.Api
import com.like.recyclerview.sample.paging3.db.Db
import com.like.recyclerview.sample.paging3.repository.inDb.ArticleRemoteMediator
import com.like.recyclerview.sample.paging3.repository.inDb.DbBannerInfoDataSource
import com.like.recyclerview.sample.paging3.repository.inDb.DbPagingRepository
import com.like.recyclerview.sample.paging3.repository.inDb.DbTopArticleDataSource
import com.like.recyclerview.sample.paging3.repository.inMemory.MemoryArticlePagingSource
import com.like.recyclerview.sample.paging3.repository.inMemory.MemoryBannerInfoDataSource
import com.like.recyclerview.sample.paging3.repository.inMemory.MemoryPagingRepository
import com.like.recyclerview.sample.paging3.repository.inMemory.MemoryTopArticleDataSource
import com.like.recyclerview.sample.paging3.viewModel.PagingViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val myModule = module {
    //Api
    single {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))// 添加日志打印
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    //Db
    single {
        Db.getInstance(get())
    }

    //DataSource
    factory {
        DbBannerInfoDataSource(get(), get(), get())
    }
    factory {
        DbTopArticleDataSource(get(), get(), get())
    }
    factory {
        ArticleRemoteMediator(get(), get())
    }
    factory {
        MemoryBannerInfoDataSource(get())
    }
    factory {
        MemoryTopArticleDataSource(get())
    }
    factory {
        MemoryArticlePagingSource(get())
    }

    //Repository
    factory {
        DbPagingRepository(get(), get(), get(), get(), get())
    }
    factory {
        MemoryPagingRepository(get(), get(), get())
    }

    //viewModel
    viewModel {
        PagingViewModel(get(), get())
    }

    single {
        // initialLoadSize 默认为 PAGE_SIZE*3，所以这里需要设置一下。
        PagingConfig(30, prefetchDistance = 1, initialLoadSize = 30, enablePlaceholders = false)
    }
}