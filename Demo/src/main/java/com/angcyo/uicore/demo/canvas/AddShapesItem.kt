package com.angcyo.uicore.demo.canvas

import com.angcyo.canvas.CanvasView
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class AddShapesItem(canvasView: CanvasView) : BaseCanvasAddItem(canvasView) {

    init {
        addIco = R.drawable.add_shapes_ico
        addText = "形状"
    }
}