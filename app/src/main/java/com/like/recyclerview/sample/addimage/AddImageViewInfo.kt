package com.like.recyclerview.sample.addimage

import com.like.recyclerview.model.IRecyclerViewItem
import com.like.recyclerview.sample.R
import com.luck.picture.lib.entity.LocalMedia

class AddImageViewInfo(val localMedia: LocalMedia, val des: String) : IRecyclerViewItem {
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
