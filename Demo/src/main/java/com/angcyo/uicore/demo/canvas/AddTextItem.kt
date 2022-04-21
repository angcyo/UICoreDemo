package com.angcyo.uicore.demo.canvas

import androidx.fragment.app.Fragment
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.items.renderer.addPictureTextRenderer
import com.angcyo.core.dslitem.IFragmentItem
import com.angcyo.dialog.inputDialog
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class AddTextItem(canvasView: CanvasView) : BaseCanvasAddItem(canvasView), IFragmentItem {

    override var itemFragment: Fragment? = null

    init {
        addIco = R.drawable.add_text_ico
        addText = "文本"

        itemClick = {
            itemFragment?.context?.inputDialog {
                onInputResult = { dialog, inputText ->
                    if (inputText.isNotEmpty()) {
                        //canvasView.addTextRenderer("$inputText")
                        canvasView.addPictureTextRenderer("$inputText")
                    }
                    false
                }
            }
        }
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