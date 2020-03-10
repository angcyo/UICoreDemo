package com.angcyo.uicore.game.layer

import android.graphics.Color
import com.angcyo.game.core.UpdateParams
import com.angcyo.game.layer.BaseLayer
import com.angcyo.library.ex.dpi
import com.angcyo.uicore.game.spirit.ColorMatrixSpirit
import kotlin.math.roundToInt

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

        val spSize = (spiritSize + spiritSpace)
        val maxRow: Int = (height / spSize).roundToInt()
        val maxCol: Int = (width / spSize).roundToInt()

        //横向填充
        while (useHeight <= height) {
            val top = useHeight + spiritSpace
            val row: Int = (top / spSize).roundToInt()

            while (useWidth <= width) {
                val left = useWidth + spiritSpace
                val col: Int = (left / spSize).roundToInt()

                addSpirit(ColorMatrixSpirit().apply {
                    spiritRectF.set(left, top, left + spiritSize, top + spiritSize)

                    spiritRow = row
                    spiritColumn = col

//                    if (left < (width / 2 + spSize / 2) &&
//                        ((left - top).abs() <= spSize * 5 ||
//                                (left - (width - top)).abs() <= spSize * 5)
//                    ) {
//                        spiritStartColor = Color.BLACK
//                        spiritEndColor = Color.BLACK
//                    } else if (top < 1 * height / 3) {
//                        spiritStartColor = Color.RED
//                        spiritEndColor = Color.BLUE
//                    } else if (top < 2 * height / 3) {
//                        spiritStartColor = Color.YELLOW
//                        spiritEndColor = Color.GREEN
//                    } else {
//                        spiritStartColor = Color.LTGRAY
//                        spiritEndColor = Color.MAGENTA
//                    }
                })
                useWidth = left + spiritSize
            }

            useWidth = 0f
            useHeight = top + spiritSize
        }
    }

    var updateTime = 0L
    var start = 0
    var count = 4

    override fun update(updateParams: UpdateParams) {
        super.update(updateParams)

        val spSize = (spiritSize + spiritSpace)
        val maxRow: Int = (layerRectF.height() / spSize).roundToInt()
        val maxCol: Int = (layerRectF.width() / spSize).roundToInt()

        if (updateTime == 0L || updateParams.updateCurrentTime - updateTime > 600) {
            //600毫秒更新一次
            updateTime = updateParams.updateCurrentTime
            start += 1
            if (start > maxCol / 2 - count / 2) {
                start = 0
            }
        }

        spiritList.forEach {
            (it as? ColorMatrixSpirit)?.apply {
                if ((spiritColumn in start..start + count && spiritRow in start..maxRow - start) ||
                    (maxCol - spiritColumn in start..start + count && spiritRow in start..maxRow - start) ||
                    (spiritRow in start..start + count && spiritColumn in start..maxCol - start) ||
                    (maxRow - spiritRow in start..start + count && spiritColumn in start..maxCol - start)
                ) {
                    spiritStartColor = Color.BLACK
                    spiritEndColor = 0
                } else {
                    spiritStartColor = Color.LTGRAY
                    spiritEndColor = Color.MAGENTA
                }
            }
        }
    }
}