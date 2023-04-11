package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.angcyo.library.ex.createPaint
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.textHeight
import com.angcyo.library.ex.textWidth
import kotlin.math.absoluteValue

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/10/28
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DrawTextView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    var text = "angcyo"

    val paint = createPaint().apply {
        strokeWidth = 1 * dp
        textSize = 24 * dp
    }

    val textPath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    var curvature = 0f//曲度[-90~90]

    val textRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //
        canvas.drawLine(measuredWidth / 2f, 0f, measuredWidth / 2f, measuredHeight + 0f, paint)
        canvas.drawLine(0f, measuredHeight / 2f, measuredWidth + 0f, measuredHeight / 2f, paint)
        rebuild()
        canvas.drawLine(0f, textRect.bottom, measuredWidth + 0f, textRect.bottom, paint)
        canvas.drawRect(textRect, paint)

        //
        val textWidth = paint.textWidth(text)
        canvas.drawText(text, measuredWidth / 2f - textWidth / 2, 100f, paint)

        //
        canvas.drawTextOnPath(text, textPath, 0f, -paint.descent(), paint)

        //
        canvas.drawPath(textPath, paint)
    }

    var dx = 0f
    var dy = 0f

    var offsetX = 0f

    fun rebuild() {
        val w = measuredWidth
        val h = measuredHeight / 2
        textPath.rewind()

        val textWidth = paint.textWidth(text)
        val textHeight = paint.textHeight()
        val textCenterX = w / 2f

        val c = curvature.absoluteValue / 90 //弯曲比例
        dx = textWidth / 2 / 2 * c
        dy = dx / 2 * c

        //offsetX = dx * c

        textRect.set(
            textCenterX - textWidth / 2 + dx - offsetX / 2,
            h - 1f - dx - dy,
            textCenterX + textWidth / 2 - dx + offsetX / 2,
            h + dx + dy
        )

        if (curvature >= 0) {
            textPath.addArc(textRect, -180f, 180f)
        } else {
            textPath.addArc(textRect, 0f, 180f)
        }
    }

}