package com.like.recyclerview.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

/**
 * 自定义的颜色分割线。
 * 用RecyclerView的addItemDecoration()方法设置
 *
 * @param type          item尺寸变化类型。
 * 0：一行中的除开最后一个位置的前面的所有item的宽度都各自减少相应的size值，最后一个item保持不变。
 * 1：一行中的所有item平分减少的值。此时size最好是能够被spanCount整除，否则会达不到完美效果。
 * @param size          分割线尺寸px。默认为1px。size最好是能够被spanCount整除，否则会达不到完美效果或者显示不出来。
 * @param dividerColor  分割线的颜色。默认为透明的
 */
class ColorLineItemDecoration(
    private val type: Int = 0,
    @Px private val size: Int = 1,
    @ColorInt private val dividerColor: Int = Color.TRANSPARENT
) : RecyclerView.ItemDecoration() {
    private val mPaint by lazy { Paint().apply { this.color = dividerColor } }

    /**
     * 在每次测量item尺寸时被调用，将decoration的尺寸计算到item的尺寸中
     *
     * type为0时：
     * 一个spanCount列的列表，加上divider后，一行Item减少的宽度为 size*(spanCount-1)
     * 而代码中把这一行所减少的宽度平摊在0 ~ (spanCount-2)位置的Item上，最后一列未做减少，故会导致最后一列Item变大
     *
     * type为1时：
     * 一个spanCount列的列表，加上divider后，一行Item减少的宽度为 size*(spanCount-1)
     * 则每个Item需减少 size*(spanCount-1)/spanCount
     * 相邻Item间距必须 是 size
     * 第一列Item的左边和最后一列Item的右边偏移必须是0
     */
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val spanCount: Int = getSpanCount(view, parent)
        val position = parent.getChildAdapterPosition(view)

        if (position < 0) return
        if (size <= 0) return

        when (type) {
            0 -> {
                outRect.set(0, 0, if (isLastColumn(view, parent)) 0 else size, if (isLastRaw(view, parent)) 0 else size)
            }
            1 -> {
                val column = position % spanCount// Item在行中的位置
                val left = size * column / spanCount
                val top = 0
                val right = size * (spanCount - 1) / spanCount - left
                val bottom = if (isLastRaw(view, parent)) {
                    0
                } else {
                    size
                }
                outRect.set(left, top, right, bottom)
            }
        }
    }

    /**
     * 在item被绘制之后调用，将指定的内容绘制到item view内容之上
     */
    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
    }

    /**
     * 在item绘制之前时被调用，将指定的内容绘制到item view内容之下
     */
    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (dividerColor == Color.TRANSPARENT) return
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            if (!isLastRaw(child, parent)) {
                // 画水平分割线
                c.drawRect(
                    (child.left - params.leftMargin).toFloat(),
                    (child.bottom + params.bottomMargin).toFloat(),
                    (child.right + params.rightMargin + size).toFloat(),
                    (child.bottom + params.bottomMargin + size).toFloat(),
                    mPaint
                )
            }
            if (!isLastColumn(child, parent)) {
                // 画垂直分割线
                c.drawRect(
                    (child.right + params.rightMargin).toFloat(),
                    (child.top - params.topMargin).toFloat(),
                    (child.right + params.rightMargin + size).toFloat(),
                    (child.bottom + params.bottomMargin).toFloat(),
                    mPaint
                )
            }
        }
    }

    private fun isLastRaw(view: View, recyclerView: RecyclerView): Boolean {
        val itemCount = recyclerView.adapter?.itemCount ?: -1
        val layoutManager = recyclerView.layoutManager
        val position = recyclerView.getChildAdapterPosition(view)
        val layoutParams = view.layoutParams

        when (layoutManager) {
            is GridLayoutManager -> {
                if (layoutParams is GridLayoutManager.LayoutParams) {
                    val spanCount = layoutManager.spanCount
                    return position + spanCount >= itemCount
                }
            }
            is StaggeredGridLayoutManager -> {
                if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                    val spanCount = layoutManager.spanCount
                    val orientation = layoutManager.orientation
                    return if (orientation == StaggeredGridLayoutManager.VERTICAL) {// 纵向滚动
                        position + spanCount >= itemCount
                    } else {// 横向滚动
                        (position + 1) % spanCount == 0
                    }
                }
            }
        }
        return false
    }

    private fun isLastColumn(view: View, recyclerView: RecyclerView): Boolean {
        val itemCount = recyclerView.adapter?.itemCount ?: -1
        val layoutManager = recyclerView.layoutManager
        val position = recyclerView.getChildAdapterPosition(view)
        val layoutParams = view.layoutParams

        when (layoutManager) {
            is GridLayoutManager -> {
                if (layoutParams is GridLayoutManager.LayoutParams) {
                    val spanCount = layoutManager.spanCount
                    return (position + 1) % spanCount == 0
                }
            }
            is StaggeredGridLayoutManager -> {
                if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                    val spanCount = layoutManager.spanCount
                    val orientation = layoutManager.orientation
                    return if (orientation == StaggeredGridLayoutManager.VERTICAL) {// 纵向滚动
                        (position + 1) % spanCount == 0
                    } else {// 横向滚动
                        position >= itemCount
                    }
                }
            }
        }
        return false
    }

    private fun getSpanCount(view: View, recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager
        val layoutParams = view.layoutParams
        when (layoutManager) {
            is GridLayoutManager -> {
                if (layoutParams is GridLayoutManager.LayoutParams) {
                    return layoutManager.spanCount
                }
            }
            is StaggeredGridLayoutManager -> {
                if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                    return layoutManager.spanCount
                }
            }
        }
        return -1
    }

}
