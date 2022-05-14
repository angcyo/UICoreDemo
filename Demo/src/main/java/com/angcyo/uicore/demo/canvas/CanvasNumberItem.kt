package com.angcyo.uicore.demo.canvas

import android.view.Gravity
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.item.style.ITextItem
import com.angcyo.item.style.TextItemConfig
import com.angcyo.uicore.demo.R

/**
 * 画布数字输入item
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/14
 */
class CanvasNumberItem : DslAdapterItem(), ITextItem {

    override var textItemConfig: TextItemConfig = TextItemConfig()

    init {
        itemLayoutId = R.layout.canvas_number_item_layout
        itemClickThrottleInterval = 0
        textItemConfig.itemTextStyle.textGravity = Gravity.CENTER
    }
}