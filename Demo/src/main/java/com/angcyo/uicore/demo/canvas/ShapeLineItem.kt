package com.angcyo.uicore.demo.canvas

import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.items.renderer.addLineRenderer
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/22
 */
class ShapeLineItem(val canvasView: CanvasView) : CanvasControlItem() {

    init {
        itemIco = R.drawable.shape_line_ico
        itemText = "线条"
        itemClick = {
            canvasView.addLineRenderer()
        }
    }

}