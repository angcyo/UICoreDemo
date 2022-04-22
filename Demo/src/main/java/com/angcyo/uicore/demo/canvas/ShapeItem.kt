package com.angcyo.uicore.demo.canvas

import android.graphics.Path
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.items.renderer.addDrawableRenderer
import com.angcyo.canvas.items.renderer.addShapeRenderer
import com.angcyo.library.ex._drawable

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/22
 */
class ShapeItem(canvasView: CanvasView) : BaseCanvasAddItem(canvasView) {

    var shapePath: Path? = null

    init {
        itemClick = {
            if (shapePath == null) {
                _drawable(addIco)?.let {
                    canvasView.addDrawableRenderer(it)
                }
            } else {
                shapePath?.let {
                    canvasView.addShapeRenderer(it)
                }
            }
        }
    }

}