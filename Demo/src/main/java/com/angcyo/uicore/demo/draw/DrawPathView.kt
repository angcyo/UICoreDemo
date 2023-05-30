package com.angcyo.uicore.demo.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withTranslation
import com.angcyo.library.component.pool.acquireTempPointF
import com.angcyo.library.ex.*
import com.angcyo.vector.VectorHelper
import com.angcyo.vector.toPath

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/15
 */
class DrawPathView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    val pathList = mutableListOf<Path>()
    val paint = createPaint().apply {
        strokeWidth = 1 * dp
    }
    var polygonPath: Path? = null

    val drawRect = RectF()
    val drawRotateRect = RectF()
    val _touchPoint = PointF()
    val _movePoint = PointF()

    var touchWidth = 0f
    var touchHeight = 0f

    var rotate = 45f

    val touchCenterPoint = PointF()
    val moveCenterPoint = PointF()

    //矩形旋转后的左上角坐标点, 用来计算宽高
    val rectRotateLeftTopPoint = PointF()

    //按下时, 手势对应的矩形宽高
    var touchRectWidth = 0f
    var touchRectHeight = 0f

    //手指按下时, 距离右下角的宽高差距
    var touchDiffWidth = 0f
    var touchDiffHeight = 0f

    val _tempPoint = acquireTempPointF()

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                _touchPoint.set(event.x, event.y)
                touchWidth = drawRect.width()
                touchHeight = drawRect.height()

                val matrix = Matrix()
                matrix.postRotate(rotate, drawRect.centerX(), drawRect.centerY())
                rectRotateLeftTopPoint.set(drawRect.left, drawRect.top)
                matrix.mapPoint(rectRotateLeftTopPoint, rectRotateLeftTopPoint)

                touchCenterPoint.set(
                    _touchPoint.x - rectRotateLeftTopPoint.x,
                    _touchPoint.y - rectRotateLeftTopPoint.y
                )

                calcRotateBeforeDistance(
                    rectRotateLeftTopPoint.x, rectRotateLeftTopPoint.y,
                    _touchPoint.x, _touchPoint.y,
                    (_touchPoint.x + rectRotateLeftTopPoint.x) / 2,
                    (_touchPoint.y + rectRotateLeftTopPoint.y) / 2,
                    _tempPoint
                )
                touchRectWidth = _tempPoint.x
                touchRectHeight = _tempPoint.y

                touchDiffWidth = touchRectWidth - touchWidth
                touchDiffHeight = touchRectHeight - touchHeight

                disableParentInterceptTouchEvent()
            }

            MotionEvent.ACTION_MOVE -> {
                _movePoint.set(event.x, event.y)

                /*//1:这种方法不行, 当矩形足够大时, 缩放的距离会远大于手指移动的距离
                val scaleX = _movePoint.x / _touchPoint.x
                val scaleY = _movePoint.y / _touchPoint.y
                val scale = max(scaleX, scaleY)
                scaleRectBy(scale, scale)*/

                /*//2:这种方法也不行, 在矩形足够大时, 放大的距离会远大于手指移动的距离
                val dx = _movePoint.x - _touchPoint.x
                val dy = _movePoint.y - _touchPoint.y

                val newWidth = touchWidth + dx
                val newHeight = touchHeight + dy

                val scaleX = newWidth / touchWidth
                val scaleY = newHeight / touchHeight

                val scale = min(scaleX, scaleY)
                scaleRectBy(scale, scale)

                touchWidth = newWidth
                touchHeight = newHeight*/

                //3: 直接修改宽高, 完美
//                val dx = _movePoint.x - _touchPoint.x
//                val dy = _movePoint.y - _touchPoint.y
//
//                //手势移动的距离
//                val d = CanvasTouchHandler.spacing(_movePoint, _touchPoint)
//                //两点的角度
//                val a = CanvasTouchHandler.spacing(_movePoint, _touchPoint)
//
//                val dx2 = dx //d * sin(90 - rotate - a)  //dx /*/ sin(rotate.toDouble())*/
//                val dy2 = dy //d * cos(90 - rotate - a)  //dy /*/ sin(rotate.toDouble())*/
//
//                val newWidth = touchWidth + dx2
//                val newHeight = touchHeight + dy2

                moveCenterPoint.set(
                    _movePoint.x - rectRotateLeftTopPoint.x,
                    _movePoint.y - rectRotateLeftTopPoint.y
                )

                calcRotateBeforeDistance(
                    rectRotateLeftTopPoint.x, rectRotateLeftTopPoint.y,
                    _movePoint.x, _movePoint.y,
                    (_movePoint.x + rectRotateLeftTopPoint.x) / 2,
                    (_movePoint.y + rectRotateLeftTopPoint.y) / 2,
                    _tempPoint
                )
                val moveRectWidth = _tempPoint.x
                val moveRectHeight = _tempPoint.y

                val newWidth = moveRectWidth - touchDiffWidth
                val newHeight = moveRectHeight - touchDiffHeight

                val dx = moveRectWidth - touchRectWidth
                val dy = moveRectHeight - touchRectHeight

                /*val newWidth = touchWidth + dx
                val newHeight = touchHeight + dy*/

                val matrix = Matrix()
                matrix.postRotate(rotate, moveCenterPoint.x, moveCenterPoint.y)
                matrix.invert(matrix)

                val p1 = matrix.mapPoint(rectRotateLeftTopPoint, PointF())
                val p2 = matrix.mapPoint(_movePoint, PointF())

                /*val newWidth = p2.x - p1.x
                val newHeight = p2.y - p1.y*/

                //drawRect.adjustSizeWithLT(newWidth, newHeight)
                //drawRect.offset(dx, dy)

                /*drawRect.right = drawRect.left + newWidth
                drawRect.bottom = drawRect.top + newHeight

                touchWidth = newWidth
                touchHeight = newHeight
                invalidate()*/

                //支持旋转的矩形
                updateRectBoundsLT(drawRect, newWidth.toFloat(), newHeight.toFloat(), rotate)
                invalidate()

                touchRectWidth = moveRectWidth
                touchRectHeight = moveRectHeight

                touchWidth = drawRect.width()
                touchHeight = drawRect.height()

                touchCenterPoint.set(
                    _movePoint.x - rectRotateLeftTopPoint.x,
                    _movePoint.y - rectRotateLeftTopPoint.y
                )

                _touchPoint.set(_movePoint)
            }

            MotionEvent.ACTION_UP -> {
                rotate += 10f
                invalidate()
                disableParentInterceptTouchEvent(false)
            }

            MotionEvent.ACTION_CANCEL -> {
                disableParentInterceptTouchEvent(false)
            }
        }
        return true
    }

    fun scaleRectBy(scaleX: Float = 1f, scaleY: Float = 1f) {
        val oldRect = RectF(drawRect)
        val rect1 = RectF(drawRect)

        //左上角缩放
        val matrix = Matrix()
        matrix.postScale(scaleX, scaleY, drawRect.left, drawRect.top)
        matrix.mapRectF(drawRect, drawRect)

        //中点缩放
        matrix.reset()
        matrix.postScale(scaleX, scaleY, rect1.centerX(), rect1.centerY())
        matrix.mapRectF(rect1, rect1)

        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        drawRect.set(w / 3f, h / 3f, w / 2f, h / 2f)
    }

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.strokeWidth = 1 * dp

        val matrix = Matrix()
        matrix.postRotate(rotate, drawRect.centerX(), drawRect.centerY())
        canvas.withMatrix(matrix) {
            canvas.drawRect(drawRect, paint)
        }

        canvas.withTranslation(100f, 100f) {
            pathList.forEach { path ->
                paint.color = Color.GRAY
                canvas.drawPath(path, paint)

                val pathBounds = RectF()
                path.computeBounds(pathBounds, true)

                //获取路径上的所有点
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val pointComponents = path.approximate(0.5f)
                    val numPoints = pointComponents.size / 3

                    val pointPath = Path()
                    var componentIndex = 0
                    for (i in 0 until numPoints) {
                        val fraction = pointComponents[componentIndex++]
                        var x = pointComponents[componentIndex++]
                        var y = pointComponents[componentIndex++]

                        val pointArray = floatArrayOf(x, y)
                        val matrix = Matrix()
                        //matrix.setTranslate(measuredWidth / 2f, measuredHeight / 2f)
                        matrix.postScale(3f, 3f, pathBounds.centerX(), pathBounds.centerY())
                        matrix.postRotate(45f, pathBounds.centerX(), pathBounds.centerY())
                        matrix.mapPoints(pointArray)
                        x = pointArray[0]
                        y = pointArray[1]

                        if (componentIndex <= 3) {
                            pointPath.moveTo(x, y)
                        } else {
                            pointPath.lineTo(x, y)
                        }
                    }
                    paint.color = Color.BLUE
                    canvas.drawPath(pointPath, paint)
                }

                //获取路径上的所有点2
                val pointPath2 = Path()
                val pathMeasure = PathMeasure(path, false)
                var distance = 0f
                val posArray = floatArrayOf(0f, 0f)
                val tanArray = floatArrayOf(0f, 0f)
                while (distance < pathMeasure.length) {
                    if (pathMeasure.getPosTan(distance, posArray, tanArray)) {
                        val x = posArray[0]
                        val y = posArray[1]
                        if (distance == 0f) {
                            pointPath2.moveTo(x, y)
                        } else {
                            pointPath2.lineTo(x, y)
                        }
                    }
                    distance += 1f
                    if (distance > pathMeasure.length) {
                        if (pathMeasure.getPosTan(pathMeasure.length, posArray, tanArray)) {
                            val x = posArray[0]
                            val y = posArray[1]
                            if (distance == 0f) {
                                pointPath2.moveTo(x, y)
                            } else {
                                pointPath2.lineTo(x, y)
                            }
                        }
                        break
                    }
                }
                paint.color = Color.RED
                canvas.drawPath(pointPath2, paint)

                //---
            }
        }

        test1(canvas)
        test2(canvas)

        polygonPath?.let {
            canvas.withTranslation(mW() / 4f, mH() / 4f) {
                canvas.drawPath(it, paint)
            }
        }

        testShader(canvas)

        //虚线绘制
        drawLine(canvas)

        //测试svg指令解析
        testSvgPath(canvas)
    }

    private fun testSvgPath(canvas: Canvas) {
        val sx = 100f
        val sy = 100f
        val cx = 150f
        val cy = 20f
        val ex = 200f
        val ey = 100f
        val test = Path()
        test.moveTo(sx, sy)
        test.lineTo(cx, cy)
        test.lineTo(ex, ey)
        paint.color = Color.RED
        canvas.drawPath(test, paint)

        val svg = "M${sx},${sy} Q${cx},${cy},${ex},${ey}"
        val svgPath = svg.toPath()
        canvas.drawPath(svgPath, paint)

        val path = Path()
        path.moveTo(sx, sy)
        path.quadTo(cx, cy, ex, ey)
        paint.color = Color.GREEN
        canvas.drawPath(path, paint)

        val circle = VectorHelper.quadCenterOfCircle(sx, sy, cx, cy, ex, ey)
        val originPoint = VectorHelper.calculateCircleCenter(sx, sy, cx, cy, ex, ey)
        val originX = originPoint.x
        val originY = originPoint.y
        val circlePath = Path()
        circlePath.addCircle(originX, originY, circle.r, Path.Direction.CW)
        paint.color = Color.BLUE
        test.rewind()
        test.moveTo(sx, sy)
        test.lineTo(originX, originY)
        test.lineTo(ex, ey)
        canvas.drawPath(test, paint)
        canvas.drawPath(circlePath, paint)
    }

    var dashWidth = 0f
    var dashGap = 0f

    private fun drawLine(canvas: Canvas) {
        val paint = paint()
        val path = Path()
        val width = mW()
        val height = mH()
        path.moveTo(10f, height - 10f)
        path.lineTo(width - 10f, height - 10f)

        paint.style = Paint.Style.STROKE
        paint.pathEffect = DashPathEffect(floatArrayOf(dashWidth, dashGap), 0f)
        canvas.drawPath(path, paint)
    }

    private fun test1(canvas: Canvas) {
        //旋转测试
        val point = PointF()
        point.set(300f, 300f)
        canvas.drawCircle(point.x, point.y, 10 * dp, paint)
        val matrix = Matrix()
        matrix.postRotate(rotate, measuredWidth / 2f, measuredHeight / 2f)
        matrix.mapPoint(point, point)
        canvas.drawCircle(point.x, point.y, 10 * dp, paint)
        //反向旋转测试
        val invertMatrix = Matrix()
        matrix.invert(invertMatrix)
        invertMatrix.mapPoint(point, point)
        canvas.drawCircle(point.x, point.y, 10 * dp, paint)
    }

    private fun test2(canvas: Canvas) {
        //矩形旋转后,固定左上角缩放

        //绘制一个矩形
        paint.strokeWidth = 6 * dp
        val rect = RectF()
        val left = 400f
        val top = 400f
        val width = 400f
        val height = 200f
        paint.color = Color.RED
        rect.set(left, top, left + width, top + height)
        canvas.drawRect(rect, paint)
        //绘制矩形旋转后
        val matrix = Matrix()
        matrix.postRotate(rotate, rect.centerX(), rect.centerY())
        canvas.withMatrix(matrix) {
            canvas.drawRect(rect, paint)
        }

        //将矩形固定左上角缩放
        paint.strokeWidth = 4 * dp
        val newWidth = 200f
        val newHeight = 100f
        paint.color = Color.GREEN
        rect.adjustSize(newWidth, newHeight, ADJUST_TYPE_LT)
        //绘制缩放后的矩形
        canvas.drawRect(rect, paint)
        //缩放后, 旋转的矩形
        canvas.withMatrix(matrix) {
            canvas.drawRect(rect, paint)
        }

        //关键计算, 固定左上角之后, 原始矩形应该所处的位置
        paint.strokeWidth = 2 * dp
        val rectRotate = RectF()
        matrix.mapRectF(rect, rectRotate)
        /*paint.color = Color.BLUE
        canvas.drawRect(rectRotate, paint)*/

        /*val matrix2 = Matrix()
        matrix.reset()
        matrix.postRotate(rotate, rectRotate.centerX(), rectRotate.centerY())
        matrix.invert(matrix2)
        matrix2.mapRectF(rectRotate, rect)
        paint.color = Color.YELLOW
        canvas.drawRect(rect, paint)*/
        rect.offset(rectRotate.centerX() - rect.centerX(), rectRotate.centerY() - rect.centerY())
        paint.color = Color.BLUE
        canvas.drawRect(rect, paint)
        matrix.reset()
        matrix.postRotate(rotate, rect.centerX(), rect.centerY())
        canvas.withMatrix(matrix) {
            canvas.drawRect(rect, paint)
        }
    }

    /**固定左上角, 调整矩形的宽高
     * [rect] 需要旋转的矩形
     * [newWidth] [newHeight] 新的宽高
     * [rotate] 矩形当前旋转的角度*/
    private fun updateRectBoundsLT(rect: RectF, newWidth: Float, newHeight: Float, rotate: Float) {
        //原始矩形旋转的中心点坐标
        val originCenterX = rect.centerX()
        val originCenterY = rect.centerY()
        //左上角固定, 调整矩形宽高
        rect.adjustSize(newWidth, newHeight, ADJUST_TYPE_LT)

        //按照原始的旋转中点坐标, 旋转调整后的矩形
        val rotateRect = RectF()
        val matrix = Matrix()
        matrix.postRotate(rotate, originCenterX, originCenterY)
        matrix.mapRectF(rect, rotateRect)

        //旋转后的矩形中点就是调整后的矩形需要偏移的x,y
        rect.offset(rotateRect.centerX() - rect.centerX(), rotateRect.centerY() - rect.centerY())
    }

    /**计算2个在反向旋转后的x,y距离*/
    private fun calcRotateBeforeDistance(
        x1: Float, y1: Float,
        x2: Float, y2: Float,
        rotateCenterX: Float, rotateCenterY: Float,
        result: PointF
    ): PointF {
        val matrix = Matrix()
        matrix.postRotate(rotate, rotateCenterX, rotateCenterY)
        matrix.invert(matrix)

        val p1 = matrix.mapPoint(x1, y1)
        val p1x = p1.x
        val p1y = p1.y
        val p2 = matrix.mapPoint(x2, y2)
        val p2x = p2.x
        val p2y = p2.y

        result.x = p2x - p1x
        result.y = p2y - p1y

        return result
    }

    fun testShader(canvas: Canvas) {

    }
}