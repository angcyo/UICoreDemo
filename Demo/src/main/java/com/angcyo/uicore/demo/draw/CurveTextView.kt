package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withTranslation
import com.angcyo.library.ex.createPaint
import com.angcyo.library.ex.createTextPaint
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.textHeight
import com.angcyo.library.ex.textWidth
import com.angcyo.library.ex.toStr
import kotlin.math.max

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/08/17
 */
class CurveTextView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    val textPaint = createTextPaint().apply {
        textSize = 16 * dp
    }
    val borderPaint = createPaint().apply {
        strokeWidth = 1 * dp
        color = Color.GREEN
    }
    val text = "测试文本\nangcyo"

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.apply {
            when (actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    if (textPaint.style == Paint.Style.STROKE) {
                        textPaint.style = Paint.Style.FILL
                    } else {
                        textPaint.style = Paint.Style.STROKE
                    }
                    invalidate()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val tx = measuredWidth / 2f
        val ty = measuredHeight / 2f
        canvas.withTranslation(tx, ty) {
            drawLine(-measuredWidth + 0f, 0f, measuredWidth + 0f, 0f, textPaint)
            drawLine(0f, -measuredHeight + 0f, 0f, measuredHeight + 0f, textPaint)

            //drawNormalText(this)
            drawCurveText(this)
            measureCurveText(this)
        }
    }

    private fun drawNormalText(canvas: Canvas) {
        val list = text.lines()
        var top = 0f
        list.forEach {
            top -= textPaint.textHeight()
        }

        list.forEach { line ->
            var left = 0f
            line.forEach { char ->
                val text = char.toStr()
                val textWidth = textPaint.textWidth(text)
                canvas.drawText(
                    text,
                    left,
                    top + textPaint.textHeight() - textPaint.descent(),
                    textPaint
                )

                val textRect = RectF()
                textRect.set(left, top, left + textWidth, top + textPaint.textHeight())
                canvas.drawRect(textRect, borderPaint)

                left += textWidth
            }
            top += textPaint.textHeight()
        }
    }

    var curve = 360f
    val curveStart: Float
        get() = 270f - curve / 2

    private fun drawCurveText(canvas: Canvas) {
        val list = text.lines()
        var top = 0f
        var textMaxWidth = 0f
        var textMaxHeight = 0f
        list.forEach { line ->
            textMaxWidth = max(textMaxWidth, textPaint.textWidth(line))
            textMaxHeight = max(textMaxHeight, textPaint.textHeight())
            top -= textPaint.textHeight()
        }

        val curveCenterX = textMaxWidth / 2
        val curveCenterY = curveCenterX
        val curveRadius = curveCenterX

        list.forEach { line ->
            var left = 0f
            line.forEachIndexed { index, char ->
                val degrees = curveStart + (index * curve / (line.length - 1))

                val matrix = Matrix()
                matrix.setRotate(degrees, curveCenterX, curveCenterY)
                val textRect = RectF()

                canvas.withMatrix(matrix) {
                    val text = char.toStr()
                    val textWidth = textPaint.textWidth(text)

                    val textDrawX = curveCenterX + curveRadius + left
                    val textDrawY = curveCenterY + top + textPaint.textHeight()
                    canvas.drawText(
                        text,
                        textDrawX,
                        textDrawY - textPaint.descent(),
                        textPaint
                    )

                    textRect.set(
                        textDrawX,
                        textDrawY - textPaint.textHeight(),
                        textDrawX + textWidth,
                        textDrawY
                    )
                    borderPaint.color = Color.GREEN
                    canvas.drawRect(textRect, borderPaint)

                    left += textWidth
                }

                matrix.mapRect(textRect)
                borderPaint.color = Color.RED
                canvas.drawRect(textRect, borderPaint)
            }
            top += textPaint.textHeight()
        }
    }

    private fun measureCurveText(canvas: Canvas) {
        val list = text.lines()
        var top = 0f
        var textMaxWidth = 0f
        var textMaxHeight = 0f
        list.forEach { line ->
            textMaxWidth = max(textMaxWidth, textPaint.textWidth(line))
            textMaxHeight = max(textMaxHeight, textPaint.textHeight())
            top -= textPaint.textHeight()
        }

        val curveCenterX = textMaxWidth / 2
        val curveCenterY = curveCenterX
        val curveRadius = curveCenterX

        val rectList = mutableListOf<List<RectF>>()

        list.forEach { line ->
            val lineRectList = mutableListOf<RectF>()
            var left = 0f
            line.forEachIndexed { index, char ->
                val degrees = curveStart + (index * curve / (line.length - 1))

                val matrix = Matrix()
                matrix.setRotate(degrees, curveCenterX, curveCenterY)
                val textRect = RectF()

                val text = char.toStr()
                val textWidth = textPaint.textWidth(text)

                val textDrawX = curveCenterX + curveRadius + left
                val textDrawY = curveCenterY + top + textPaint.textHeight()

                textRect.set(
                    textDrawX,
                    textDrawY - textPaint.textHeight(),
                    textDrawX + textWidth,
                    textDrawY
                )

                left += textWidth

                matrix.mapRect(textRect)
                lineRectList.add(textRect)
            }
            top += textPaint.textHeight()

            rectList.add(lineRectList)
        }

        var minLeft = Float.MAX_VALUE
        var minTop = Float.MAX_VALUE
        var maxRight = Float.MIN_VALUE
        var maxBottom = Float.MIN_VALUE

        rectList.forEach { lineRectList ->
            lineRectList.forEach { rect ->
                minLeft = minOf(minLeft, rect.left)
                minTop = minOf(minTop, rect.top)
                maxRight = maxOf(maxRight, rect.right)
                maxBottom = maxOf(maxBottom, rect.bottom)
            }
        }

        val textBoundsRect = RectF()
        textBoundsRect.set(minLeft, minTop, maxRight, maxBottom)
        borderPaint.color = Color.MAGENTA
        canvas.drawRect(textBoundsRect, borderPaint)
    }

}