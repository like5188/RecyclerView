package com.like.recyclerview.ext.addimage

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.like.common.util.CoilEngine
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.ext.R
import com.like.recyclerview.viewholder.BindingViewHolder
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia

open class ItemAdapter<VB : ViewDataBinding, ValueInList>(
    private val maxImageCount: Int = 9
) : AbstractAdapter<VB, ValueInList>() {
    lateinit var activity: AppCompatActivity
    lateinit var itemCreator: (LocalMedia) -> ValueInList
    lateinit var getLocalMedias: () -> List<LocalMedia>
    lateinit var notifyRemovePlus: () -> Unit
    lateinit var notifyAddPlus: () -> Unit
    private val mItemTouchHelper by lazy {
        ItemTouchHelper(ItemTouchHelperCallback(this))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<VB>, binding: VB, position: Int, item: ValueInList) {
        super.onBindViewHolder(holder, binding, position, item)
        holder.binding.root.setOnClickListener {
            val localMedias = getLocalMedias()
            // 预览图片 可自定长按保存路径
            // 注意 .themeStyle(R.style.theme)；里面的参数不可删，否则闪退...
            PictureSelector.create(activity)
                .themeStyle(R.style.picture_default_style)
                .isNotPreviewDownload(true)
                .imageEngine(CoilEngine.create()) // 请参考Demo GlideEngine.java
                .openExternalPreview(position, localMedias)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    fun addLocalMedias(list: List<LocalMedia>) {
        val localMedias = getLocalMedias()
        // 去掉已经添加过的
        val items = list.filter { !localMedias.contains(it) }.map {
            itemCreator(it)
        }
        val curCount = mList.size
        when {
            curCount + items.size < maxImageCount -> {
                addAllToEnd(items)
            }
            curCount + items.size == maxImageCount -> {// 移除+号
                notifyRemovePlus()
                addAllToEnd(items)
            }
            else -> {// 不能添加
                Toast.makeText(activity, "只能添加 $maxImageCount 张图片", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun removeItem(position: Int) {
        val curCount = mList.size
        when {
            curCount < maxImageCount -> {
                remove(position)
            }
            curCount == maxImageCount -> {// 添加+号
                remove(position)
                notifyAddPlus()
            }
        }
    }

}
