package com.angcyo.uicore.dslitem

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.uicore.demo.BuildConfig
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/29
 */
class AppMenuFooterItem : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.app_item_menu_footer
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.text_view)?.text = span {
            append("v")
            append(BuildConfig.VERSION_NAME)
            appendln()
            append(BuildConfig.BUILD_TIME)
        }
    }
}