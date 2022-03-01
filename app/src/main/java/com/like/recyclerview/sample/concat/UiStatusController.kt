package com.like.recyclerview.sample.concat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

class UiStatusController(private val content: View) {
    private val context = content.context
    private val root: FrameLayout by lazy {
        FrameLayout(content.context).apply {
            layoutParams = content.layoutParams
        }
    }
    private var empty: ViewDataBinding? = null
    private var error: ViewDataBinding? = null
    private var networkError: ViewDataBinding? = null
    private var loading: ViewDataBinding? = null

    fun showContent() {
        setVisibility(content = View.VISIBLE)
    }

    fun <T : ViewDataBinding> showEmpty(@LayoutRes layoutResource: Int): T {
        empty = empty ?: getViewDataBinding(layoutResource)
        setVisibility(empty = View.VISIBLE)
        return empty as T
    }

    fun <T : ViewDataBinding> showError(@LayoutRes layoutResource: Int): T {
        error = error ?: getViewDataBinding(layoutResource)
        setVisibility(error = View.VISIBLE)
        return error as T
    }

    fun <T : ViewDataBinding> showNetworkError(@LayoutRes layoutResource: Int): T {
        networkError = networkError ?: getViewDataBinding(layoutResource)
        setVisibility(netWorkError = View.VISIBLE)
        return networkError as T
    }

    fun <T : ViewDataBinding> showLoading(@LayoutRes layoutResource: Int): T {
        loading = loading ?: getViewDataBinding(layoutResource)
        setVisibility(loading = View.VISIBLE)
        return loading as T
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
        if (this.empty?.root?.visibility != empty)
            this.empty?.root?.visibility = empty
        if (this.error?.root?.visibility != error)
            this.error?.root?.visibility = error
        if (this.networkError?.root?.visibility != netWorkError)
            this.networkError?.root?.visibility = netWorkError
        if (this.loading?.root?.visibility != loading)
            this.loading?.root?.visibility = loading
        if (this.content.visibility != content)
            this.content.visibility = content
    }

    private fun <T : ViewDataBinding> getViewDataBinding(@LayoutRes layoutResource: Int): T {
        addRootToParent()
        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutResource, root, true)
    }

    private fun addRootToParent() {
        if (root.parent != null) return
        (content.parent as? ViewGroup)?.let {
            it.removeView(content)
            it.addView(root)
            root.addView(content)
            content.visibility = View.GONE
        }
    }
}