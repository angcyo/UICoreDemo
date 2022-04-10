package com.angcyo.uicore.demo.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withMatrix
import com.angcyo.canvas.utils.createPaint
import com.angcyo.canvas.utils.mapPoint
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.setBounds
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/08
 */
class DrawImageView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    var drawable: Drawable? = null

    var drawableMatrix: Matrix = Matrix()
        set(value) {
            field = value
            invalidate()
        }

    val paint = createPaint(Color.BLUE).apply {
        strokeWidth = 2 * dp
    }

    val circlePaint = createPaint(Color.RED, Paint.Style.FILL)
    val circleSize = 2 * dp
    val circleRect: RectF = RectF()

    val rect: RectF = RectF()
    val drawRect: RectF = RectF()

    init {
        drawable = resources.getDrawable(R.drawable.face, getContext().theme)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        circleRect.set(rect)
        circleRect.inset(-circleSize * 2, -circleSize * 2)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withMatrix(drawableMatrix) {
            drawable?.setBounds(measuredWidth, measuredHeight)
            drawable?.draw(canvas)

            canvas.drawRect(0f, 0f, measuredWidth.toFloat(), measuredHeight.toFloat(), paint)
        }

        drawableMatrix.mapRect(drawRect, rect)
        canvas.drawRect(drawRect, paint)

        //circleMatrix.set(drawableMatrix)

        val center = PointF(circleRect.centerX(), circleRect.centerY())
        val _center = drawableMatrix.mapPoint(center)

        val lt = PointF(circleRect.left, circleRect.top)
        val _lt = drawableMatrix.mapPoint(lt, PointF())

        val rt = PointF(circleRect.right, circleRect.top)
        val _rt = drawableMatrix.mapPoint(rt, PointF())

        val rb = PointF(circleRect.right, circleRect.bottom)
        val _rb = drawableMatrix.mapPoint(rb, PointF())

        val lb = PointF(circleRect.left, circleRect.bottom)
        val _lb = drawableMatrix.mapPoint(lb, PointF())

        canvas.drawCircle(_center.x, _center.y, circleSize, circlePaint)
        canvas.drawCircle(_lt.x, _lt.y, circleSize, circlePaint)
        canvas.drawCircle(_lb.x, _lb.y, circleSize, circlePaint)
        canvas.drawCircle(_rt.x, _rt.y, circleSize, circlePaint)
        canvas.drawCircle(_rb.x, _rb.y, circleSize, circlePaint)

        //val c1 = CanvasTouchHandler.spacing()
    }

}