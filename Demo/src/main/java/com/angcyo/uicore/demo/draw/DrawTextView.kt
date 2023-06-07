package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withTranslation
import com.angcyo.canvas.render.element.CurveTextDraw
import com.angcyo.library.ex.createPaint
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.textBounds
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

    companion object {
        const val DEF = "j angcyoJ"
    }

    var text = DEF

    val paint = createPaint().apply {
        strokeWidth = 1 * dp
        textSize = 24 * dp
    }

    val textPath = Path()

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    var curvature = 0f//曲度[-360~360]

    val textRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE

        //
        canvas.drawLine(measuredWidth / 2f, 0f, measuredWidth / 2f, measuredHeight + 0f, paint)
        canvas.drawLine(0f, measuredHeight / 2f, measuredWidth + 0f, measuredHeight / 2f, paint)
        /*rebuild()
        canvas.drawLine(0f, textRect.bottom, measuredWidth + 0f, textRect.bottom, paint)
        canvas.drawRect(textRect, paint)

        //
        val textWidth = paint.textWidth(text)
        canvas.drawText(text, measuredWidth / 2f - textWidth / 2, 100f, paint)*/

        /*//
        canvas.drawTextOnPath(text, textPath, 0f, -paint.descent(), paint)

        //
        canvas.drawPath(textPath, paint)*/

        testTextOnPath(canvas)
        test2(canvas)
    }

    fun testTextOnPath(canvas: Canvas) {
        //曲度占用整个圆度数的比例
        val fraction = curvature / 360f
        if (fraction == 0f) {
        } else {
            //文本宽度
            val textWidth = paint.textWidth(text)
            //文本高度
            //val textHeight = paint.textHeight()
            val textHeight = paint.textBounds(text).height().toFloat()
            val curveTextInfo = CurveTextDraw.create(text, curvature, textWidth, textHeight, paint)
            val cx = measuredWidth / 2f
            val cy = measuredHeight / 2f
            val tx = cx - curveTextInfo.curveTextWidth / 2
            val ty = if (curvature > 0) {
                cy - curveTextInfo.curveTextHeight / 2 + curveTextInfo.curveTextHeight / 2 - textHeight
            } else {
                cy - curveTextInfo.curveTextHeight
            }
            canvas.withTranslation(tx, ty) {
                paint.style = Paint.Style.FILL
                //val drawPath = curveTextInfo.getTextDrawPath()
                //canvas.drawTextOnPath(text, drawPath, 0f, -paint.descent(), paint)
                //
                paint.color = Color.RED
                paint.style = Paint.Style.STROKE
                canvas.drawPath(curveTextInfo.getTextDrawInnerCirclePath(), paint)
                canvas.drawPath(curveTextInfo.getTextDrawOuterCirclePath(), paint)
                //
                paint.color = Color.BLUE
                paint.style = Paint.Style.STROKE
                canvas.drawRect(
                    0f,
                    0f,
                    curveTextInfo.curveTextWidth,
                    curveTextInfo.curveTextHeight,
                    paint
                )
                //
                paint.color = Color.MAGENTA
                paint.style = Paint.Style.FILL
                curveTextInfo.draw(canvas, paint)

                canvas.drawLine(
                    -tx,
                    curveTextInfo.curveCy,
                    -tx + measuredWidth.toFloat(),
                    curveTextInfo.curveCy,
                    paint
                )
                canvas.drawLine(
                    curveTextInfo.curveCx,
                    -ty,
                    curveTextInfo.curveCx,
                    -ty + measuredHeight.toFloat(),
                    paint
                )
            }
        }
    }

    fun test2(canvas: Canvas) {
        /*val paint = Paint()
        paint.textSize = 50f
        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL

        val text = "Hello World"

        canvas.withTranslation(100f, 100f) {
            val path = Path()
            path.moveTo(0f, 0f)
            path.quadTo(200f, -100f, 400f, 0f)

            canvas.drawTextOnPath(text, path, 0f, 0f, paint)
        }*/
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