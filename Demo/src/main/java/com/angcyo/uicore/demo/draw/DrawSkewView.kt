package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.*
import com.angcyo.canvas.utils.createPaint
import com.angcyo.library.L
import com.angcyo.library.ex.*
import com.angcyo.library.gesture.RectScaleGestureHandler

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/01/09
 */
class DrawSkewView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {
    val anchorPoint = PointF()

    val subRect = RectF()
    val groupRect = RectF()
    var subRotate = 30f
        set(value) {
            field = value % 360
            updateGroupRect()
        }
    var groupRotate = 0f
        set(value) {
            field = value % 360
            subRotate += value
        }

    var groupScaleX = 1f
    var groupScaleY = 1f

    var subSkewX = 0f
    var subSkewY = 0f

    val subMatrix = Matrix()
    val subMatrix2 = Matrix()

    val paint = createPaint().apply {
        strokeWidth = 1 * dp
    }

    init {
        groupRotate = 0f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val centerX = w / 2
        val centerY = h / 2

        val width = 100 * dp
        val height = width

        val margin = width / 2
        //anchorPoint.set(centerX - 2 * width, centerY - 2 * height)
        //anchorPoint.set(centerX - width, centerY - height)
        //anchorPoint.set(0f, 0f)

        /*subRect.set(
            centerX - width / 2f,
            centerY - height / 2f,
            centerX + width / 2f,
            centerY + height / 2f
        )*/
        val r = w - margin
        val b = h - margin
        val l = r - width
        val t = b - height
        subRect.set(l, t, r, b)
        anchorPoint.set(l - width, t - height)

        updateGroupRect()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        //drawOriginRect(canvas)
        //drawTest1(canvas)
        //drawTest2(canvas)

        drawGroup(canvas)
        //drawSub(canvas)
        drawSub2(canvas)
    }

    //---

    /**绘制Sub2*/
    fun drawSub2(canvas: Canvas) {
        val drawRect = RectF(subRect)
        val color = Color.BLACK

        val matrix1 = Matrix()
        matrix1.setRotate(subRotate, subRect.centerX(), subRect.centerY())
        val matrix2 = Matrix()
        matrix2.setScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        matrix2.postRotate(groupRotate, anchorPoint.x, anchorPoint.y)
        val matrix3 = Matrix(matrix1)
        matrix3.postConcat(matrix2)
        canvas.withMatrix(matrix3) {
            paint.color = color
            drawRect(drawRect, paint)
        }
        L.i("groupRotate:$groupRotate subRotate:$subRotate\n${matrix1.toLogString()}${matrix2.toLogString()}${matrix3.toLogString()}")

        //---拆解数据
        val subScaleX = matrix3.getScaleX()
        val subScaleY = matrix3.getScaleY()

        val subSkewX = matrix3.getSkewX()
        val subSkewY = matrix3.getSkewY()

        val subTranX = matrix3.getTranslateX()
        val subTranY = matrix3.getTranslateY()

        val subRotate = matrix3.getRotate()

        //只绘制缩放
        canvas.withScale(subScaleX, subScaleY, subRect.centerX(), subRect.centerY()) {
            paint.color = color
            drawRect(drawRect, paint)
        }

        //只绘制倾斜
        canvas.withSkew(subSkewX, subSkewY) {
            paint.color = color
            drawRect(drawRect, paint)
        }
    }

