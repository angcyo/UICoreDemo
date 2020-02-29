package com.angcyo.uicore.dslitem

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/29
 */
class AppMenuHeaderItem : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.app_item_menu_header
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.text_view)?.text = span {
            append("angcyo")
            appendln()
            append(nowTimeString()) {
                fontSize = 12 * dpi
            }
        }
    }
}