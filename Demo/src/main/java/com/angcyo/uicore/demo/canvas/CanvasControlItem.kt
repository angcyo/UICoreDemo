package com.angcyo.uicore.demo.canvas

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.color
import com.angcyo.library.ex.toColorInt
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
open class CanvasControlItem : DslAdapterItem() {

    /**图标资源*/
    var itemIco: Int = 0

    /**图标的颜色*/
    var itemIcoColor: Int = "#363d4b".toColorInt()

    /**文本*/
    var itemText: CharSequence? = null

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

        val drawable = _drawable(itemIco).color(itemIcoColor)
        itemHolder.img(R.id.image_view)?.setImageDrawable(drawable)
        itemHolder.tv(R.id.text_view)?.text = itemText
    }
}