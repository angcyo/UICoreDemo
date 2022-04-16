package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.angcyo.canvas.utils.createPaint

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/04/16
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class BorderView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    val paint = createPaint()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
    }

}