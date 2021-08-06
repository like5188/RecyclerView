package com.like.recyclerview.ext.adapter.addimage

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import com.like.recyclerview.utils.add
import com.like.recyclerview.utils.remove
import com.luck.picture.lib.entity.LocalMedia

class AdapterManager<ValueInList>(
    activity: AppCompatActivity,
    itemAdapter: ItemAdapter<*, ValueInList>,
    plusAdapter: PlusAdapter<*, *>,
    getLocalMedias: () -> List<LocalMedia>,
    transformer: (LocalMedia) -> ValueInList,
    onPlusItemClicked: () -> Unit
) {
    private val mAdapter: ConcatAdapter by lazy {
        ConcatAdapter(itemAdapter, plusAdapter)
    }

    init {
        itemAdapter.activity = activity
        itemAdapter.transformer = transformer
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