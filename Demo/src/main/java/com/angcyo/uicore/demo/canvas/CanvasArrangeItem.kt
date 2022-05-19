package com.angcyo.uicore.demo.canvas

import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.Strategy
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._dimen
import com.angcyo.library.ex._string
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 * Canvas图层排序
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/19
 */
class CanvasArrangeItem : CanvasControlItem() {

    /**安排方式*/
    var itemArrange: Int = 0
        set(value) {
            field = value
            when (value) {
                CanvasDelegate.ARRANGE_FORWARD -> {
                    itemIco = R.drawable.canvas_arrange_forward
                    itemText = _string(R.string.canvas_forward)
                }
                CanvasDelegate.ARRANGE_BACKWARD -> {
                    itemIco = R.drawable.canvas_arrange_backward
                    itemText = _string(R.string.canvas_backward)
                }
                CanvasDelegate.ARRANGE_FRONT -> {
                    itemIco = R.drawable.canvas_arrange_front
                    itemText = _string(R.string.canvas_front)
                }
                CanvasDelegate.ARRANGE_BACK -> {
                    itemIco = R.drawable.canvas_arrange_back
                    itemText = _string(R.string.canvas_back)
                }
            }
        }

    init {
        itemPaddingTop = _dimen(R.dimen.lib_xhdpi)

        itemClick = {
            itemRenderer?.let {
                itemCanvasDelegate?.arrange(it, itemArrange, Strategy.normal)
            }
        }
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        val renderer = itemRenderer
        itemEnable =
            renderer != null && itemCanvasDelegate?.canArrange(renderer, itemArrange) == true
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
    }

}