package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withRotation
import androidx.core.graphics.withTranslation
import com.angcyo.library.ex.createPaint
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.ensure
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
            //圆的周长
            val circleLength = (textWidth * 1 / fraction).absoluteValue
            //圆的半径
            val radius = circleLength / 2 / Math.PI
            //外圆的半径
            val outRadius = radius + textHeight
            val curveTextInfo =
                CurveTextInfo(curvature, textWidth, radius.toFloat(), outRadius.toFloat())
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
                val drawPath = curveTextInfo.getTextDrawPath()
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
                curveTextInfo.draw(canvas, paint, text)

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

    /**曲线文本信息*/
    data class CurveTextInfo(
        /**曲度[-360~360], 角度*/
        var curvature: Float = 0f,
        /**文本默认的原始宽度*/
        var textWidth: Float = 0f,
        /**内圈半径*/
        var innerRadius: Float = 0f,
        /**外圈半径*/
        var outerRadius: Float = 0f,

        //---计算结果↓---

        /**曲线绘制中心坐标*/
        var curveCx: Float = 0f,
        var curveCy: Float = 0f,
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

        /**文本的高度*/
        val textHeight: Float
            get() = outerRadius - innerRadius

        init {
            measureCurveText()
        }

        /**测量曲线文本的宽高大小*/
        fun measureCurveText() {
            //计算宽高
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

            //计算相对于0,0位置应该绘制的中心位置
            val textHeight = outerRadius - innerRadius
            val cx = curveTextWidth / 2
            val cy = if (curvature > 0) {
                textHeight + innerRadius
            } else {
                curveTextHeight - textHeight - innerRadius
            }
            curveCx = cx
            curveCy = cy
        }

        /**获取文本绘制的路径
         * 这种方式在正向曲线绘制时没有问题,
         * 但是反向绘制时, 大圈内绘制文本无法闭合
         * 小圈外绘制文本会叠加
         * 解决方案: 一个字符一个字符绘制
         * [draw]*/
        fun getTextDrawPath(): Path {
            val path = Path()
            val cx = curveCx
            val cy = curveCy
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
            val cx = curveCx
            val cy = curveCy
            path.addCircle(cx, cy, innerRadius, Path.Direction.CW)
            return path
        }

        fun getTextDrawOuterCirclePath(): Path {
            val path = Path()
            val cx = curveCx
            val cy = curveCy
            path.addCircle(cx, cy, outerRadius, Path.Direction.CW)
            return path
        }

        /**绘制曲线文本,按照角度变化, 一个一个字符绘制 */
        fun draw(canvas: Canvas, paint: Paint, text: String) {
            //开始绘制的角度
            val startAngle = leftAngleDegrees
            //每个像素对应的角度
            val pixelAngle = (curvature.absoluteValue / textWidth).ensure(0f)

            var textSumWidth = 0f
            for (char in text) {
                val charStr = char.toString()
                val charWidth = paint.textWidth(charStr)
                textSumWidth += charWidth
            }
            //间隙角度
            val gapAngle: Float =
                (curvature.absoluteValue - pixelAngle * textSumWidth) / (text.length - 1)

            //自旋角度
            val charRotate = if (curvature > 0) 90f else -90f
            var charStartAngle = startAngle
            for (char in text) {
                val charStr = char.toString()
                val charWidth = paint.textWidth(charStr)
                val charHeight = textHeight
                /*val charMinSize = min(charWidth, charHeight)
                val charMaxSize = max(charWidth, charHeight)*/
                val textRotateCenterX = curveCx + innerRadius + charHeight / 2
                val textRotateCenterY = curveCy /*+ charWidth / 2*/
                val x = textRotateCenterX - charWidth / 2
                val y = textRotateCenterY + charHeight / 2 - paint.descent()

                val charAngle = pixelAngle * charWidth
                val rotate =
                    if (curvature > 0) charStartAngle + charAngle / 2 else charStartAngle - charAngle / 2
                canvas.withRotation(rotate, curveCx, curveCy) {
                    canvas.withRotation(charRotate, textRotateCenterX, textRotateCenterY) {
                        //文本
                        canvas.drawText(charStr, x, y, paint)
                    }
                }
                /*if (charStartAngle == startAngle) {
                    canvas.drawText(charStr, x, y, paint)
                    paint.color = Color.MAGENTA
                    canvas.withRotation(charRotate, textRotateCenterX, textRotateCenterY) {
                        canvas.drawText(charStr, x, y, paint)
                    }
                }*/
                if (curvature > 0) {
                    charStartAngle += gapAngle + charAngle
                } else {
                    charStartAngle -= gapAngle + charAngle
                }
            }
        }
    }

}