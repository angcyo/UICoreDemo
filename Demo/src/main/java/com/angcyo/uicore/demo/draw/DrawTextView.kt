package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.angcyo.canvas.utils.createPaint
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.textHeight
import com.angcyo.library.ex.textWidth

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

    var curvature = 0f//曲度[0~90]

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

    fun rebuild() {
        val w = measuredWidth
        val h = measuredHeight / 2
        textPath.rewind()

        val textWidth = paint.textWidth(text)
        val textHeight = paint.textHeight()
        val textCenterX = w / 2f

        val c = curvature / 90
        dx = textWidth / 2 / 2 * c
        dy = dx / 2 * c

        textRect.set(
            textCenterX - textWidth / 2 + dx,
            h - 1f - dx - dy,
            textCenterX + textWidth / 2 - dx,
            h + dx + dy
        )

        textPath.addArc(textRect, -180f, 180f)
    }

}