    /**绘制Group*/
    fun drawGroup(canvas: Canvas) {
        val drawRect = RectF(groupRect)
        val color = Color.WHITE

        //原数据绘制
        canvas.withSave {
            paint.color = color
            drawRect(groupRect, paint)
        }
        canvas.withRotation(groupRotate, groupRect.centerX(), groupRect.centerY()) {
            paint.color = Color.BLACK
            //drawRect(groupRect, paint)
        }

        //缩放旋转后绘制√
        val matrix = Matrix()
        matrix.setRotate(groupRotate, anchorPoint.x, anchorPoint.y)
        matrix.postScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        canvas.withMatrix(matrix) {
            paint.color = color
            drawRect(groupRect, paint)//这种方式绘制, 会看起来会扁了
        }
        L.i("指定锚点缩放${anchorPoint}->${matrix.toLogString()}")

        val dataMatrix = Matrix()
        dataMatrix.setRotate(groupRotate, groupRect.centerX(), groupRect.centerY())
        dataMatrix.postScale(groupScaleX, groupScaleY, groupRect.centerX(), groupRect.centerY())

        drawRect.scale(groupScaleX, groupScaleY, groupRect.centerX(), groupRect.centerY())
        val drawMatrix = Matrix()
        drawMatrix.setSkew(
            matrix.getSkewX(),
            matrix.getSkewY(),
            groupRect.centerX(),
            groupRect.centerY()
        )
        canvas.withMatrix(drawMatrix) {
            paint.color = color
            drawRect(drawRect, paint)//这种方式绘制, 会看起来会扁了
        }

        /*val matrix2 = Matrix()
        matrix2.setRotate(groupRotate)
        matrix2.postScale(groupScaleX, groupScaleY)
        L.i("默认锚点缩放->${matrix2.toLogString()}")
        canvas.withMatrix(matrix2) {
            paint.color = Color.RED
            drawRect(groupRect, paint)
        }*/

        //使用数据缩放的方式绘制, 就不会看起来扁了
        /*val drawMatrix = Matrix()
        drawMatrix.setRotate(groupRotate, groupRect.centerX(), groupRect.centerY())
        matrix.mapRect(drawRect)
        drawMatrix.postTranslate(
            drawRect.centerX() - groupRect.centerX(),
            drawRect.centerY() - groupRect.centerY()
        )
        drawRect.set(groupRect)
        drawRect.scale(groupScaleX, groupScaleY, groupRect.centerX(), groupRect.centerY())
        L.i("指定锚点缩放${anchorPoint}->${groupRect}->${drawRect}")
        canvas.withMatrix(drawMatrix) {
            paint.color = color
            drawRect(drawRect, paint)
        }*/

        /*val dataMatrix = Matrix()
        dataMatrix.setScale(groupScaleX, groupScaleY, groupRect.centerX(), groupRect.centerY())
        dataMatrix.mapRect(drawRect)

        val drawMatrix = Matrix()
        drawMatrix.set(matrix)

        val invertDataMatrix = Matrix()
        dataMatrix.invert(invertDataMatrix)
        drawMatrix.postConcat(invertDataMatrix)

        canvas.withMatrix(drawMatrix) {
            paint.color = color
            drawRect(drawRect, paint)
        }*/


        /*val test = RectF(groupRect)
        test.scale(groupScaleX, groupScaleY, 0f, 0f)
        L.i("默认锚点缩放$->${groupRect}->${test}")*/
    }

