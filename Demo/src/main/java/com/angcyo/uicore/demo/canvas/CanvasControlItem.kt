package com.angcyo.uicore.demo.canvas

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span

/**
 * 带图文的item
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
open class CanvasControlItem : CanvasIconItem() {

    /**文本*/
    var itemText: CharSequence? = null

    /**上角标*/
    var itemTextSuperscript: CharSequence? = null

    init {
        itemLayoutId = R.layout.item_canvas_control_layout
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.text_view)?.apply {
            text = span {
                append(itemText)
                append(itemTextSuperscript) {
                    isSuperscript = true
                }
            }
            setTextColor(itemColor)
        }
    }
}