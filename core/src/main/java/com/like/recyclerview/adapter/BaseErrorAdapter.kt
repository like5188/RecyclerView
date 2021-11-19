package com.like.recyclerview.adapter

import androidx.databinding.ViewDataBinding

/**
 * 封装了（适用于：显示错误 item 的 adapter、加载更多 item 的 adapter）
 * 1：错误回调；
 */
open class BaseErrorAdapter<VB : ViewDataBinding, ValueInList> : BaseAdapter<VB, ValueInList>() {

    /**
     * 出错时调用此方法。子类可以重写此方法进行界面更新。
     */
    open fun error(throwable: Throwable) {}

}
