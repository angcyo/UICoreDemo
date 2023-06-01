package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.angcyo.library.ex.createPaint
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.getPointOnCircle
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

    var text = "angcyo"

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
            //圆的周长
            val circleLength = (textWidth * 1 / fraction).absoluteValue
            //圆的半径
            val radius = circleLength / 2 / Math.PI
            //外圆的半径
            val outRadius = radius + textHeight
            val curveTextInfo = CurveTextInfo(curvature, radius.toFloat(), outRadius.toFloat())
            val cx = measuredWidth / 2f
            val cy = measuredHeight / 2f
            /*canvas.withTranslation(
                cx - curveTextInfo.curveTextWidth / 2,
                cy - curveTextInfo.curveTextHeight / 2 + curveTextInfo.curveTextHeight / 2 - textHeight
            ) {
                val drawPath = curveTextInfo.getTextDrawPath()
                canvas.drawTextOnPath(text, drawPath, 0f, -paint.descent(), paint)
                paint.color = Color.RED
                canvas.drawPath(curveTextInfo.getTextDrawInnerCirclePath(), paint)
                canvas.drawPath(curveTextInfo.getTextDrawOuterCirclePath(), paint)
                paint.color = Color.BLUE
                canvas.drawRect(
                    0f,
                    0f,
                    curveTextInfo.curveTextWidth,
                    curveTextInfo.curveTextHeight,
                    paint
                )
            }*/

            val testPath = Path()
            testPath.addCircle(cx, cy, radius.toFloat(), Path.Direction.CCW)
            canvas.drawTextOnPath(text, testPath, 0f, 0f, paint)


            /*val path = Path()
            *//*val innerRect = RectF(
                cx - curveTextInfo.innerWidth / 2,
                cy - curveTextInfo.innerHeight / 2,
                cx + curveTextInfo.innerWidth / 2,
                cy + curveTextInfo.innerHeight / 2,
            )
            val outerRect = RectF(
                cx - curveTextInfo.outerWidth / 2,
                cy - curveTextInfo.outerHeight / 2,
                cx + curveTextInfo.outerWidth / 2,
                cy + curveTextInfo.outerHeight / 2,
            )*//*
            val innerRect = RectF(
                (cx - radius).toFloat(), (cy - radius).toFloat(),
                (cx + radius).toFloat(), (cy + radius).toFloat()
            )
            val outerRect = RectF(
                (cx - outRadius).toFloat(), (cy - outRadius).toFloat(),
                (cx + outRadius).toFloat(), (cy + outRadius).toFloat(),
            )
            val baselineAngle = if (curvature > 0) 270f else 90f
            canvas.withTranslation(0f, radius.toFloat()) {
                if (curvature > 0) {
                    path.addArc(innerRect, baselineAngle - curvature.absoluteValue / 2, curvature)
                } else {
                    path.addArc(outerRect, baselineAngle + curvature.absoluteValue / 2, curvature)
                }
                canvas.drawPath(path, paint)
                canvas.drawTextOnPath(text, path, 0f, 0f, paint)
                canvas.drawRect(innerRect, paint)
                canvas.drawRect(outerRect, paint)
                canvas.drawCircle(innerRect.centerX(), innerRect.centerY(), radius.toFloat(), paint)
                canvas.drawCircle(
                    outerRect.centerX(),
                    outerRect.centerY(),
                    outRadius.toFloat(),
                    paint
                )
            }
            val bounds = RectF()
            bounds.left = cx - curveTextInfo.outerWidth / 2
            bounds.right = cx + curveTextInfo.outerWidth / 2
            bounds.top = cy - textHeight + paint.descent()
            bounds.bottom = bounds.top + curveTextInfo.outerHeight
            paint.color = Color.BLUE
            canvas.drawRect(bounds, paint)*/
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

    /**曲线文本信息*/
    data class CurveTextInfo(
        /**曲度[-360~360], 角度*/
        var curvature: Float = 0f,
        /**内圈半径*/
        var innerRadius: Float = 0f,
        /**外圈半径*/
        var outerRadius: Float = 0f,

        /**曲线文本的宽高*/
        var curveTextWidth: Float = 0f,
        var curveTextHeight: Float = 0f,
    ) {

        /**基准角度*/
        val baseAngleDegrees: Float
            get() = if (curvature > 0) 270f else 90f

        /**左边角度*/
        val leftAngleDegrees: Float
            get() = baseAngleDegrees - curvature / 2

        /**右边角度*/
        val rightAngleDegrees: Float
            get() = baseAngleDegrees + curvature / 2

        init {
            measureCurveTextSize()
        }

        /**测量曲线文本的宽高大小*/
        fun measureCurveTextSize() {
            if (curvature.absoluteValue == 360f) {
                curveTextWidth = outerRadius * 2
                curveTextHeight = outerRadius * 2
            } else if (curvature.absoluteValue >= 180) {
                curveTextWidth = outerRadius * 2

                val top = getPointOnCircle(0f, 0f, outerRadius, baseAngleDegrees)
                val bottom = getPointOnCircle(0f, 0f, outerRadius, leftAngleDegrees)
                curveTextHeight = (top.y - bottom.y).absoluteValue
            } else {
                val left = getPointOnCircle(0f, 0f, outerRadius, leftAngleDegrees)
                val right = getPointOnCircle(0f, 0f, outerRadius, rightAngleDegrees)
                curveTextWidth = (left.x - right.x).absoluteValue

                val top = getPointOnCircle(0f, 0f, outerRadius, baseAngleDegrees)
                val bottom = getPointOnCircle(0f, 0f, innerRadius, leftAngleDegrees)
                curveTextHeight = (top.y - bottom.y).absoluteValue
            }
        }

        /**获取文本绘制的路径*/
        fun getTextDrawPath(): Path {
            val path = Path()
            val textHeight = outerRadius - innerRadius
            val cx = curveTextWidth / 2
            val cy = if (curvature > 0) {
                textHeight + innerRadius
            } else {
                curveTextHeight - textHeight - innerRadius
            }
            val oval = if (curvature > 0) {
                RectF(
                    cx - innerRadius,
                    cy - innerRadius,
                    cx + innerRadius,
                    cy + innerRadius,
                )
            } else {
                RectF(
                    cx - outerRadius,
                    cy - outerRadius,
                    cx + outerRadius,
                    cy + outerRadius,
                )
                /*RectF(
                    cx - innerRadius,
                    cy - innerRadius,
                    cx + innerRadius,
                    cy + innerRadius,
                )*/
            }
            path.addArc(oval, leftAngleDegrees, curvature)
            //path.addOval(oval, Path.Direction.CW)
            return path
        }

        /**提示圆圈的path*/
        fun getTextDrawInnerCirclePath(): Path {
            val path = Path()
            val textHeight = outerRadius - innerRadius
            val cx = curveTextWidth / 2
            val cy = if (curvature > 0) {
                textHeight + innerRadius
            } else {
                curveTextHeight - textHeight - innerRadius
            }
            path.addCircle(cx, cy, innerRadius, Path.Direction.CW)
            return path
        }

        fun getTextDrawOuterCirclePath(): Path {
            val path = Path()
            val textHeight = outerRadius - innerRadius
            val cx = curveTextWidth / 2
            val cy = if (curvature > 0) {
                textHeight + innerRadius
            } else {
                curveTextHeight - textHeight - innerRadius
            }
            path.addCircle(cx, cy, outerRadius, Path.Direction.CW)
            return path
        }
    }

}