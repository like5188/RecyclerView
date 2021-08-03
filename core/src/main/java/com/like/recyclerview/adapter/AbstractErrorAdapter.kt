package com.like.recyclerview.adapter

import androidx.databinding.ViewDataBinding

/**
 * 封装了
 * 1：错误回调；
 */
abstract class AbstractErrorAdapter<VB : ViewDataBinding, ValueInList> : AbstractAdapter<VB, ValueInList>() {

    /**
     * 出错时调用此方法。子类可以重写此方法进行界面更新。
     */
    abstract fun onError(throwable: Throwable)

}
