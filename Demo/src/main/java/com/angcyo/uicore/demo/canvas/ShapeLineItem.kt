package com.angcyo.uicore.demo.canvas

import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.items.renderer.addLineRenderer
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/22
 */
class ShapeLineItem(canvasView: CanvasView) : BaseCanvasAddItem(canvasView) {

    init {
        addIco = R.drawable.shape_line_ico
        addText = "线条"
        itemClick = {
            canvasView.addLineRenderer()
        }
    }

}