package com.angcyo.uicore.demo.canvas

import com.angcyo.canvas.CanvasView
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class AddImageItem(canvasView: CanvasView) : BaseCanvasAddItem(canvasView) {

    init {
        addIco = R.drawable.add_image_ico
        addText = "图片"
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
    }
}