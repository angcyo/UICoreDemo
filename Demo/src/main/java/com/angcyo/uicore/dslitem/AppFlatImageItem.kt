package com.angcyo.uicore.dslitem

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.glide.giv
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/21
 */

class AppFlatImageItem(index: Int) : AppImageItem(index) {
    init {
        itemLayoutId = R.layout.app_item_flat_image
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

        itemHolder.giv(R.id.image_view)?.apply {
            maskDrawable = null
            drawBorder = false
        }
    }
}