    /**绘制Sub*/
    fun drawSub(canvas: Canvas) {
        val drawRect = RectF(subRect)
        val color = Color.WHITE

        //原数据绘制
        canvas.withSave {
            paint.color = color
            drawRect(subRect, paint)
        }
        canvas.withRotation(subRotate, subRect.centerX(), subRect.centerY()) {
            paint.color = Color.BLACK
            drawRect(subRect, paint)
        }

        //缩放旋转后绘制
        /*val matrix = Matrix()
        matrix.setRotate(subRotate)
        matrix.postScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        canvas.withMatrix(matrix) {
            paint.color = Color.RED
            drawRect(subRect, paint)//这种方式绘制, 会看起来扁了
        }*/

        //使用数据缩放的方式绘制, 就不会看起来扁了
        drawRect.scale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        canvas.withRotation(subRotate, anchorPoint.x, anchorPoint.y) {
            paint.color = Color.YELLOW
            //drawRect(drawRect, paint)
        }

        //不旋转时, 缩放绘制正确√
        val drawRect2 = RectF(subRect)
        drawRect2.scale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        canvas.withSave {
            paint.color = color
            //drawRect(drawRect2, paint)//直接缩放绘制的矩形
        }

        val drawMatrix = Matrix()
        drawMatrix.setRotate(subRotate, drawRect2.centerX(), drawRect2.centerY())
        drawMatrix.postSkew(subSkewX, subSkewY, drawRect2.centerX(), drawRect2.centerY())
        canvas.withMatrix(drawMatrix) {
            paint.color = Color.BLUE
            //drawRect(drawRect2, paint)
        }

        //内外旋转, 缩放绘制正确√
        val drawRect3 = RectF(subRect)
        val matrix1 = Matrix()
        matrix1.setRotate(subRotate, subRect.centerX(), subRect.centerY())
        val matrix2 = Matrix()
        matrix2.setScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        matrix2.postRotate(groupRotate, anchorPoint.x, anchorPoint.y)
        val matrix3 = Matrix(matrix1)
        matrix3.postConcat(matrix2)
        canvas.withMatrix(matrix3) {
            paint.color = Color.CYAN
            drawRect(drawRect3, paint)
        }
        L.i("groupRotate:$groupRotate subRotate:$subRotate\n${matrix1.toLogString()}${matrix2.toLogString()}${matrix3.toLogString()}")

        val rect3 = RectF(subRect)
        matrix3.mapRect(rect3)
        canvas.withSave {
            paint.color = Color.BLACK
            drawRect(rect3, paint)
        }

        //---拆解数据
        val subScaleX = matrix3.getScaleX()
        val subScaleY = matrix3.getScaleY()

        val subSkewX = matrix3.getSkewX()
        val subSkewY = matrix3.getSkewY()

        val subTranX = matrix3.getTranslateX()
        val subTranY = matrix3.getTranslateY()

        val subRotate = matrix3.getRotate()

        val testRotateMatrix = Matrix()
        testRotateMatrix.setRotate(subRotate, subRect.centerX(), subRect.centerY())

        val testTranslateMatrix = Matrix()
        testTranslateMatrix.set(matrix3)
        testTranslateMatrix.postTranslate(
            subRect.centerX() - rect3.centerX(),
            subRect.centerY() - rect3.centerY()
        )

        //---移除偏移量绘制
        matrix3.updateTranslate(0f, 0f)
        canvas.withMatrix(matrix3) {
            paint.color = Color.BLACK
            //drawRect(drawRect3, paint)
        }

        val m = Matrix()
        //m.setSkew(subSkewX, subSkewY, subDrawRect.centerX(), subDrawRect.centerY())
        //m.postRotate(subRotate, subDrawRect.centerX(), subDrawRect.centerY())
        /*canvas.withRotation(subRotate, subRect.centerX(), subRect.centerY()) {
            withSkew(subSkewX, subSkewY) {
                paint.color = Color.RED
                drawRect(subRect, paint)
            }
        }*/
        val r = RectF(subRect)
        r.scale(subScaleX, subScaleY, r.centerX(), r.centerY())
        m.setSkew(subSkewX, subSkewY, r.centerX(), r.centerY())
        //m.postTranslate(subTranX, subTranY)
        canvas.withMatrix(m) {
            paint.color = Color.RED
            drawRect(r, paint)
        }

        val subDrawRect = RectF(subRect)
        subDrawRect.scale(subScaleX, subScaleY, subDrawRect.centerX(), subDrawRect.centerY())
        val subDrawMatrix = Matrix()
        //subDrawMatrix.setSkew(subSkewX, subSkewY, subDrawRect.centerX(), subDrawRect.centerY())
        //subDrawMatrix.postRotate(subRotate, subDrawRect.centerX(), subDrawRect.centerY())
        //subDrawMatrix.setRotate(subRotate, subDrawRect.centerX(), subDrawRect.centerY())
        //subDrawMatrix.postSkew(subSkewX, subSkewY, subDrawRect.centerX(), subDrawRect.centerY())
        subDrawMatrix.updateScale(subScaleX, subScaleY)
        subDrawMatrix.updateSkew(subSkewX, subSkewY)
        subDrawMatrix.updateTranslate(subRect.left, subRect.top)

        canvas.withMatrix(subDrawMatrix) {
            paint.color = Color.RED
            //drawRect(subRect, paint) //绘制
        }
        L.i(subDrawMatrix.toLogString())

        /*canvas.withSkew(subSkewX, subSkewY) {
            withRotation(subRotate, subRect.centerX(), subRect.centerY()) {
                paint.color = Color.MAGENTA
                drawRect(subDrawRect, paint) //绘制
            }
        }*/

        val denom = Math.pow(subScaleX.toDouble(), 2.0) + Math.pow(subSkewY.toDouble(), 2.0)
        val sx = Math.sqrt(denom)
        val sy = (subScaleX * subScaleY - subSkewX * subSkewY) / sx
        val kx = Math.atan2((subScaleX * subSkewX + subSkewY * subScaleY).toDouble(), denom)
        val ky = 0.0
        val angle = Math.atan2(subSkewY.toDouble(), subScaleX.toDouble())

        val rect2 = RectF(subRect)
        rect2.scale(sx.toFloat(), sy.toFloat(), subRect.centerX(), subRect.centerY())

        val matrix4 = Matrix()
        matrix4.setRotate(angle.toFloat(), subRect.centerX(), subRect.centerY())
        matrix4.postSkew(kx.toFloat(), ky.toFloat(), subRect.centerX(), subRect.centerY())
        canvas.withMatrix(matrix4) {
            paint.color = Color.RED
            //drawRect(rect2, paint) //绘制
        }
        L.i("angle:$angle\n${matrix4.toLogString()}")
    }

