package com.like.recyclerview.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.ToastUtils
import com.like.recyclerview.sample.addimage.PictureSelectorActivity
import com.like.recyclerview.sample.concat.ConcatActivity
import com.like.recyclerview.sample.tree.TreeActivity

class MainActivity : AppCompatActivity() {

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

    fun startConcatActivity(view: View) {
        startActivity(Intent(this, ConcatActivity::class.java))
    }

}
