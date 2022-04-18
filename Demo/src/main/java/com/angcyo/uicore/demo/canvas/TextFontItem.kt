package com.angcyo.uicore.demo.canvas

import android.widget.LinearLayout
import com.angcyo.canvas.CanvasView
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class TextFontItem(canvasView: CanvasView) : BaseCanvasAddItem(canvasView) {

    init {
        addIco = R.drawable.text_font_ico
        addText = "字体"

        itemHeight = LinearLayout.LayoutParams.WRAP_CONTENT
    }
}