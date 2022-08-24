package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withRotation
import com.angcyo.library.L
import com.angcyo.library.ex.*
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

    var flipHorizontal = false
    var flipVertical = false

    /**矩形缩放处理*/
    val rectScaleGestureHandler = RectScaleGestureHandler().apply {
        onRectScaleChangeAction = { rect, end ->
            drawRect.set(rect)
            flipHorizontal = isFlipHorizontal
            flipVertical = isFlipVertical
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
        canvas.drawColor(Color.GRAY)

        //origin
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        drawRect(canvas, originRect)

        //draw
        paint.color = Color.RED
        drawRect(canvas, drawRect)

        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        val text1 =
            "w:${originRect.width()}->${drawRect.width()} h:${originRect.height()}->${drawRect.height()}"
        val text2 = "$drawRect"
        val text3 = "flipH:$flipHorizontal flipV:$flipVertical $rectPosition"
        canvas.drawText(text1, 0f, paint.textHeight(), paint)
        canvas.drawText(text2, 0f, paint.textHeight() * 2, paint)
        canvas.drawText(text3, 0f, paint.textHeight() * 3, paint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        interceptParentTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                //
                rectScaleGestureHandler.keepScaleRatio = keepRadio
                rectScaleGestureHandler.keepScaleRatioOnFrame = false
                rectScaleGestureHandler.initialize(originRect, rotate, rectPosition)
            }
        }
        //
        rectScaleGestureHandler.onTouchEvent(event)
        postInvalidate()
        return true
    }


    val _path = Path()
    val dasEffect = DashPathEffect(floatArrayOf(4 * density, 5 * density), 0f)

    /**绘制矩形, 包括旋转后的矩形*/
    fun drawRect(canvas: Canvas, rect: RectF) {
        _path.rewind()
        _path.addRect(rect, Path.Direction.CW)
        //用虚线绘制
        paint.pathEffect = dasEffect
        canvas.drawPath(_path, paint)
        paint.pathEffect = null

        //旋转后用实线绘制
        canvas.withRotation(rotate, rect.centerX(), rect.centerY()) {
            canvas.drawPath(_path, paint)
        }

        //中心线
        drawCenter(canvas, originRect)
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

    fun test2() {
        val left = originRect.left
        val top = originRect.top
        val anchorPoint = PointF(left, top)
        val matrix = Matrix()
        matrix.setScale(1.5f, 1.5f, anchorPoint.x, anchorPoint.y)
        matrix.mapRect(drawRect, originRect)

        postInvalidate()
    }

    fun test3() {
        val left = originRect.left
        val top = originRect.top
        val anchorRotatePoint = PointF(left, top)
        val matrix = Matrix()
        matrix.setRotate(rotate, originRect.centerX(), originRect.centerY())
        matrix.mapPoint(anchorRotatePoint, anchorRotatePoint)

        matrix.reset()
        //等比缩放时, 锚点不会偏移. 不等比缩放时, 锚点就会移动, 此时需要额外处理
        matrix.setScale(1.2f, 1.5f, anchorRotatePoint.x, anchorRotatePoint.y)
        matrix.mapRect(drawRect, originRect)

        //之后锚点的位置
        val anchorRotatePoint2 = PointF(left, top)
        matrix.reset()
        matrix.setRotate(rotate, drawRect.centerX(), drawRect.centerY())
        matrix.mapPoint(anchorRotatePoint2, anchorRotatePoint2)

        //偏移到原始锚点的位置
        matrix.reset()
        matrix.setTranslate(
            anchorRotatePoint.x - anchorRotatePoint2.x,
            anchorRotatePoint.y - anchorRotatePoint2.y
        )
        matrix.mapRect(drawRect)

        postInvalidate()
    }

    fun test4() {
        val scaleHandler = RectScaleGestureHandler()
        scaleHandler.onRectScaleChangeAction = { rect, end ->
            drawRect.set(rect)
            postInvalidate()
        }
        scaleHandler.initialize(drawRect, rotate, 10f, 10f)
        scaleHandler.startScaleBy(0.9f, 0.9f, true)
    }

}