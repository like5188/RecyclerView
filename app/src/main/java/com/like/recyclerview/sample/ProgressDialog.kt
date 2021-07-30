package com.like.recyclerview.sample

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager

/**
 * 进度条对话框
 */
class ProgressDialog(context: Context) : Dialog(context) {

    override fun onStart() {
        super.onStart()
        //如果需要dialog显示在软键盘之上，就需要为window添加FLAG_ALT_FOCUSABLE_IM这个属性
        window?.setFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM, WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM)
        // 设置背景透明，并去掉 dialog 默认的 padding
        window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_progress)
    }

}