    //---

    /**更新Group包裹范围*/
    fun updateGroupRect() {
        val scaleRect = RectF(subRect)
        scaleRect.rotate(subRotate)
        groupRect.set(
            minOf(anchorPoint.x, scaleRect.left),
            minOf(anchorPoint.y, scaleRect.top),
            maxOf(anchorPoint.x, scaleRect.right),
            maxOf(anchorPoint.y, scaleRect.bottom)
        )
    }

    fun calcGroupRect(): RectF {
        /*val scaleRect =
            RectF(subRect).scale(groupScaleX, groupScaleY, subRect.centerX(), subRect.centerY())
                .rotate(subRotate, subRect.centerX(), subRect.centerY())*/
        val scaleRect = RectF(subRect)
        val matrix = Matrix()
        matrix.setRotate(subRotate, subRect.centerX(), subRect.centerY())
        matrix.postScale(groupScaleX, groupScaleX, anchorPoint.x, anchorPoint.y)
        matrix.mapRect(scaleRect)

        val rect = RectF(
            minOf(anchorPoint.x, scaleRect.left),
            minOf(anchorPoint.y, scaleRect.top),
            maxOf(anchorPoint.x, scaleRect.right),
            maxOf(anchorPoint.y, scaleRect.bottom),
        )
        return rect
    }

    /**绘制直接作用的矩形效果*/
    fun drawOriginRect(canvas: Canvas) {
        val rect = RectF(subRect)
        val matrix = Matrix()
        matrix.setRotate(subRotate, subRect.centerX(), subRect.centerY())
        matrix.postScale(groupScaleX, groupScaleX, anchorPoint.x, anchorPoint.y)
        canvas.withMatrix(matrix) {
            paint.color = Color.YELLOW
            drawRect(rect, paint)
        }
    }

