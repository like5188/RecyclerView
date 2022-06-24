package com.like.recyclerview.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.hjq.toast.ToastUtils
import com.like.common.util.Logger
import com.like.recyclerview.sample.addimage.PictureSelectorActivity
import com.like.recyclerview.sample.paging3.data.db.Db
import com.like.recyclerview.sample.paging3.view.PagingActivity
import com.like.recyclerview.sample.tree.TreeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {
    private val db: Db by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ToastUtils.init(this.application)
        setContentView(R.layout.activity_main)
    }

    fun startTreeActivity(view: View) {
        startActivity(Intent(this, TreeActivity::class.java))
    }

    fun startPictureSelectorActivity(view: View) {
        startActivity(Intent(this, PictureSelectorActivity::class.java))
    }

    fun startPagingActivity(view: View) {
        startActivity(Intent(this, PagingActivity::class.java))
    }

    fun queryDb(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.bannerDao().getAll().apply {
                Logger.i("banner size=${this.size} $this")
            }
            db.topArticleDao().getAll().apply {
                Logger.i("topArticle size=${this.size} $this")
            }
            db.articleDao().getAll().apply {
                Logger.i("article size=${this.size} $this")
            }
        }
    }

    fun clearDb(view: View) {
        lifecycleScope.launch(Dispatchers.IO) {
            db.bannerDao().clear()
            db.topArticleDao().clear()
            db.articleDao().clear()
        }
    }

}
