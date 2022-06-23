package com.like.recyclerview.sample

import android.app.Application
import com.like.recyclerview.sample.paging3.util.myModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

class MyApplication : Application() {
    companion object {
        lateinit var sInstance: Application
    }

    override fun onCreate() {
        super.onCreate()
        sInstance = this
        startKoin {
            androidContext(this@MyApplication)
        }
        loadKoinModules(myModule)
    }
}