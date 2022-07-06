package com.like.recyclerview.sample.paging3.api

import com.like.recyclerview.sample.paging3.vo.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("/banner/json")
    suspend fun getBanner(): ResultModel<List<BannerInfo.Banner>?>

    @GET("/article/top/json")
    suspend fun getTopArticle(): ResultModel<List<TopArticle>?>

    @GET("/article/list/{page}/json")
    suspend fun getArticle(@Path("page") page: Int, @Query("page_size") pageSize: Int): ResultModel<PagingModel<Article>?>
}