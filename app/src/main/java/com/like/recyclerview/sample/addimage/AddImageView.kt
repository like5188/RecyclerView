package com.like.recyclerview.sample.addimage

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.like.common.util.selectMultiplePhoto
import com.like.common.util.selectSinglePhoto
import com.like.recyclerview.adapter.BaseListAdapter
import com.like.recyclerview.ext.addimage.ItemAdapter
import com.like.recyclerview.layoutmanager.WrapGridLayoutManager
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ViewAddImageBinding
import com.like.recyclerview.sample.databinding.ViewImageBinding
import com.like.recyclerview.utils.ItemTouchHelperCallback
import com.like.recyclerview.utils.add
import com.like.recyclerview.utils.remove
import com.like.recyclerview.viewholder.BindingViewHolder
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.coroutines.launch
import java.io.File

/**
 *  添加图片视图
 */
class AddImageView(context: Context, attrs: AttributeSet) : RecyclerView(context, attrs) {
    private lateinit var myItemAdapter: MyItemAdapter
    private lateinit var myPlusAdapter: MyPlusAdapter

    init {
        layoutManager = WrapGridLayoutManager(context, 4)
    }

    /**
     * @param maxSelectNum  最大图片选择数量
     * @param onItemChanged 添加删除图片监听
     */
    fun init(maxSelectNum: Int = Int.MAX_VALUE, onItemChanged: (() -> Unit)? = null) {
        val activity = context as AppCompatActivity
        myItemAdapter = MyItemAdapter()
        myPlusAdapter = MyPlusAdapter(R.drawable.icon_add)
        val addImageAdapter = ConcatAdapter(myItemAdapter, myPlusAdapter)
        with(myItemAdapter) {
            this.onItemChanged = onItemChanged
            this.onRemovePlus = {
                addImageAdapter.remove(myPlusAdapter)
            }
            this.onAddPlus = {
                addImageAdapter.add(myPlusAdapter)
            }
        }
        with(myPlusAdapter) {
            addOnItemClickListener {
                activity.lifecycleScope.launch {
                    if (maxSelectNum == 1) {
                        activity.selectSinglePhoto()?.apply {
                            myItemAdapter.addLocalMedias(listOf(this), maxSelectNum)
                        }
                    } else {
                        activity.selectMultiplePhoto(myItemAdapter.currentList, maxSelectNum)?.apply {
                            myItemAdapter.addLocalMedias(this, maxSelectNum)
                        }
                    }
                }
                myItemAdapter.showDeleteButton.set(false)
            }
        }
        adapter = addImageAdapter
    }

}

class MyItemAdapter : ItemAdapter<ViewImageBinding>(object : DiffUtil.ItemCallback<LocalMedia>() {
    override fun areItemsTheSame(oldItem: LocalMedia, newItem: LocalMedia): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LocalMedia, newItem: LocalMedia): Boolean {
        return oldItem.compressPath == newItem.compressPath
    }
}) {
    private val mItemTouchHelper by lazy {
        ItemTouchHelper(ItemTouchHelperCallback(this))
    }
    val showDeleteButton: ObservableBoolean = ObservableBoolean()
    var onItemChanged: (() -> Unit)? = null
    var onRemovePlus: (() -> Unit)? = null
    var onAddPlus: (() -> Unit)? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewImageBinding>, item: LocalMedia?) {
        item ?: return
        holder.binding.iv.load(File(item.compressPath))
        holder.binding.root.setOnLongClickListener {
            // 显示删除按钮
            if (!showDeleteButton.get()) {
                showDeleteButton.set(true)
            }
            true
        }
        holder.binding.ivDelete.setOnClickListener {
            removeItem(holder.bindingAdapterPosition)
        }
        holder.binding.showDeleteButton = showDeleteButton
    }

    override fun getItemViewType(position: Int, item: LocalMedia?): Int {
        return R.layout.view_image
    }

    override fun onAdded() {
        onItemChanged?.invoke()
    }

    override fun onRemoved() {
        onItemChanged?.invoke()
    }

    override fun onRemovePlus() {
        onRemovePlus?.invoke()
    }

    override fun onAddPlus() {
        onAddPlus?.invoke()
    }

}

class MyPlusAdapter(
    @DrawableRes private val addImageResId: Int
) : BaseListAdapter<ViewAddImageBinding, Any>(object : DiffUtil.ItemCallback<Any>() {
    override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
        return true
    }

    override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
        return true
    }
}) {

    override fun onBindViewHolder(holder: BindingViewHolder<ViewAddImageBinding>, item: Any?) {
        holder.binding.iv.setImageResource(addImageResId)
    }

    override fun getItemViewType(position: Int, item: Any?): Int {
        return R.layout.view_add_image
    }

    override fun getItemCount(): Int {
        return 1
    }

}
