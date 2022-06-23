package com.like.recyclerview.sample.paging3.data.netWork

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitUtils {
    val retrofitApi: Api by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.wanandroid.com/")
            .client(
                OkHttpClient.Builder()
                    .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))// 添加日志打印
                    .build()
            )
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }
}