    fun drawTest1(canvas: Canvas) {
        //---
        val subDrawRect = RectF(subRect)

        val groupRect = RectF(
            minOf(anchorPoint.x, subDrawRect.left),
            minOf(anchorPoint.y, subDrawRect.top),
            maxOf(anchorPoint.x, subDrawRect.right),
            maxOf(anchorPoint.y, subDrawRect.bottom),
        )

        RectScaleGestureHandler.rectScaleToWithGroup(
            subDrawRect,
            subDrawRect,
            groupScaleX,
            groupScaleY,
            anchorPoint.x,
            anchorPoint.y,
            groupRotate,
            groupRect,
            0f,
            0f
        )

        drawRect(canvas, subDrawRect, subRotate, subSkewX, subSkewY, Color.BLUE)
        subDrawRect.rotate(subRotate)
        canvas.withSave {
            paint.color = Color.WHITE
            drawRect(subDrawRect, paint)
        }

        //---
        subMatrix.reset()
        subMatrix.setRotate(subRotate, subRect.centerX(), subRect.centerY())
        subMatrix.postScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        //subMatrix.setScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        //subMatrix.postRotate(subRotate, subRect.centerX(), subRect.centerY())

        val subXLength = subRect.centerX() - groupRect.left
        val subYLength = subRect.centerY() - groupRect.top

        val newCenterX = groupRect.left + subXLength * groupScaleX
        val newCenterY = groupRect.top + subYLength * groupScaleY

        val dx = newCenterX - subRect.centerX()
        val dy = newCenterY - subRect.centerY()
        //subMatrix.postTranslate(dx, dy)
        canvas.withMatrix(subMatrix) {
            paint.color = Color.RED
            drawRect(subRect, paint)
        }
        drawRect(
            canvas,
            subDrawRect,
            subMatrix.getRotateDegrees(),
            subMatrix.getSkewX(),
            subMatrix.getSkewY(),
            Color.BLUE
        )

        //---
        val groupDrawRect = RectF(
            minOf(anchorPoint.x, subDrawRect.left),
            minOf(anchorPoint.y, subDrawRect.top),
            maxOf(anchorPoint.x, subDrawRect.right),
            maxOf(anchorPoint.y, subDrawRect.bottom),
        )
        groupDrawRect.scale(groupScaleX, groupScaleY)

        //---
        val subCenterXRatio = (subRect.centerX() - groupRect.left) / groupRect.width()
        val subCenterYRatio = (subRect.centerY() - groupRect.top) / groupRect.height()

        val newCenterX2 = groupRect.left + groupDrawRect.width() * subCenterXRatio
        val newCenterY2 = groupRect.top + groupDrawRect.height() * subCenterYRatio

        val dx2 = newCenterX2 - subRect.centerX()
        val dy2 = newCenterY2 - subRect.centerY()

        subMatrix2.reset()
        subMatrix2.setRotate(subRotate, subRect.centerX(), subRect.centerY())
        subMatrix2.postScale(groupScaleX, groupScaleY, subRect.centerX(), subRect.centerY())
        subMatrix2.postTranslate(dx2, dy2)
        canvas.withMatrix(subMatrix2) {
            paint.color = Color.BLACK
            drawRect(subRect, paint)
        }

        //---
        canvas.withRotation(groupRotate, groupDrawRect.centerX(), groupDrawRect.centerY()) {
            paint.color = Color.GREEN
            drawRect(groupDrawRect, paint)
        }
        groupDrawRect.rotate(groupRotate)
        canvas.withSave {
            paint.color = Color.WHITE
            drawRect(groupDrawRect, paint)
        }
    }

    fun drawTest2(canvas: Canvas) {
        //---
        val groupRect = calcGroupRect()
        val groupScaleRect = RectF(groupRect).scale(
            groupScaleX,
            groupScaleY
        )//RectF(groupRect).scale(groupScaleX, groupScaleY)
        drawRect(canvas, groupScaleRect, groupRotate, 0f, 0f, Color.GREEN)

        val subDrawRect = RectF(subRect).scale(
            groupScaleX, groupScaleY,
            subRect.centerX(), subRect.centerY()
        )

        //---
        val centerPoint = PointF(subRect.centerX(), subRect.centerY()).scale(
            groupScaleX,
            groupScaleY,
            anchorPoint.x,
            anchorPoint.y
        )
        val newCenterX = centerPoint.x
        val newCenterY = centerPoint.y

        val dx = newCenterX - subDrawRect.centerX()
        val dy = newCenterY - subDrawRect.centerY()

        subMatrix2.reset()
        subMatrix2.setRotate(subRotate, subRect.centerX(), subRect.centerY())
        subMatrix2.postScale(groupScaleX, groupScaleY, subRect.centerX(), subRect.centerY())
        subMatrix2.postTranslate(dx, dy)
        canvas.withMatrix(subMatrix2) {
            paint.color = Color.BLACK
            drawRect(subRect, paint)
        }

        //---
        drawRect(canvas, subDrawRect, subRotate, subSkewX, subSkewY, Color.BLUE)
    }

    fun drawRect(
        canvas: Canvas,
        rect: RectF,
        rotate: Float,
        skx: Float,
        sky: Float,
        color: Int = Color.BLUE
    ) {
        canvas.withSkew(skx, sky) {
            canvas.withRotation(rotate, rect.centerX(), rect.centerY()) {
                paint.color = color
                drawRect(rect, paint)
            }
        }
    }
}