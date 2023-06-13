package com.angcyo.uicore.widget

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.component.lastContext
import com.angcyo.library.ex.elseBlank
import com.angcyo.library.ex.openUrl
import com.angcyo.library.ex.toFullTime
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2023/06/13
 */
class IdiomHistoryItem : DslAdapterItem() {

    init {
        itemLayoutId = R.layout.appwidget_idiom
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        itemHolder.gone(R.id.history_view, R.id.lib_image_view)

        (itemData as? IdiomInfoBean)?.let {
            itemHolder.tv(R.id.lib_title_view)?.text = it.name
            itemHolder.tv(R.id.lib_tip_view)?.text = it.pronounce
            itemHolder.tv(R.id.lib_body_view)?.text = it.des
            itemHolder.tv(R.id.lib_des_view)?.text =
                it.sample.elseBlank(it.provenance).elseBlank(it.synonym)

            itemHolder.tv(R.id.statistics_view)?.text = it.time.toFullTime()

            val url = it.url
            itemHolder.clickItem {
                lastContext.openUrl(url)
            }
        }
    }

}