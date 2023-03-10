package com.angcyo.uicore.demo.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import com.angcyo.widget.base.createPaint
import com.angcyo.library.ex.*
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

    val invertMatrix = Matrix()

    val paint = createPaint(Color.BLUE).apply {
        strokeWidth = 2 * dp
    }

    //矩形4个角
    val circlePaint = createPaint(Color.RED, Paint.Style.FILL)
    val circleSize = 2 * dp
    val circleRect: RectF = RectF()

    val rect: RectF = RectF()
    val drawRect: RectF = RectF()

    val invertRect = RectF()

    //旋转之后的path, 用来测试touch命中
    val rotatePath: Path = Path()
    var isTouchInView: Boolean = false

    init {
        drawable = resources.getDrawable(R.drawable.face, getContext().theme)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        rect.set(0f, 0f, w.toFloat(), h.toFloat())
        circleRect.set(rect)
        circleRect.inset(-circleSize * 2, -circleSize * 2)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                disableParentInterceptTouchEvent()
                checkHit(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                checkHit(event.x, event.y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                disableParentInterceptTouchEvent(false)
                isTouchInView = false
                postInvalidateOnAnimation()
            }
        }
        return true
    }

    fun checkHit(x: Float, y: Float) {
        rotatePath.reset()
        rotatePath.addRect(
            0f,
            0f,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            Path.Direction.CW
        )
        rotatePath.transform(drawableMatrix)
        isTouchInView = rotatePath.contains(x.toInt(), y.toInt(), rect)
        //invalidate()
        postInvalidateOnAnimation()
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withMatrix(drawableMatrix) {
            drawable?.setBounds(measuredWidth, measuredHeight)
            drawable?.draw(canvas)

            if (isTouchInView) {
                paint.color = Color.RED
            } else {
                paint.color = Color.BLUE
            }

            paint.color = paint.color.alphaRatio(0.5f)
            canvas.drawRect(rect, paint)
        }

        drawableMatrix.mapRect(drawRect, rect)
        paint.color = Color.RED
        paint.color = paint.color.alphaRatio(0.5f)
        canvas.drawRect(drawRect, paint)

        //反转一下
        if (drawableMatrix.invert(invertMatrix)) {
            invertMatrix.mapRect(invertRect, drawRect)
            paint.color = Color.YELLOW
            paint.color = paint.color.alphaRatio(0.5f)
            canvas.drawRect(invertRect, paint)

            invertMatrix.postScale(drawableMatrix.getScaleX(), drawableMatrix.getScaleY())
            invertMatrix.mapRect(invertRect, drawRect)
            paint.color = Color.MAGENTA
            paint.color = paint.color.alphaRatio(0.5f)
            canvas.drawRect(invertRect, paint)
        }

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

        canvas.drawArc(
            0f,
            0f,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            350f,
            90f,
            true,
            paint
        )
        paint.color = Color.RED
        canvas.drawArc(
            0f,
            0f,
            measuredWidth.toFloat(),
            measuredHeight.toFloat(),
            350f,
            -90f,
            true,
            paint
        )
    }

}