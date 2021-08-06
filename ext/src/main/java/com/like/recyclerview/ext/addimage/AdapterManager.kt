package com.like.recyclerview.ext.addimage

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.utils.add
import com.like.recyclerview.utils.remove
import com.luck.picture.lib.entity.LocalMedia

/**
 * [ItemAdapter]、[PlusAdapter]交互逻辑封装
 *
 * @param getLocalMedias    获取当前已经添加了的所有 [LocalMedia]
 * @param itemCreator       根据 [LocalMedia] 创建 item
 * @param onPlusItemClicked +号被点击回调
 */
class AdapterManager<ValueInList>(
    activity: AppCompatActivity,
    itemAdapter: ItemAdapter<*, ValueInList>,
    plusAdapter: PlusAdapter<*, *>,
    getLocalMedias: () -> List<LocalMedia>,
    itemCreator: (LocalMedia) -> ValueInList,
    onPlusItemClicked: () -> Unit
) {
    private val mAdapter: ConcatAdapter by lazy {
        ConcatAdapter(itemAdapter, plusAdapter)
    }

    init {
        itemAdapter.activity = activity
        itemAdapter.itemCreator = itemCreator
        itemAdapter.getLocalMedias = getLocalMedias
        itemAdapter.notifyRemovePlus = { mAdapter.remove(plusAdapter) }
        itemAdapter.notifyAddPlus = { mAdapter.add(plusAdapter) }

        plusAdapter.activity = activity
        plusAdapter.getLocalMedias = getLocalMedias
        plusAdapter.addItems = { itemAdapter.addLocalMedias(it) }
        plusAdapter.onPlusItemClicked = onPlusItemClicked
    }

    fun getAdapter(): ConcatAdapter = mAdapter
}