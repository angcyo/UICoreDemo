package com.angcyo.uicore.demo.canvas

import android.graphics.Paint
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.items.renderer.TextItemRenderer
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex.color
import com.angcyo.library.ex.toColorInt
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.clickIt

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class TextSolidStyleItem(val textItemRenderer: TextItemRenderer, val canvasView: CanvasView) :
    DslAdapterItem() {

    init {
        itemLayoutId = R.layout.layout_canvas_text_solid_style
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        val strokeView = itemHolder.img(R.id.text_stroke_view)
        val solidView = itemHolder.img(R.id.text_solid_view)

        val isStroke = textItemRenderer.paint.style == Paint.Style.STROKE
        strokeView?.apply {
            val drawable =
                drawable.color(if (isStroke) "#282828".toColorInt() else "#b3b7ba".toColorInt())
            setImageDrawable(drawable)

            clickIt {
                textItemRenderer.updatePaintStyle(Paint.Style.STROKE)
                updateAdapterItem()
            }
        }
        solidView?.apply {
            val drawable =
                drawable.color(if (!isStroke) "#282828".toColorInt() else "#b3b7ba".toColorInt())
            setImageDrawable(drawable)

            clickIt {
                textItemRenderer.updatePaintStyle(Paint.Style.FILL)
                updateAdapterItem()
            }
        }
    }

}