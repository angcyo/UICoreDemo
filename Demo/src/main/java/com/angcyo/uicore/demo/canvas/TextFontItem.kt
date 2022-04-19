package com.angcyo.uicore.demo.canvas

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.color
import com.angcyo.library.ex.toColorInt
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class TextFontItem : DslAdapterItem() {

    init {
        itemLayoutId = R.layout.layout_canvas_text_style
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        itemHolder.img(R.id.image_view)?.apply {
            val drawable = _drawable(R.drawable.text_font_ico).color("#282828".toColorInt())
            setImageDrawable(drawable)
        }
    }
}