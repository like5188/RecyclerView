package com.like.recyclerview.sample.addimage

import android.content.Context
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableBoolean
import androidx.recyclerview.widget.DiffUtil
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
        myPlusAdapter = MyPlusAdapter(maxSelectNum, R.drawable.icon_add)
        adapter = AddImageAdapterManager(
            activity = context as AppCompatActivity,
            itemAdapter = myItemAdapter,
            plusAdapter = myPlusAdapter,
            getSelectedLocalMedias = ::getSelectedLocalMedias,
            itemCreator = {
                AddImageViewInfo(it)
            },
            onPlusClicked = {
                myItemAdapter.showDeleteButton.set(false)
            }
        ).getConcatAdapter()
    }

    fun getSelectedLocalMedias() = myItemAdapter.currentList.map {
        it.localMedia
    }

}

class AddImageViewInfo(val localMedia: LocalMedia) : IRecyclerViewItem {
    override var layoutId: Int = R.layout.view_image
}

class MyItemAdapter(maxSelectNum: Int) :
    ItemAdapter<ViewImageBinding, AddImageViewInfo>(object : DiffUtil.ItemCallback<AddImageViewInfo>() {
        override fun areItemsTheSame(oldItem: AddImageViewInfo, newItem: AddImageViewInfo): Boolean {
            return oldItem.localMedia.id == newItem.localMedia.id
        }

        override fun areContentsTheSame(oldItem: AddImageViewInfo, newItem: AddImageViewInfo): Boolean {
            return oldItem.localMedia.compressPath == newItem.localMedia.compressPath
        }
    }, maxSelectNum) {
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
    maxSelectNum: Int,
    @DrawableRes private val addImageResId: Int
) : PlusAdapter<ViewAddImageBinding>(maxSelectNum) {

    override fun onBindViewHolder(holder: BindingViewHolder<ViewAddImageBinding>) {
        holder.binding.iv.setImageResource(addImageResId)
    }

    override fun getLayoutId(): Int {
        return R.layout.view_add_image
    }

}
