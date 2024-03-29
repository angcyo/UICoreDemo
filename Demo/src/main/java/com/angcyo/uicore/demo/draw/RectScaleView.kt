package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withRotation
import com.angcyo.library.L
import com.angcyo.library.component.pool.acquireTempMatrix
import com.angcyo.library.component.pool.acquireTempPointF
import com.angcyo.library.component.pool.acquireTempRectF
import com.angcyo.library.component.pool.release
import com.angcyo.library.ex.computePathBounds
import com.angcyo.library.ex.density
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.eachPath
import com.angcyo.library.ex.flipHorizontal
import com.angcyo.library.ex.flipVertical
import com.angcyo.library.ex.interceptParentTouchEvent
import com.angcyo.library.ex.mapPoint
import com.angcyo.library.ex.paint
import com.angcyo.library.ex.rotate
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
        set(value) {
            field = value
            anchorPoint = null
        }

    /**强制指定锚点*/
    var anchorPoint: PointF? = null

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

    val _tempRect = RectF()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.GRAY)

        //origin 原始矩形
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        drawRect(canvas, originRect)//绘制未旋转和旋转后的2个矩形

        //draw 缩放后
        paint.color = Color.RED
        _tempRect.set(drawRect)
        _tempRect.sort()
        drawRect(canvas, _tempRect)

        //anchor
        if (anchorPoint != null) {
            _tempRect.set(anchorPoint!!.x, anchorPoint!!.y, touchX, touchY)
            paint.color = Color.GREEN
            drawRect(canvas, _tempRect, false)
        }

        paint.color = Color.WHITE
        paint.style = Paint.Style.FILL
        val text1 =
            "w:${originRect.width()}->${drawRect.width()} h:${originRect.height()}->${drawRect.height()}"
        canvas.drawText(text1, 0f, paint.textHeight(), paint)
        val text2 = "$drawRect"
        canvas.drawText(text2, 0f, paint.textHeight() * 2, paint)
        val text3 = "flipH:$flipHorizontal flipV:$flipVertical $rectPosition"
        canvas.drawText(text3, 0f, paint.textHeight() * 3, paint)
        val text4 =
            "scaleX:${rectScaleGestureHandler.currentScaleX} scaleY:${rectScaleGestureHandler.currentScaleY}"
        canvas.drawText(text4, 0f, paint.textHeight() * 4, paint)

        //testLinePath(canvas)
        testLine(canvas)

        testGroup(canvas)
    }

    var touchX = 0f
    var touchY = 0f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        interceptParentTouchEvent(event)
        touchX = event.x
        touchY = event.y
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                //
                rectScaleGestureHandler.keepScaleRatio = keepRadio
                rectScaleGestureHandler.keepScaleRatioOnFrame = false
                if (anchorPoint == null) {
                    rectScaleGestureHandler.initialize(originRect, rotate, rectPosition)
                } else {
                    rectScaleGestureHandler.initializeAnchor(
                        originRect,
                        rotate,
                        anchorPoint!!.x,
                        anchorPoint!!.y
                    )
                }
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
    fun drawRect(canvas: Canvas, rect: RectF, drawRotate: Boolean = true) {
        _path.rewind()
        _path.addRect(rect, Path.Direction.CW)
        //用虚线绘制
        paint.pathEffect = dasEffect
        canvas.drawPath(_path, paint)
        paint.pathEffect = null

        //旋转后用实线绘制
        if (drawRotate) {
            canvas.withRotation(rotate, rect.centerX(), rect.centerY()) {
                canvas.drawPath(_path, paint)
            }
        }

        //中心线
        drawCenter(canvas, rect)
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

    fun testLinePath(canvas: Canvas) {
        val line1 = Path()
        val y = measuredHeight / 3f
        line1.moveTo(10f, y)
        line1.lineTo(200f, y)
        paint.style = Paint.Style.STROKE
        canvas.drawPath(line1, paint)
        val pathBounds = line1.computePathBounds()
        L.i(pathBounds)
        line1.eachPath { index, ratio, contourIndex, posArray, _ ->
            L.i(posArray)
        }
        val matrix = Matrix()
        //matrix.setScale(1.5f, 1.5f, pathBounds.centerX(), pathBounds.centerY())
        matrix.setScale(1.5f, 1.5f)
        matrix.postRotate(rotate, pathBounds.centerX(), pathBounds.centerY())
        matrix.postTranslate(0f, 10f)
        line1.transform(matrix)
        paint.color = Color.BLUE
        canvas.drawPath(line1, paint)
        line1.eachPath { index, ratio, contourIndex, posArray, _ ->
            L.w(posArray)
        }

        paint.color = Color.WHITE
        val line2 = Path()
        val y2 = measuredHeight * 2 / 3f
        line2.moveTo(10f, y2)
        line2.lineTo(200f, y2)
        paint.style = Paint.Style.FILL
        canvas.drawPath(line2, paint)
        L.i(line2.computePathBounds())

        val line3 = Path()
        paint.color = Color.YELLOW
        val y3 = measuredHeight * 3 / 4f
        line3.moveTo(10f, y3)
        line3.lineTo(200f, y3)
        paint.style = Paint.Style.FILL_AND_STROKE
        canvas.drawPath(line3, paint)
    }

    fun testScale() {
        L.i("原", originRect)
        originRect.flipHorizontal(true).flipVertical(true)
        L.i("翻转后", originRect)

        //val matrix = Matrix()
        //matrix.setRotate(15f, originRect.centerX(), originRect.centerY()) //旋转后,翻转信息丢失
        //matrix.setScale(1.5f, 1.2f, originRect.centerX(), originRect.centerY()) //缩放后,翻转信息丢失
        //matrix.mapRect(originRect)
        //L.i("Matrix后", originRect)

        RectScaleGestureHandler.rectScaleTo(
            originRect,
            drawRect,
            1.5f,
            1.2f,
            rotate,
            originRect.left,
            originRect.top
        )
        L.i("缩放后", drawRect)
        RectScaleGestureHandler.rectScaleTo(
            originRect,
            originRect,
            1.5f,
            1.2f,
            rotate,
            originRect.left,
            originRect.top
        )
        L.i("缩放后2", originRect)
        postInvalidate()
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
        /*
        val matrix = Matrix()
        matrix.setScale(1.5f, 1.5f, anchorPoint.x, anchorPoint.y)
        matrix.mapRect(drawRect, originRect)*/

        RectScaleGestureHandler.rectUpdateTo(
            originRect,
            drawRect,
            originRect.width(),
            1f,
            rotate,
            anchorPoint.x,
            anchorPoint.y
        )

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
        scaleHandler.initializeAnchor(drawRect, rotate, 10f, 10f)
        scaleHandler.rectScaleBy(0.9f, 0.9f, true)
    }

    fun testScaleFrame() {
        val rotateRect = acquireTempRectF()
        val rotateScaleRect = acquireTempRectF()
        val originScaleRect = acquireTempRectF()
        val originScaleRotateRect = acquireTempRectF()

        val matrix = acquireTempMatrix()
        matrix.setRotate(rotate, originRect.centerX(), originRect.centerY())
        matrix.mapRect(rotateRect, originRect)

        //现在缩放旋转后的矩形
        matrix.reset()
        matrix.setScale(0.5f, 0.5f, originRect.centerX(), originRect.centerY())
        matrix.mapRect(rotateScaleRect, rotateRect)
        matrix.mapRect(originScaleRect, originRect)

        matrix.reset()
        matrix.setRotate(rotate, originRect.centerX(), originRect.centerY())
        matrix.mapRect(originScaleRotateRect, originScaleRect)

        //最后判断
        if (originScaleRotateRect == rotateScaleRect) {

        }

        rotateScaleRect.release()
        rotateRect.release()
        matrix.release()
    }

    fun testHeight() {
        val anchor = acquireTempPointF()
        anchor.set(originRect.left, originRect.top)
        anchor.rotate(rotate, originRect.centerX(), originRect.centerY())

        RectScaleGestureHandler.rectUpdateTo(
            drawRect,
            drawRect,
            drawRect.width(),
            1f,
            rotate,
            anchor.x,
            anchor.y
        )
        anchor.release()
        postInvalidate()
    }

    fun testLine(canvas: Canvas) {
        val paint = Paint().apply {
            color = Color.GREEN
            strokeWidth = 1f
            style = Paint.Style.FILL_AND_STROKE
        }
        val path = Path()
        path.moveTo(10f, 300f)
        path.lineTo(100f, 300f)
        canvas.drawPath(path, paint)
        val bounds1 = path.computePathBounds()

        val matrix = acquireTempMatrix()
        //matrix.setTranslate(-bounds1.left, -bounds1.top)
        matrix.setScale(2f, 2f, bounds1.left, bounds1.top)
        val newPath = Path()
        path.transform(matrix, newPath)
        canvas.drawPath(newPath, paint)
        val bounds2 = newPath.computePathBounds()

        matrix.release()
    }

    //--group

    val groupAnchorPaint = paint().apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
        strokeWidth = 1 * dp
    }

    val groupAnchorPoint = PointF()

    fun testGroup(canvas: Canvas) {
        groupAnchorPoint.x = measuredWidth / 4f
        groupAnchorPoint.y = measuredHeight / 4f
        /*canvas.drawLine(
            0f,
            groupAnchorPoint.y,
            measuredWidth.toFloat(),
            groupAnchorPoint.y,
            groupAnchorPaint
        )
        canvas.drawLine(
            groupAnchorPoint.x,
            0f,
            groupAnchorPoint.x,
            measuredHeight.toFloat(),
            groupAnchorPaint
        )*/
        canvas.drawCircle(groupAnchorPoint.x, groupAnchorPoint.y, dp, groupAnchorPaint)
    }

    var groupScaleX = 1f
    var groupScaleY = 1f

    /**测试旋转矩形按照指定锚点, 缩放到指定比例*/
    fun adjustTest() {
        groupScaleX -= 0.05f
        groupScaleY -= 0.1f
        RectScaleGestureHandler.rectUpdateTo(
            originRect,
            drawRect,
            originRect.width() * groupScaleX,
            originRect.height() * groupScaleY,
            rotate,
            groupAnchorPoint.x,
            groupAnchorPoint.y
        )
        postInvalidate()
    }
}