package com.like.recyclerview.sample.paging3.util

import androidx.paging.PagingConfig
import com.like.recyclerview.sample.paging3.db.Db
import com.like.recyclerview.sample.paging3.repository.inDb.ArticleRemoteMediator
import com.like.recyclerview.sample.paging3.repository.inDb.BannerInfoDbDataSource
import com.like.recyclerview.sample.paging3.repository.inDb.DbPagingRepository
import com.like.recyclerview.sample.paging3.repository.inDb.TopArticleDbDataSource
import com.like.recyclerview.sample.paging3.repository.inMemory.ArticlePagingSource
import com.like.recyclerview.sample.paging3.repository.inMemory.BannerInfoDataSource
import com.like.recyclerview.sample.paging3.repository.inMemory.MemoryPagingRepository
import com.like.recyclerview.sample.paging3.repository.inMemory.TopArticleDataSource
import com.like.recyclerview.sample.paging3.viewModel.PagingViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val myModule = module {
    //Db
    single {
        Db.getInstance(get())
    }

    //Dao
    single {
        get<Db>().bannerDao()
    }
    single {
        get<Db>().topArticleDao()
    }
    single {
        get<Db>().articleDao()
    }

    //DataSource
    factory {
        BannerInfoDbDataSource(get(), get())
    }
    factory {
        TopArticleDbDataSource(get(), get())
    }
    factory {
        ArticleRemoteMediator(get())
    }
    factory {
        BannerInfoDataSource()
    }
    factory {
        TopArticleDataSource()
    }
    factory {
        ArticlePagingSource()
    }

    //Repository
    factory {
        DbPagingRepository(get(), get(), get())
    }
    factory {
        MemoryPagingRepository(get(), get())
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