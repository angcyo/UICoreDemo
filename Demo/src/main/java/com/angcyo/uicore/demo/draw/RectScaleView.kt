package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withRotation
import com.angcyo.library.L
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.interceptParentTouchEvent
import com.angcyo.library.ex.paint
import com.angcyo.library.ex.textHeight
import com.angcyo.library.gesture.RectScaleGestureHandler

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/16
 */
class RectScaleView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    val paint = paint().apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 1 * dp
    }

    var rotate: Float = 0f

    /**原始矩形*/
    var originRect: RectF = RectF()

    /**改变后的矩形*/
    var drawRect: RectF = RectF()

    var keepRadio = true

    var rectPosition: Int = RectScaleGestureHandler.RECT_RB

    /**矩形缩放处理*/
    val rectScaleGestureHandler = RectScaleGestureHandler().apply {
        onRectScaleChangeAction = { rect, end ->
            drawRect.set(rect)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val centerX = w / 2f
        val centerY = h / 2f

        val width = 45 * dp
        val height = 65 * dp

        originRect.set(centerX - width, centerY - height, centerX + width, centerY + height)
        drawRect.set(originRect)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        //origin
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        canvas.withRotation(rotate, originRect.centerX(), originRect.centerY()) {
            canvas.drawRect(originRect, paint)
        }
        drawCenter(canvas, originRect)

        //draw
        paint.color = Color.RED
        canvas.withRotation(rotate, drawRect.centerX(), drawRect.centerY()) {
            canvas.drawRect(drawRect, paint)
        }
        drawCenter(canvas, drawRect)

        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        val text1 =
            "w:${originRect.width()}->${drawRect.width()} h:${originRect.height()}->${drawRect.height()}"
        val text2 = "$drawRect"
        canvas.drawText(text1, 0f, paint.textHeight(), paint)
        canvas.drawText(text2, 0f, paint.textHeight() * 2, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        interceptParentTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                //
                rectScaleGestureHandler.initialize(originRect, rotate, rectPosition, keepRadio)
            }
        }
        //
        rectScaleGestureHandler.onTouchEvent(event.actionMasked, event.x, event.y)
        postInvalidate()
        return true
    }

    /**绘制中心十字线*/
    fun drawCenter(canvas: Canvas, rect: RectF) {
        canvas.drawLine(
            0f,
            rect.centerY(),
            measuredWidth.toFloat(),
            rect.centerY(),
            paint
        )
        canvas.drawLine(
            rect.centerX(),
            0f,
            rect.centerX(),
            measuredHeight.toFloat(),
            paint
        )
    }

    fun test() {
        val matrix = Matrix()
        matrix.setScale(-0.6f, 0.6f, drawRect.right, drawRect.top)
        matrix.mapRect(drawRect)
        L.i(drawRect)
        postInvalidate()
    }
}