package com.like.recyclerview.sample.paging3.util

import androidx.paging.PagingConfig
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.dataSource.db.ArticleRemoteMediator
import com.like.recyclerview.sample.paging3.dataSource.db.BannerDbDataSource
import com.like.recyclerview.sample.paging3.dataSource.db.TopArticleDbDataSource
import com.like.recyclerview.sample.paging3.dataSource.memory.ArticlePagingSource
import com.like.recyclerview.sample.paging3.dataSource.memory.BannerDataSource
import com.like.recyclerview.sample.paging3.dataSource.memory.TopArticleDataSource
import com.like.recyclerview.sample.paging3.repository.PagingRepository
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
        BannerDbDataSource(get(), get())
    }
    factory {
        TopArticleDbDataSource(get(), get())
    }
    factory {
        ArticleRemoteMediator(get())
    }
    factory {
        BannerDataSource()
    }
    factory {
        TopArticleDataSource()
    }
    factory {
        ArticlePagingSource()
    }

    //Repository
    factory {
        PagingRepository(get(), get(), get(), get(), get())
    }

    //viewModel
    viewModel {
        PagingViewModel(get())
    }

    single {
        PagingConfig(10, prefetchDistance = 1, initialLoadSize = 10)
    }
}