package com.angcyo.uicore.dslitem

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.itemIndexPosition
import com.angcyo.dsladapter.margin
import com.angcyo.library.ex.dpi
import com.angcyo.library.toast
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/05/14
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class AppFocusItem : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.app_item_focus

        margin(2 * dpi)

        itemClick = {
            toast("click:${itemIndexPosition()}")
        }
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)
        itemHolder.tv(R.id.lib_text_view)?.text = "position:$itemPosition"
    }
}