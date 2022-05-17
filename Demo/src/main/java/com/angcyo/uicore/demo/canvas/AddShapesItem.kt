package com.angcyo.uicore.demo.canvas

import com.angcyo.library.ex._string
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class AddShapesItem : CanvasControlItem() {

    init {
        itemIco = R.drawable.canvas_shapes_ico
        itemText = _string(R.string.canvas_shapes)
    }
}