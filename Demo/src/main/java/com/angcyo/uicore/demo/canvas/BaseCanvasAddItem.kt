package com.angcyo.uicore.demo.canvas

import com.angcyo.canvas.CanvasView
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
open class BaseCanvasAddItem(val canvasView: CanvasView) : DslAdapterItem() {

    /**图标资源*/
    var addIco: Int = 0

    /**文本*/
    var addText: CharSequence? = null

    init {
        itemLayoutId = R.layout.item_canvas_add_layout
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        itemHolder.img(R.id.image_view)?.setImageResource(addIco)
        itemHolder.tv(R.id.text_view)?.text = addText
    }
}