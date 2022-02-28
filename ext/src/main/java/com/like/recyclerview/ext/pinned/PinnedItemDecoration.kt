package com.like.recyclerview.ext.pinned

import android.graphics.Canvas
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.utils.findFirstVisibleItemPosition

/**
 * 配合[IPinnedItem]类型的数据来实现固定悬浮功能。对固定悬浮标签的操作和正常视图一样（包括动画、各种事件等等）
 * 注意：1、使用粘性分组标签时，RecyclerView不能设置padding、margin等，否则会影响显示效果。
 *      2、必须在RecyclerView外面添加一层FrameLayout或者RelativeLayout。例如：
<FrameLayout>
<androidx.recyclerview.widget.RecyclerView />
</FrameLayout>
 */
class PinnedItemDecoration(private val mItemAdapter: BaseAdapter<*, *>) : RecyclerView.ItemDecoration() {
    private var mCurPinnedItem: PinnedItem? = null
    private var mRecyclerViewParent: ViewGroup? = null
    private var mOnPinnedItemRenderListener: OnPinnedItemRenderListener? = null

    private fun init(recyclerView: RecyclerView) {
        if (mRecyclerViewParent != null) {
            return
        }
        if (recyclerView.parent !is FrameLayout && recyclerView.parent !is RelativeLayout) {
            throw Throwable("RecyclerView的parent只能是FrameLayout或者RelativeLayout")
        }
        Log.e("tag", "init")
        mRecyclerViewParent = recyclerView.parent as ViewGroup
    }

    fun setOnPinnedHeaderRenderListener(listener: OnPinnedItemRenderListener) {
        mOnPinnedItemRenderListener = listener
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        init(parent)

        val prePinnedItem = findPrePinnedPositionAndLayoutIdFromFirstVisibleItem(parent)
        if (prePinnedItem == null) {// 说明前面已经没有PinnedView了
            if (mCurPinnedItem != null) {
//                Log.v(TAG, "清除pinnedView")
                removeCurPinnedView()
                mCurPinnedItem = null
            }
        } else {
            if (mCurPinnedItem == null || mCurPinnedItem!!.layoutId != prePinnedItem.layoutId) {
//                Log.v(TAG, "改变pinnedView布局，并绑定数据：$prePinnedItem")
                // 移除原来的
                removeCurPinnedView()
                // 添加新的
                mCurPinnedItem = prePinnedItem.copy()
                mCurPinnedItem?.binding = addPinnedView(parent, prePinnedItem.layoutId)
                bindVariable()
            } else {
                if (mCurPinnedItem?.position != prePinnedItem.position)
                    mCurPinnedItem?.position = prePinnedItem.position
                if (mCurPinnedItem?.data != prePinnedItem.data) {
//                    Log.w(TAG, "布局未变，绑定新数据：$mCurPinnedItem")
                    mCurPinnedItem?.data = prePinnedItem.data
                    bindVariable()
                }
            }
            movePinnedView(parent)
        }
    }

    private fun bindVariable() {
        mCurPinnedItem?.apply {
            binding?.let {
                val variableId = this.data.variableId
                if (variableId >= 0) {
                    try {
                        it.setVariable(variableId, this.data)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                it.executePendingBindings()// 解决闪烁问题
                mOnPinnedItemRenderListener?.onRender(it, this.layoutId, this.data, this.position)
            }
        }
    }

    private fun movePinnedView(recyclerView: RecyclerView) {
        mCurPinnedItem?.binding ?: return
        val curPinnedView = mCurPinnedItem!!.binding!!.root
        var top = 0
        var bottom = curPinnedView.height
//        Log.w(TAG, "1 top=$top bottom=$bottom")
        val nextPinnedItem = findNextPinnedPositionAndLayoutIdFromFirstVisibleItem(recyclerView)
//        Log.d(TAG, "mCurPinnedItem=$mCurPinnedItem")
//        Log.v(TAG, "nextPinnedItem=$nextPinnedItem")
        if (nextPinnedItem != null) {
            val nextPinnedView = recyclerView.layoutManager?.findViewByPosition(nextPinnedItem.position)
//            Log.e(TAG, "nextPinnedView=$nextPinnedView")
            if (nextPinnedView != null && nextPinnedView.top in top..bottom) {
                top = nextPinnedView.top - curPinnedView.height
                bottom = nextPinnedView.top
            }
        }
//        Log.i(TAG, "2 top=$top bottom=$bottom")
        if (top != 0 || bottom != 0) {// 改变布局时，都是0，如果两个布局高度不一样，会造成闪烁。
            if (curPinnedView.visibility != View.VISIBLE)
                curPinnedView.visibility = View.VISIBLE
            curPinnedView.layout(curPinnedView.left, top, curPinnedView.right, bottom)
        }
    }

    private fun removeCurPinnedView() {
        mCurPinnedItem?.binding?.apply {
            // 移除原来的
            if (root.parent != null) {
                root.post { mRecyclerViewParent?.removeView(root) }
            }
        }
    }

    private fun addPinnedView(recyclerView: RecyclerView, layoutId: Int): ViewDataBinding? {
        if (layoutId == -1) return null
        val viewDataBinding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(recyclerView.context),
            layoutId,
            mRecyclerViewParent,
            true
        )
        viewDataBinding.root.visibility = View.INVISIBLE
        return viewDataBinding
    }

    /**
     * 从第一个可见的item开始往前递减找出PinnedView的位置。包含第一个可见的item
     */
    private fun findPrePinnedPositionAndLayoutIdFromFirstVisibleItem(recyclerView: RecyclerView): PinnedItem? {
        val firstVisiblePosition = recyclerView.findFirstVisibleItemPosition()
        if (firstVisiblePosition < mItemAdapter.itemCount && firstVisiblePosition >= 0) {
            for (position in firstVisiblePosition downTo 0) {
                val item = mItemAdapter.get(position)
                if (item is IPinnedItem) {
                    return PinnedItem(
                        item.layoutId,
                        position,
                        item
                    )
                }
            }
        }
        return null
    }

    /**
     * 从第一个可见的item开始往后找出PinnedView的位置。不包含第一个可见的item
     */
    private fun findNextPinnedPositionAndLayoutIdFromFirstVisibleItem(recyclerView: RecyclerView): PinnedItem? {
        val firstVisiblePosition = recyclerView.findFirstVisibleItemPosition()
        if (firstVisiblePosition < mItemAdapter.itemCount - 1 && firstVisiblePosition >= 0) {
            for (position in firstVisiblePosition + 1 until mItemAdapter.itemCount) {
                val item = mItemAdapter.get(position)
                if (item is IPinnedItem) {
                    return PinnedItem(
                        item.layoutId,
                        position,
                        item
                    )
                }
            }
        }
        return null
    }

    data class PinnedItem(
        val layoutId: Int,
        var position: Int,
        var data: IPinnedItem,
        var binding: ViewDataBinding? = null
    ) {
        fun copy() =
            PinnedItem(
                layoutId,
                position,
                data,
                binding
            )
    }

    interface OnPinnedItemRenderListener {
        /**
         * pinnedView渲染，数据改变或者布局改变时回调
         *
         * @param viewDataBinding
         * @param layoutId pinnedView对应的视图布局id
         * @param item
         * @param itemPosition pinnedView对应数据在RecyclerView中的位置
         */
        fun onRender(
            viewDataBinding: ViewDataBinding,
            layoutId: Int,
            item: IPinnedItem,
            itemPosition: Int
        )
    }
}