package com.like.recyclerview.ext.addimage

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.utils.add
import com.like.recyclerview.utils.remove
import com.luck.picture.lib.entity.LocalMedia

/**
 * [ItemAdapter]、[PlusAdapter]交互逻辑封装
 *
 * @param getSelectedLocalMedias    获取当前已经添加了的所有 [LocalMedia]
 * @param itemCreator       根据 [LocalMedia] 创建 item
 * @param onPlusClicked +号被点击回调
 */
class AddImageAdapterManager<ValueInList>(
    activity: AppCompatActivity,
    itemAdapter: ItemAdapter<*, ValueInList>,
    plusAdapter: PlusAdapter<*, *>,
    getSelectedLocalMedias: () -> List<LocalMedia>,
    itemCreator: (LocalMedia) -> ValueInList,
    onPlusClicked: () -> Unit
) {
    private val mAdapter: ConcatAdapter by lazy {
        ConcatAdapter(itemAdapter, plusAdapter)
    }

    init {
        itemAdapter.activity = activity
        itemAdapter.itemCreator = itemCreator
        itemAdapter.getSelectedLocalMedias = getSelectedLocalMedias
        itemAdapter.notifyRemovePlus = { mAdapter.remove(plusAdapter) }
        itemAdapter.notifyAddPlus = { mAdapter.add(plusAdapter) }

        plusAdapter.activity = activity
        plusAdapter.getSelectedLocalMedias = getSelectedLocalMedias
        plusAdapter.onSelected = { itemAdapter.addLocalMedias(it) }
        plusAdapter.onPlusClicked = onPlusClicked
    }

    fun getConcatAdapter(): ConcatAdapter = mAdapter
}