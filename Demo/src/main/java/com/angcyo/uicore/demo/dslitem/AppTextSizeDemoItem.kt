package com.angcyo.uicore.demo.dslitem

import android.graphics.Paint
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.annotation.Pixel
import com.angcyo.library.ex.*
import com.angcyo.library.unit.InchValueUnit
import com.angcyo.library.unit.MmValueUnit
import com.angcyo.library.unit.PointValueUnit
import com.angcyo.library.unit.unitDecimal
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/12/04
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class AppTextSizeDemoItem : DslAdapterItem() {

    var itemDemoText: CharSequence? = null

    /**dp*/
    var itemDemoTextSize: Float = 9f

    init {
        itemLayoutId = R.layout.app_item_text_size
    }

    val ptValueUnit = PointValueUnit()
    val mmValueUnit = MmValueUnit()
    val inValueUnit = InchValueUnit()

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.lib_text_view)?.apply {
            text = itemDemoText
            textSize = itemDemoTextSize.toDp()
        }

        //单位切换
        val dp = itemDemoTextSize
        val px = dp.toDp()

        val paint = itemHolder.tv(R.id.lib_text_view)?.paint ?: Paint()

        val w = paint.textWidth("${itemDemoText?.get(0)}")
        val h = paint.textHeight()
        val size1 = (h.toDpFromPixel() * 0.7535 - 0.00000007).toFloat().toDp()
        val h2 = paint.textBounds("$itemDemoText").height()
        val size2 = (h2.toDpFromPixel() * 0.7535 - 0.00000007).toFloat().toDp()

        itemHolder.tv(R.id.lib_des_view)?.text = span {
            append("v:->")
            append(px)

            appendln()
            append("w:->")
            append(w)

            appendln()
            append("h:->")
            append(h)
            appendln()
            append("h′:->")
            append(size1)

            appendln()
            append("c:->")
            append(c(w, h).toFloat())

            appendln()
            append("h2:->")
            append(h2.toFloat())
            appendln()
            append("h2′:->")
            append(size2)
        }
    }

    /**单位转换*/
    fun Appendable.append(@Pixel px: Float) {
        val dp = px.toDpFromPixel().unitDecimal()
        val pt = ptValueUnit.convertPixelToValue(px).unitDecimal()
        val mm = mmValueUnit.convertPixelToValue(px).unitDecimal()
        val inch = inValueUnit.convertPixelToValue(px).unitDecimal()
        append("${dp}dp ${px.unitDecimal()}px ${pt}pt ${mm}mm ${inch}in")
    }

}