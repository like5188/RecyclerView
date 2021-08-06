package com.like.recyclerview.sample.addimage

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import com.like.common.util.CoilEngine
import com.like.recyclerview.adapter.AbstractAdapter
import com.like.recyclerview.sample.databinding.ViewAddImageBinding
import com.like.recyclerview.viewholder.BindingViewHolder
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

abstract class AbstractPlusAdapter(
    private val activity: AppCompatActivity,
    @DrawableRes addImageResId: Int
) : AbstractAdapter<ViewAddImageBinding, AddInfo>() {

    init {
        addToEnd(AddInfo(addImageResId))
    }

    override fun onBindViewHolder(holder: BindingViewHolder<ViewAddImageBinding>, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = get(position) ?: return
        val binding = holder.binding
        binding.iv.setImageResource(item.addImageResId)
        binding.iv.setOnClickListener {
            val localMedias = getLocalMedias()
            PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(9)
                .imageEngine(CoilEngine.create())
                .selectionData(localMedias)
                .imageSpanCount(3)// 每行显示个数 int
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isCompress(true)// 是否压缩 true or false
                .isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中) true or false
                .minimumCompressSize(100)// 小于100kb的图片不压缩
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>?) {
                        if (result.isNullOrEmpty()) return
                        // 例如 LocalMedia 里面返回五种path
                        // 1.media.getPath(); 为原图path
                        // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
                        // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
                        // 4.media.getOriginalPath()); media.isOriginal());为true时此字段才有值
                        // 5.media.getAndroidQToPath();为Android Q版本特有返回的字段，此字段有值就用来做上传使用
                        // 如果同时开启裁剪和压缩，则取压缩路径为准因为是先裁剪后压缩
                        for (media in result) {
                            Log.i(PictureSelectorActivity.TAG, "是否压缩:" + media.isCompressed)
                            Log.i(PictureSelectorActivity.TAG, "压缩:" + media.compressPath)
                            Log.i(PictureSelectorActivity.TAG, "原图:" + media.path)
                            Log.i(PictureSelectorActivity.TAG, "是否裁剪:" + media.isCut)
                            Log.i(PictureSelectorActivity.TAG, "裁剪:" + media.cutPath)
                            Log.i(PictureSelectorActivity.TAG, "是否开启原图:" + media.isOriginal)
                            Log.i(PictureSelectorActivity.TAG, "原图路径:" + media.originalPath)
                            Log.i(PictureSelectorActivity.TAG, "Android Q 特有Path:" + media.androidQToPath)
                        }
                        addItems(result)
                    }

                    override fun onCancel() {
                    }
                })
            onAddClicked()
        }
    }

    abstract fun getLocalMedias(): List<LocalMedia>
    abstract fun addItems(list: List<LocalMedia>)
    abstract fun onAddClicked()
}
