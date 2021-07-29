package com.like.recyclerview.sample

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hjq.toast.ToastUtils
import com.like.recyclerview.sample.concat.ConcatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ToastUtils.init(this.application)
        setContentView(R.layout.activity_main)
    }

    fun startPagingActivity(view: View) {
    }

    fun startTreeActivity(view: View) {
    }

    fun startPictureSelectorActivity(view: View) {
    }

    fun startConcatActivity(view: View) {
        startActivity(Intent(this, ConcatActivity::class.java))
    }
}
