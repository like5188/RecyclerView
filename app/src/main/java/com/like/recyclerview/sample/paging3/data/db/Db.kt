package com.like.recyclerview.sample.paging3.data.db

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.like.recyclerview.sample.paging3.data.model.Article
import com.like.recyclerview.sample.paging3.data.model.BannerInfo
import com.like.recyclerview.sample.paging3.data.model.RemoteKeysEntity
import com.like.recyclerview.sample.paging3.data.model.TopArticle

@Database(
    entities = [BannerInfo.Banner::class, TopArticle::class, Article::class, RemoteKeysEntity::class],
    version = 1,
    exportSchema = false
)
abstract class Db : RoomDatabase() {
    private val mIsDatabaseCreated = MutableLiveData<Boolean>()

    companion object {
        private const val DATABASE_NAME = "paging3.db"

        @Volatile
        private var INSTANCE: Db? = null
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 版本从1到2的改变。例如增加表、字段等等
                // database.execSQL()
            }
        }

        /**
         * @param useInMemory 是否创建内存数据库。数据只是保存在内存中，没有保存在disk中。
         */
        fun getInstance(context: Context, useInMemory: Boolean = false): Db =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, useInMemory).also {
                    INSTANCE = it
                    it.updateDatabaseCreated(context)
                }
            }

        private fun buildDatabase(context: Context, useInMemory: Boolean): Db {
            val databaseBuilder = if (useInMemory) {
                Room.inMemoryDatabaseBuilder(context, Db::class.java)
            } else {
                Room.databaseBuilder(context, Db::class.java, DATABASE_NAME)
            }
            return databaseBuilder.addCallback(object : RoomDatabase.Callback() {
                // 在第一次创建数据库时将调用
                // 如果应用程序在首次启动时崩溃，并且数据库已经创建完成但是数据还没插入表中的话，
                // 这些预加载数据将再也不会插入表中了，因为onCreate方法不会再被调用了。
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    initData(context, useInMemory)
                }
            })
//                    .addMigrations(MIGRATION_1_2)// 更新版本之后升级数据库
                .fallbackToDestructiveMigration()// 更新版本之后清空数据库重新创建
                .build()
        }

        private fun initData(context: Context, useInMemory: Boolean) {
//            ioDiskThread {
//                // Add a delay to simulate a long-running operation
//                SystemClock.sleep(4000)
//                // Generate the data for pre-population
//                val database = Db.getInstance(context, useInMemory)
//                // 初始化数据库
//                val circles = DataGenerator.generateCircles()
//                database.runInTransaction {
//                    database.circleDao().insert(circles)
//                }
//                // notify that the database was created and it's ready to be used
//                database.setDatabaseCreated()
//            }
        }
    }

    abstract fun bannerDao(): BannerDao
    abstract fun topArticleDao(): TopArticleDao
    abstract fun articleDao(): ArticleDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    /**
     * Check whether the database already exists and expose it via [.getDatabaseCreated]
     */
    private fun updateDatabaseCreated(context: Context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated()
        }
    }

    private fun setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true)
    }
}