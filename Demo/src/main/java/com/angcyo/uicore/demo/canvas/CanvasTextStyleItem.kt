package com.angcyo.uicore.demo.canvas

import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.items.renderer.IItemRenderer
import com.angcyo.canvas.items.renderer.PictureTextItemRenderer
import com.angcyo.canvas.items.renderer.TextItemRenderer
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.color
import com.angcyo.library.ex.have
import com.angcyo.library.ex.toColorInt
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class CanvasTextStyleItem(
    val renderer: IItemRenderer<*>,
    val textStyle: Int,
    val ico: Int,
    val canvasView: CanvasView
) : DslAdapterItem() {

    init {
        itemLayoutId = R.layout.layout_canvas_text_style

        itemClick = {
            if (renderer is TextItemRenderer) {
                val have = renderer.rendererItem?.textStyle?.have(textStyle) == true
                renderer.enableTextStyle(textStyle, !have)
                updateAdapterItem()
            } else if (renderer is PictureTextItemRenderer) {
                val have = renderer.rendererItem?.textStyle?.have(textStyle) == true
                renderer.enableTextStyle(textStyle, !have)
                updateAdapterItem()
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

        itemHolder.img(R.id.image_view)?.apply {
            if (renderer is TextItemRenderer) {
                val have = renderer.rendererItem?.textStyle?.have(textStyle) == true
                val drawable =
                    _drawable(ico).color(if (have) "#282828".toColorInt() else "#b3b7ba".toColorInt())
                setImageDrawable(drawable)
            } else if (renderer is PictureTextItemRenderer) {
                val have = renderer.rendererItem?.textStyle?.have(textStyle) == true
                val drawable =
                    _drawable(ico).color(if (have) "#282828".toColorInt() else "#b3b7ba".toColorInt())
                setImageDrawable(drawable)
            }
        }
    }

}