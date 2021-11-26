package com.like.recyclerview.ext.addimage

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.previewPhotos
import com.like.recyclerview.adapter.BaseAdapter
import com.like.recyclerview.utils.ItemTouchHelperCallback
import com.like.recyclerview.viewholder.BindingViewHolder
import com.luck.picture.lib.entity.LocalMedia

open class ItemAdapter<VB : ViewDataBinding, ValueInList>(
    private val maxSelectNum: Int = Int.MAX_VALUE
) : BaseAdapter<VB, ValueInList>() {
    lateinit var activity: AppCompatActivity
    lateinit var itemCreator: (LocalMedia) -> ValueInList
    lateinit var getSelectedLocalMedias: () -> List<LocalMedia>
    lateinit var notifyRemovePlus: () -> Unit
    lateinit var notifyAddPlus: () -> Unit
    private val mItemTouchHelper by lazy {
        ItemTouchHelper(ItemTouchHelperCallback(this))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, binding: VB, position: Int, item: ValueInList) {
        super.onBindViewHolder(holder, binding, position, item)
        holder.binding.root.setOnClickListener {
            activity.previewPhotos(getSelectedLocalMedias(), position)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun addLocalMedias(list: List<LocalMedia>) {
        val localMedias = getSelectedLocalMedias()
        // 去掉已经添加过的
        val items = list.filter { !localMedias.contains(it) }.map {
            itemCreator(it)
        }
        val curCount = mList.size
        when {
            curCount + items.size < maxSelectNum -> {
                addAllToEnd(items)
                onAdded()
            }
            curCount + items.size == maxSelectNum -> {// 移除+号
                notifyRemovePlus()
                addAllToEnd(items)
                onAdded()
            }
            else -> {// 不能添加
                Toast.makeText(activity, "只能添加 $maxSelectNum 张图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removeItem(position: Int) {
        val curCount = mList.size
        when {
            curCount < maxSelectNum -> {
                remove(position)
                onRemoved()
            }
            curCount == maxSelectNum -> {// 添加+号
                remove(position)
                notifyAddPlus()
                onRemoved()
            }
        }
    }

    open fun onAdded() {

    }

    open fun onRemoved() {

    }

}
