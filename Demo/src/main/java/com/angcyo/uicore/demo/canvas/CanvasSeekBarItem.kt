package com.angcyo.uicore.demo.canvas

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.item.DslSeekBarInfoItem
import com.angcyo.library.ex._color
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.progress.DslSeekBar

/**
 * 画布滑块Item
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/16
 */
class CanvasSeekBarItem : DslSeekBarInfoItem() {

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.v<DslSeekBar>(R.id.lib_seek_view)?.apply {
            val color = _color(R.color.canvas_primary)
            setBgGradientColors("${_color(R.color.canvas_line)}")
            setTrackGradientColors("$color")
            updateThumbColor(color)
        }
    }

}