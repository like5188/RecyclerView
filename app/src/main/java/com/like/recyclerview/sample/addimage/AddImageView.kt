package com.like.recyclerview.sample.addimage

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.like.recyclerview.ext.addimage.AddImageAdapterManager
import com.like.recyclerview.ext.addimage.ItemAdapter
import com.like.recyclerview.ext.addimage.PlusAdapter
import com.like.recyclerview.layoutmanager.WrapGridLayoutManager
import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.R
import com.like.recyclerview.sample.databinding.ViewAddImageBinding
import com.like.recyclerview.sample.databinding.ViewImageBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import com.luck.picture.lib.entity.LocalMedia
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
        myItemAdapter = MyItemAdapter(maxSelectNum).also { it.onItemChanged = onItemChanged }
        myPlusAdapter = MyPlusAdapter(R.drawable.icon_add, maxSelectNum)
        adapter = AddImageAdapterManager(
            activity = context as AppCompatActivity,
            itemAdapter = myItemAdapter,
            plusAdapter = myPlusAdapter,
            getSelectedLocalMedias = {
                myItemAdapter.mList.map {
                    it.localMedia
                }
            },
            itemCreator = {
                AddImageViewInfo(it)
            },
            onPlusClicked = {
                myItemAdapter.showDeleteButton.set(false)
            }
        ).getConcatAdapter()
    }

    fun getSelectedImages() = myItemAdapter.mList

}

class AddImageViewInfo(val localMedia: LocalMedia) : IRecyclerViewItem {
    override var layoutId: Int = R.layout.view_image

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AddImageViewInfo

        if (localMedia != other.localMedia) return false

        return true
    }

    override fun hashCode(): Int {
        return localMedia.hashCode()
    }

}

/**
 * +号视图需要的数据
 */
data class AddInfo(@DrawableRes val addImageResId: Int) : IRecyclerViewItem {
    override var layoutId: Int = R.layout.view_add_image
}

class MyItemAdapter(maxSelectNum: Int = Int.MAX_VALUE) : ItemAdapter<ViewImageBinding, AddImageViewInfo>(maxSelectNum) {
    val showDeleteButton: ObservableBoolean = ObservableBoolean()
    var onItemChanged: (() -> Unit)? = null

    override fun onBindViewHolder(holder: BindingViewHolder<ViewImageBinding>, item: AddImageViewInfo) {
        super.onBindViewHolder(holder, item)
        holder.binding.iv.load(File(item.localMedia.compressPath))
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

    override fun onAdded() {
        onItemChanged?.invoke()
    }

    override fun onRemoved() {
        onItemChanged?.invoke()
    }
}

class MyPlusAdapter(
    @DrawableRes addImageResId: Int,
    maxSelectNum: Int = Int.MAX_VALUE
) : PlusAdapter<ViewAddImageBinding, AddInfo>(maxSelectNum) {

    init {
        addToEnd(AddInfo(addImageResId))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewAddImageBinding>, item: AddInfo) {
        super.onBindViewHolder(holder, item)
        holder.binding.iv.setImageResource(item.addImageResId)
    }

}