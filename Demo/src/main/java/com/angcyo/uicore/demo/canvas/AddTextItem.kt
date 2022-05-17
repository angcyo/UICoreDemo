package com.angcyo.uicore.demo.canvas

import androidx.fragment.app.Fragment
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.utils.addPictureTextRender
import com.angcyo.dialog.inputDialog
import com.angcyo.dsladapter.item.IFragmentItem
import com.angcyo.library.ex._string
import com.angcyo.library.ex.dpi
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class AddTextItem(val canvasView: CanvasView) : CanvasControlItem(), IFragmentItem {

    override var itemFragment: Fragment? = null

    init {
        itemIco = R.drawable.canvas_text_ico
        itemText = _string(R.string.canvas_text)

        itemClick = {
            itemFragment?.context?.inputDialog {
                inputViewHeight = 100 * dpi
                onInputResult = { dialog, inputText ->
                    if (inputText.isNotEmpty()) {
                        //canvasView.addTextRenderer("$inputText")
                        //canvasView.addPictureTextRenderer("$inputText")
                        canvasView.addPictureTextRender("$inputText")
                    }
                    false
                }
            }
        }
    }
}