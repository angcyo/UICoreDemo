package com.angcyo.uicore.game.spirit

import android.animation.ArgbEvaluator
import android.graphics.Canvas
import android.graphics.Paint
import com.angcyo.game.core.DrawParams
import com.angcyo.game.core.UpdateParams
import com.angcyo.game.spirit.ColorSpirit
import com.angcyo.library.ex.dp

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ColorMatrixSpirit : ColorSpirit() {

    var spiritStartColor: Int = -1
    var spiritEndColor: Int = -1

    var spiritDrawColor: Int = -1

    var step = 0.08f
    var fraction = 0f

    val argbEvaluator = ArgbEvaluator()

    init {
        roundX = 25 * dp
        roundY = 25 * dp
    }

    override fun draw(canvas: Canvas, drawParams: DrawParams) {
        spiritPaint.style = Paint.Style.FILL_AND_STROKE
        super.draw(canvas, drawParams)
    }

    override fun getSpiritColor(): Int {
        return spiritDrawColor
    }

    override fun update(updateParams: UpdateParams) {
        fraction += step
        if (fraction > 1f || fraction < 0f) {
            step = -step
            fraction += step
        }
        spiritDrawColor = argbEvaluator.evaluate(fraction, spiritStartColor, spiritEndColor) as Int
    }
}