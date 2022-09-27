package com.like.recyclerview.ext.addimage

import android.content.Context
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.like.recyclerview.adapter.BaseListAdapter
import com.luck.picture.lib.entity.LocalMedia

/**
 * 带一个"+"号的选择器，用于选择图片、视频等。
 * 功能：在添加、删除 item 时，通过是否达到[maxSelectNum]来控制"+"号 item 的显示隐藏。
 */
abstract class ItemAdapter<VB : ViewDataBinding>(
    diffCallback: DiffUtil.ItemCallback<LocalMedia>
) : BaseListAdapter<VB, LocalMedia>(diffCallback) {
    private lateinit var context: Context
    var maxSelectNum: Int = Int.MAX_VALUE

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.context = recyclerView.context
    }

    fun addLocalMedias(list: List<LocalMedia>) {
        // 去掉已经添加过的
        val needAddItems = list.filter { !currentList.contains(it) }
        val curCount = itemCount
        when {
            curCount + needAddItems.size < maxSelectNum -> {
                val newItems = currentList.toMutableList()
                newItems.addAll(needAddItems)
                submitList(newItems) {
                    onAdded()
                }
            }
            curCount + needAddItems.size == maxSelectNum -> {// 移除+号
                onRemovePlus()
                val newItems = currentList.toMutableList()
                newItems.addAll(needAddItems)
                submitList(newItems) {
                    onAdded()
                }
            }
            else -> {// 不能添加
                Toast.makeText(context, "只能添加 $maxSelectNum 张图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removeItem(position: Int) {
        val curCount = itemCount
        when {
            curCount < maxSelectNum -> {
                val newItems = currentList.toMutableList()
                newItems.removeAt(position)
                submitList(newItems) {
                    onRemoved()
                }
            }
            curCount == maxSelectNum -> {// 添加+号
                val newItems = currentList.toMutableList()
                newItems.removeAt(position)
                submitList(newItems) {
                    onRemoved()
                }
                onAddPlus()
            }
        }
    }

    abstract fun onAdded()
    abstract fun onRemoved()
    abstract fun onRemovePlus()
    abstract fun onAddPlus()

}
