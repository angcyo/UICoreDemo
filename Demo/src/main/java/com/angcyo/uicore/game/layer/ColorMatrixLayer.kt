package com.angcyo.uicore.game.layer

import android.graphics.Color
import com.angcyo.game.layer.BaseLayer
import com.angcyo.library.ex.dpi
import com.angcyo.uicore.game.spirit.ColorMatrixSpirit

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ColorMatrixLayer : BaseLayer() {

    //精灵大小
    val spiritSize = 10 * dpi

    //间隙
    val spiritSpace = 2 * dpi

    override fun onLayerUpdate(width: Float, height: Float) {
        super.onLayerUpdate(width, height)

        clearSpirit()

        var useWidth = 0f
        var useHeight = 0f

        //横向填充
        while (useHeight <= height) {
            val top = useHeight + spiritSpace

            while (useWidth <= width) {
                val left = useWidth + spiritSpace
                addSpirit(ColorMatrixSpirit().apply {
                    spiritRectF.set(left, top, left + spiritSize, top + spiritSize)

                    if (top < 1 * height / 3) {
                        spiritStartColor = Color.RED
                        spiritEndColor = Color.BLUE
                    } else if (top < 2 * height / 3) {
                        spiritStartColor = Color.YELLOW
                        spiritEndColor = Color.GREEN
                    } else {
                        spiritStartColor = Color.LTGRAY
                        spiritEndColor = Color.MAGENTA
                    }
                })
                useWidth = left + spiritSize
            }

            useWidth = 0f
            useHeight = top + spiritSize
        }
    }
}