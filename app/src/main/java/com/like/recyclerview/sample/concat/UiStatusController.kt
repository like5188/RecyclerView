package com.like.recyclerview.sample.concat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

class UiStatusController(
    private val contentView: View,
    @LayoutRes private val emptyLayoutRes: Int = 0,
    @LayoutRes private val errorLayoutRes: Int = 0,
    @LayoutRes private val networkErrorLayoutRes: Int = 0,
    @LayoutRes private val loadingLayoutRes: Int = 0,
) {
    private val context = contentView.context
    private val root: FrameLayout by lazy {
        FrameLayout(contentView.context).apply {
            layoutParams = contentView.layoutParams
        }
    }
    var emptyBinding: ViewDataBinding? = null
        private set
    var errorBinding: ViewDataBinding? = null
        private set
    var networkErrorBinding: ViewDataBinding? = null
        private set
    var loadingBinding: ViewDataBinding? = null
        private set

    fun showContent() {
        setVisibility(content = View.VISIBLE)
    }

    fun showEmpty() {
        emptyBinding = (emptyBinding ?: getViewDataBinding(emptyLayoutRes)) ?: return
        setVisibility(empty = View.VISIBLE)
    }

    fun showError() {
        errorBinding = (errorBinding ?: getViewDataBinding(errorLayoutRes)) ?: return
        setVisibility(error = View.VISIBLE)
    }

    fun showNetworkError() {
        networkErrorBinding = (networkErrorBinding ?: getViewDataBinding(networkErrorLayoutRes)) ?: return
        setVisibility(netWorkError = View.VISIBLE)
    }

    fun showLoading() {
        loadingBinding = (loadingBinding ?: getViewDataBinding(loadingLayoutRes)) ?: return
        setVisibility(loading = View.VISIBLE)
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

    private fun <T : ViewDataBinding> getViewDataBinding(@LayoutRes layoutResource: Int): T? {
        if (layoutResource == 0) return null
        val binding = DataBindingUtil.inflate<T>(LayoutInflater.from(context), layoutResource, root, true)
        binding ?: return null
        initRoot()
        return binding
    }

    /**
     * 把[root]添加到[contentView]和其 parent 的中间
     */
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