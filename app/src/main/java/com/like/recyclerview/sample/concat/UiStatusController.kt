package com.like.recyclerview.sample.concat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

class UiStatusController(
    private val contentView: View
) {
    private val context = contentView.context
    private val root: FrameLayout by lazy {
        FrameLayout(contentView.context).apply {
            layoutParams = contentView.layoutParams
        }
    }
    private var emptyBinding: ViewDataBinding? = null
    private var errorBinding: ViewDataBinding? = null
    private var networkErrorBinding: ViewDataBinding? = null
    private var loadingBinding: ViewDataBinding? = null

    fun showContent() {
        setVisibility(content = View.VISIBLE)
    }

    fun <T : ViewDataBinding> showEmpty(@LayoutRes layoutResource: Int): T {
        emptyBinding = emptyBinding ?: getViewDataBinding(layoutResource)
        setVisibility(empty = View.VISIBLE)
        return emptyBinding as T
    }

    fun <T : ViewDataBinding> showError(@LayoutRes layoutResource: Int): T {
        errorBinding = errorBinding ?: getViewDataBinding(layoutResource)
        setVisibility(error = View.VISIBLE)
        return errorBinding as T
    }

    fun <T : ViewDataBinding> showNetworkError(@LayoutRes layoutResource: Int): T {
        networkErrorBinding = networkErrorBinding ?: getViewDataBinding(layoutResource)
        setVisibility(netWorkError = View.VISIBLE)
        return networkErrorBinding as T
    }

    fun <T : ViewDataBinding> showLoading(@LayoutRes layoutResource: Int): T {
        loadingBinding = loadingBinding ?: getViewDataBinding(layoutResource)
        setVisibility(loading = View.VISIBLE)
        return loadingBinding as T
    }

    fun hideAll() {
        setVisibility()
    }

    private fun setVisibility(
        empty: Int = View.GONE,
        error: Int = View.GONE,
        netWorkError: Int = View.GONE,
        loading: Int = View.GONE,
        content: Int = View.GONE,
    ) {
        if (this.emptyBinding?.root?.visibility != empty)
            this.emptyBinding?.root?.visibility = empty
        if (this.errorBinding?.root?.visibility != error)
            this.errorBinding?.root?.visibility = error
        if (this.networkErrorBinding?.root?.visibility != netWorkError)
            this.networkErrorBinding?.root?.visibility = netWorkError
        if (this.loadingBinding?.root?.visibility != loading)
            this.loadingBinding?.root?.visibility = loading
        if (this.contentView.visibility != content)
            this.contentView.visibility = content
    }

    private fun <T : ViewDataBinding> getViewDataBinding(@LayoutRes layoutResource: Int): T {
        initRoot()
        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutResource, root, true)
    }

    private fun initRoot() {
        if (root.parent != null) return
        (contentView.parent as? ViewGroup)?.let {
            it.removeView(contentView)
            it.addView(root)
            root.addView(contentView)
            contentView.visibility = View.GONE
        }
    }
}