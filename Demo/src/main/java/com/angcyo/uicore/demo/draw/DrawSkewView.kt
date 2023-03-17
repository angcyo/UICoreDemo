package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.graphics.*
import com.angcyo.library.L
import com.angcyo.library.ex.*
import com.angcyo.library.gesture.RectScaleGestureHandler
import java.lang.Math.pow
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/01/09
 */
class DrawSkewView(context: Context, attributeSet: AttributeSet? = null) :
    BaseMatrixView(context, attributeSet) {

    val subMatrix = Matrix()
    val subMatrix2 = Matrix()

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        //drawOriginRect(canvas)
        //drawTest1(canvas)
        //drawTest2(canvas)
        //drawTest3(canvas)

        //drawGroup(canvas)
        //drawSub(canvas)
        //drawSub2(canvas)

        //draw2Group(canvas)
        //draw2Sub(canvas)

        //--

        //drawSubDefault(canvas)
        //drawSubDefault2(canvas)
        drawSubMatrix(canvas)

        //---

        draw3Group(canvas)
        //draw3Sub(canvas)
        //draw3SubRotate(canvas)
        //draw3SubSkew(canvas)
        draw4Sub(canvas)

        val rect = RectF(0f, 0f, 100f, 40f)
        val matrix = Matrix()
        matrix.setRotate(10f, rect.centerX(), rect.centerY())
        matrix.postScale(2.5145070086f, 1.0925665152f, rect.centerX(), rect.centerY())
        matrix.postSkew(-16.8075149696f.toRadians(), 0f, rect.centerX(), rect.centerY())

        rect.offsetTo(100f, 100f)
        canvas.withMatrix(matrix) {
            paint.color = Color.RED
            drawRect(rect, paint)
        }

        testSaveVaule(canvas, subMatrix())
        //testSaveVaule(canvas,matrix)

        //---

        /*testScaleMatrix()
        testRotateMatrix()

        testRotateScaleMatrix()

        testSkewMatrix()

        val matrix = Matrix()
        val rotateMatrix = Matrix()
        rotateMatrix.setRotate(30f)

        matrix.postConcat(rotateMatrix)*/

        testMatrix()

        testRect()
        L.d()
    }

    fun testMatrix() {
        val m = Matrix()
        m.setScale(1.5f, 1.3f)
        m.postSkew(1f, 1f)

        val m2 = Matrix()
        m2.setScale(1.5f, 1.3f, 100f, 100f)
        m2.postSkew(1f, 1f, 100f, 100f)

        val m3 = Matrix()
        m3.setTranslate(-100f, -100f)
        m3.preConcat(m)
        val m4 = Matrix()
        m4.setTranslate(-100f, -100f)
        m4.postConcat(m)
        L.d()
    }

    //---

    /**默认只处理缩放属性的绘制方式*/
    fun drawSubDefault(canvas: Canvas) {
        val color = Color.WHITE
        val rect = RectF(subRect)
        rect.scaleFromCenter(groupScaleX, groupScaleY)
        rect.offsetCenterTo(targetRectBounds().centerX(), targetRectBounds().centerY())

        val drawMatrix = Matrix()
        drawMatrix.setRotate(subRotate, rect.centerX(), rect.centerY())
        drawMatrix.postSkew(subSkewX, subSkewY, rect.centerX(), rect.centerY())
        canvas.withMatrix(drawMatrix) {
            paint.color = color
            drawRect(rect, paint)
        }
    }

    fun drawSubDefault2(canvas: Canvas) {
        val color = Color.CYAN
        val rect = RectF(subRect)
        rect.scaleFromCenter(groupScaleX, groupScaleY)
        rect.offsetCenterTo(targetRectBounds().centerX(), targetRectBounds().centerY())

        val drawMatrix = Matrix()
        drawMatrix.setRotate(subMatrix().getRotateDegrees(), rect.centerX(), rect.centerY())
        drawMatrix.postSkew(subSkewX, subSkewY, rect.centerX(), rect.centerY())
        canvas.withMatrix(drawMatrix) {
            paint.color = color
            drawRect(rect, paint)
        }
    }

    /**使用动态计算出来的matrix绘制
     * [draw3Sub]*/
    fun drawSubMatrix(canvas: Canvas) {
        val color = Color.BLACK
        val matrix = subMatrix()

        //√
        //使用path transform的方式绘制出来的视觉效果最佳, 不会出现线的粗细差
        val path = Path()
        path.addRect(subRect, Path.Direction.CW)
        path.transform(matrix)
        canvas.withSave {
            paint.color = color
            drawPath(path, paint)
        }

        //√
        //使用matrix的方式绘制出来的视觉效果会有粗细差
        /*matrix.updateTranslate()//debug
        val rect = RectF(subRect)
        rect.offsetCenterTo(subRect.width(), subRect.height())
        canvas.withMatrix(matrix) {
            paint.color = color
            drawRect(rect, paint)
        }*/

        //---test
        /*canvas.withMatrix(subMatrix2()) {
            paint.color = Color.RED
            drawRect(subRect, paint)
        }*/
    }

    fun groupMatrix(): Matrix {
        val matrix = Matrix()
        //matrix.setRotate(groupRotate, groupRect.centerX(), groupRect.centerY())
        //matrix.postScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        val rect = RectF(groupRect)
        matrix.setScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        //matrix.mapRect(rect)
        /*matrix.postRotate(
            groupRotate,
            rect.centerX(),
            rect.centerY()
        )*/
        matrix.postRotate(groupRotate, anchorPoint.x, anchorPoint.y)

        return matrix
    }

    fun draw3Group(canvas: Canvas) {
        val matrix = groupMatrix()

        val path = Path()
        path.addRect(groupRect, Path.Direction.CW)
        path.transform(matrix)

        val color = Color.WHITE
        canvas.withSave {
            paint.color = color
            drawPath(path, paint)
        }
    }

    fun subMatrix(): Matrix {
        val matrix = Matrix()
        matrix.setRotate(subRotate, subRect.centerX(), subRect.centerY())

        val groupMatrix = groupMatrix()
        matrix.postConcat(groupMatrix)
        return matrix
    }

    fun subMatrix2(): Matrix {
        val matrix = Matrix()
        matrix.setRotate(subRotate, subRect.centerX(), subRect.centerY())

        val groupMatrix = groupMatrix()
        //matrix.postConcat(groupMatrix)
        matrix.preConcat(groupMatrix)
        return matrix
    }

    fun targetRectBounds(): RectF {
        val rect = RectF(subRect)
        subMatrix().mapRect(rect)
        return rect
    }

    fun draw3Sub(canvas: Canvas) {
        val color = Color.BLACK
        val matrix = subMatrix()

        //√
        //使用path transform的方式绘制出来的视觉效果最佳, 不会出现线的粗细差
        val path = Path()
        path.addRect(subRect, Path.Direction.CW)
        path.transform(matrix)

        canvas.withSave {
            paint.color = color
            drawPath(path, paint)
        }

        //√
        //使用matrix的方式绘制出来的视觉效果会有粗细差
        /*canvas.withMatrix(matrix) {
            paint.color = Color.RED
            drawRect(subRect, paint)
        }*/

        //---×

        /*val rotateMatrix = Matrix()
        val rotateMatrixInvert = Matrix()
        rotateMatrix.setRotate(matrix.getRotateDegrees())
        rotateMatrix.invert(rotateMatrixInvert)

        val resultMatrix = Matrix()
        resultMatrix.set(matrix)
        resultMatrix.postConcat(rotateMatrixInvert)

        val t = Matrix(rotateMatrix)
        t.postConcat(resultMatrix)

        val rect = RectF(subRect)
        rect.scaleFromCenter(resultMatrix.getScaleX(), resultMatrix.getScaleY())
        canvas.withRotation(matrix.getRotateDegrees(), subRect.centerX(), subRect.centerY()) {
            paint.color = Color.RED
            drawRect(rect, paint)
        }*/

        //---×

        /*val rotateMatrix = Matrix()
        rotateMatrix.setRotate(matrix.getRotateDegrees(), subRect.centerX(), subRect.centerY())

        val scaleMatrix = Matrix()
        scaleMatrix.setScale(
            matrix.getScaleX() / rotateMatrix.getScaleX(),
            matrix.getScaleY() / rotateMatrix.getScaleY(),
            subRect.centerX(), subRect.centerY()
        )

        val t = Matrix(rotateMatrix)
        t.postConcat(scaleMatrix)

        val targetRect = RectF(subRect)
        matrix.mapRect(targetRect)

        val rect = RectF(subRect)
        scaleMatrix.mapRect(rect)
        rect.offsetCenterTo(targetRect.centerX(), targetRect.centerY())

        val drawMatrix = Matrix()
        drawMatrix.setRotate(matrix.getRotateDegrees(), rect.centerX(), rect.centerY())
        drawMatrix.postSkew(
            matrix.getSkewX() / rotateMatrix.getSkewX(),
            matrix.getSkewY() / rotateMatrix.getSkewY(),
            rect.centerX(), rect.centerY()
        )
        canvas.withMatrix(drawMatrix) {
            paint.color = Color.RED
            drawRect(rect, paint)
        }*/

        //---×

        /*val targetRect = RectF(subRect)
        matrix.mapRect(targetRect)

        val rotateMatrix = Matrix()
        rotateMatrix.setRotate(matrix.getRotateDegrees(), subRect.centerX(), subRect.centerY())
        val rotateMatrixInvert = Matrix()
        rotateMatrix.invert(rotateMatrixInvert)

        val scaleMatrix = Matrix(matrix)
        scaleMatrix.postConcat(rotateMatrixInvert)

        val subScaleX = matrix.getScaleX() / rotateMatrix.getScaleX()
        val subScaleY = matrix.getScaleY() / rotateMatrix.getScaleY()

        val subSkewX = matrix.getSkewX() / rotateMatrix.getSkewX()
        val subSkewY = matrix.getSkewY() / rotateMatrix.getSkewY()

        val t = Matrix(rotateMatrix)
        t.postScale(subScaleX, subScaleY)
        t.postSkew(subSkewX, subSkewY)

        val subScaleX2 = scaleMatrix.getScaleX()
        val subScaleY2 = scaleMatrix.getScaleY()

        val subSkewX2 = scaleMatrix.getSkewX()
        val subSkewY2 = scaleMatrix.getSkewY()
        val t2 = Matrix(rotateMatrix)
        t2.postScale(subScaleX2, subScaleY2)
        t2.postSkew(subSkewX2, subSkewY2)

        val angle = matrix.getRotateDegrees()
        val denom = Math.pow(matrix.getScaleX().toDouble(), 2.0) + Math.pow(
            matrix.getSkewY().toDouble(),
            2.0
        )
        val sx = Math.sqrt(denom)
        val sy =
            (matrix.getScaleX() * matrix.getScaleY() - matrix.getSkewX() * matrix.getSkewY()) / sx
        val kx = Math.atan2(
            (matrix.getScaleX() * matrix.getSkewX() + matrix.getSkewY() * matrix.getScaleY()).toDouble(),
            denom
        )
        val ky = 0.0

        val t3 = Matrix()
        t3.setRotate(angle)
        t3.postScale(sx.toFloat(), sy.toFloat())
        t3.postSkew(kx.toFloat(), ky.toFloat())

        val drawMatrix = Matrix()
        val rect = RectF(subRect)
        rect.scale(subScaleX2, subScaleY2)
        rect.offsetCenterTo(targetRect.centerX(), targetRect.centerY())
        drawMatrix.setRotate(matrix.getRotateDegrees(), rect.centerX(), rect.centerY())
        //drawMatrix.postSkew(subSkewX2, subSkewY2, rect.centerX(), rect.centerY())
        canvas.withMatrix(drawMatrix) {
            paint.color = Color.RED
            drawRect(rect, paint)
        }*/

        //---

        val angle = matrix.getRotateDegrees()
        val denom = pow(matrix.getScaleX().toDouble(), 2.0) +
                pow(matrix.getSkewY().toDouble(), 2.0)
        val sx = sqrt(denom)
        val sy =
            (matrix.getScaleX() * matrix.getScaleY() - matrix.getSkewX() * matrix.getSkewY()) / sx
        val kx = atan2(
            (matrix.getScaleX() * matrix.getSkewX() + matrix.getSkewY() * matrix.getScaleY()).toDouble(),
            denom
        )
        val ky = 0.0

        val rect2 = RectF(subRect)
        rect2.scale(sx.toFloat(), sy.toFloat())
        rect2.offsetCenterTo(targetRectBounds().centerX(), targetRectBounds().centerY())

        val drawMatrix = Matrix()
        drawMatrix.setRotate(angle, rect2.centerX(), rect2.centerY())
        drawMatrix.postSkew(kx.toFloat(), ky.toFloat(), rect2.centerX(), rect2.centerY())
        canvas.withMatrix(drawMatrix) {
            paint.color = Color.RED
            drawRect(rect2, paint)
        }


        //---
        val rect = RectF(subRect)

        /*val rotation = matrix.getRotate()
        canvas.withRotation(rotation, rect.centerX(), rect.centerY()) {
            paint.color = Color.RED
            drawRect(rect, paint)
        }*/

        //这种方式可以绘制出原始rect变换的轮廓rect
        canvas.withSave {
            matrix.mapRect(rect)
            paint.color = Color.YELLOW
            drawRect(rect, paint)
        }

        //--

        /*val rotateMatrix = rotateMatrix(rotation)
        val subScaleX = matrix.getScaleX() / rotateMatrix.getScaleX()
        val subScaleY = matrix.getScaleY() / rotateMatrix.getScaleY()

        val subSkewX = matrix.getSkewX() - rotateMatrix.getSkewX()// * subScaleX
        val subSkewY = matrix.getSkewY() - rotateMatrix.getSkewY()// * subScaleY

        val rect2 = RectF(subRect)
        rect2.scaleFromCenter(subScaleX, subScaleY)
        rect2.offsetCenterTo(rect.centerX(), rect.centerY())

        val t = Matrix()
        val rect3 = RectF(subRect)
        t.setRotate(rotation, rect3.centerX(), rect3.centerY())
        t.postScale(subScaleX, subScaleY, rect3.centerX(), rect3.centerY())
        t.postSkew(subSkewX, subSkewY, rect3.centerX(), rect3.centerY())
        t.updateTranslate()
        rect3.offsetCenterTo(rect.centerX(), rect.centerY())
        canvas.withMatrix(t) {
            paint.color = Color.BLUE
            drawRect(rect3, paint)
        }*/

        //只处理缩放和旋转, 不处理倾斜√
        /*val drawMatrix = Matrix()
        drawMatrix.setRotate(subRotate, rect2.centerX(), rect2.centerY())
        //drawMatrix.postSkew(subSkewX, subSkewY, rect2.centerX(), rect2.centerY())
        canvas.withMatrix(drawMatrix) {
            paint.color = Color.GREEN
            drawRect(rect2, paint)
        }*/

        //--- ×
        /*val rect2 = RectF(subRect)

        val rotateMatrix = rotateMatrix(subRotate)
        val subScaleX = matrix.getScaleX() / rotateMatrix.getScaleX()
        val subScaleY = matrix.getScaleY() / rotateMatrix.getScaleY()
        rect2.scale(subScaleX, subScaleY)
        rect2.offsetCenterTo(targetRectBounds().centerX(), targetRectBounds().centerY())

        val drawMatrix = Matrix()
        drawMatrix.setRotate(subRotate, rect2.centerX(), rect2.centerY())
        drawMatrix.postSkew(subSkewX, subSkewY, rect2.centerX(), rect2.centerY())
        canvas.withMatrix(drawMatrix) {
            paint.color = Color.RED
            drawRect(rect2, paint)
        }*/
    }

    fun draw4Sub(canvas: Canvas) {
        val color = Color.RED
        val matrix = subMatrix()

        val rect = RectF(subRect)

        /*
        //先缩放, 后倾斜, 倾斜不会影响之前的缩放值
        val drawMatrix = Matrix()
        drawMatrix.setScale(matrix.getScaleX(), matrix.getScaleY())
        drawMatrix.postSkew(matrix.getSkewX(), matrix.getSkewY())

        //先倾斜, 后缩放, 缩放会影响之前的倾斜值
        val drawMatrix2 = Matrix()
        drawMatrix2.setSkew(matrix.getSkewX(), matrix.getSkewY())
        drawMatrix2.postScale(matrix.getScaleX(), matrix.getScaleY())*/

        L.d()

        /*val rotateMatrix = rotateMatrix(matrix.getRotateDegrees())
        val translateMatrix = Matrix()
        translateMatrix.setTranslate(matrix.getTranslateX(), matrix.getTranslateY())
        val noRotateMatrix = matrix.removeMatrix(rotateMatrix)

        val rect = RectF(subRect)
        val drawMatrix = matrix.removeMatrix(translateMatrix)
        canvas.withMatrix(drawMatrix) {
            paint.color = color
            drawRect(rect, paint)
        }*/

        //rect.scale(noRotateMatrix.getScaleX(), noRotateMatrix.getScaleY())
        /*noRotateMatrix.mapRect(rect)
        rect.offsetCenterTo(targetRectBounds().centerX(), targetRectBounds().centerY())
        canvas.withRotation(0f, rect.centerX(), rect.centerY()) {
            paint.color = color
            drawRect(rect, paint)
        }*/

        /*val drawMatrix = Matrix()
        drawMatrix.updateScale(noRotateMatrix.getScaleX(), noRotateMatrix.getScaleY())
        drawMatrix.updateSkew(noRotateMatrix.getSkewX(), noRotateMatrix.getSkewY())
        //rect.offsetCenterTo(targetRectBounds().centerX(), targetRectBounds().centerY())
        canvas.withMatrix(drawMatrix) {
            paint.color = color
            drawRect(rect, paint)
        }*/

        /*noRotateMatrix.updateTranslate(0f, 0f)
        val rect = RectF(subRect)
        rect.offsetCenterTo(targetRectBounds().centerX(), targetRectBounds().centerY())
        canvas.withMatrix(noRotateMatrix) {
            paint.color = color
            drawRect(rect, paint)
        }

        rotateMatrix.postConcat(noRotateMatrix)
        canvas.withMatrix(rotateMatrix) {
            paint.color = color
            drawRect(rect, paint)
        }*/
    }

    fun draw3SubRotate(canvas: Canvas) {
        val color = Color.GREEN
        val rect = RectF(subRect)
        subMatrix().mapRect(rect)

        val drawRect = RectF(subRect)
        drawRect.scale(groupScaleX, groupScaleY, subRect.centerX(), subRect.centerY())

        drawRect.offsetCenterTo(rect.centerX(), rect.centerY())

        canvas.withRotation(subRotate, drawRect.centerX(), drawRect.centerY()) {
            paint.color = color
            drawRect(drawRect, paint)
        }

        //val matrix = Matrix()
        //matrix.setRotate(subRotate, subRect.centerX(), subRect.centerY())
        //matrix.postScale(groupScaleX, groupScaleY, subRect.centerX(), subRect.centerY())

        //val groupMatrix = groupMatrix()
        //groupMatrix.updateSkew()
        //matrix.postConcat(groupMatrix)

        /*val path = Path()
        path.addRect(subRect, Path.Direction.CW)
        path.transform(matrix)*/

        //rect.scale(groupScaleX, groupScaleY)

        /*canvas.withMatrix(matrix) {
            paint.color = color
            //drawPath(path, paint)
            drawRect(rect, paint)
        }*/
    }

    fun draw3SubSkew(canvas: Canvas) {
        val color = Color.BLUE
        val rect = RectF(subRect)

        val subDrawMatrix = subMatrix()
        subDrawMatrix.mapRect(rect)

        val drawRect = RectF(subRect)
        drawRect.scale(
            subDrawMatrix.getScaleX(),
            subDrawMatrix.getScaleY(),
            subRect.centerX(),
            subRect.centerY()
        )

        drawRect.offsetCenterTo(rect.centerX(), rect.centerY())

        /*canvas.withRotation(subRotate, drawRect.centerX(), drawRect.centerY()) {
            paint.color = color
            drawRect(drawRect, paint)
        }*/

        val m = Matrix()
        m.setSkew(
            subDrawMatrix.getSkewX(),
            subDrawMatrix.getSkewY(),
            drawRect.centerX(),
            drawRect.centerY()
        )
        canvas.withMatrix(m) {
            paint.color = color
            drawRect(drawRect, paint)
        }
    }

    //---

    /**绘制组*/
    fun draw2Group(canvas: Canvas) {
        val drawRect = RectF(groupRect)
        val color = Color.WHITE

        drawRect.scale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        canvas.withRotation(groupRotate, drawRect.centerX(), drawRect.centerY()) {
            paint.color = color
            drawRect(drawRect, paint)
        }
    }

    /**绘制Sub*/
    fun draw2Sub(canvas: Canvas) {
        val drawRect = RectF(subRect)
        val color = Color.BLACK

        val subMatrix = Matrix()
        subMatrix.setRotate(subRotate, subRect.centerX(), subRect.centerY())
        val groupMatrix = Matrix()
        //groupMatrix.setRotate(groupRotate, groupRect.centerX(), groupRect.centerY())
        //groupMatrix.postScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        groupMatrix.setScale(groupScaleX, groupScaleY, anchorPoint.x, anchorPoint.y)
        groupMatrix.postRotate(groupRotate, groupRect.centerX(), groupRect.centerY())

        val resultMatrix = Matrix(subMatrix)
        resultMatrix.postConcat(groupMatrix)
        canvas.withMatrix(resultMatrix) {
            paint.color = color
            drawRect(drawRect, paint)
        }

        /*canvas.withRotation(subRotate, subRect.centerX(), subRect.centerY()) {
            paint.color = color
            drawRect(drawRect, paint)
        }*/

        //---

        val testMatrix = Matrix()
        testMatrix.updateSkew(
            resultMatrix.getSkewX(),
            resultMatrix.getSkewY()
        )
        testMatrix.updateScale(
            resultMatrix.getScaleX(),
            resultMatrix.getScaleY()
        )

        val testMatrix2 = Matrix()
        testMatrix2.setRotate(subRotate, subRect.centerX(), subRect.centerY())

        val testMatrix3 = Matrix()
        testMatrix3.setRotate(subRotate)

        canvas.withMatrix(testMatrix) {
            paint.color = color
            drawRect(drawRect, paint)
        }

        canvas.withMatrix(testMatrix2) {
            paint.color = color
            drawRect(drawRect, paint)
        }

        //---

        val path = Path()
        path.addRect(drawRect, Path.Direction.CW)
        path.transform(testMatrix)
        canvas.withSave {
            paint.color = color
            drawPath(path, paint)
        }

        //--

        drawRect.scale(
            resultMatrix.getScaleX(),
            resultMatrix.getScaleY(),
            subRect.centerX(),
            subRect.centerY()
        )
        val testMatrix4 = Matrix()
        testMatrix4.updateSkew(
            resultMatrix.getSkewX(),
            resultMatrix.getSkewY()
        )

        canvas.withMatrix(testMatrix4) {
            paint.color = color
            drawRect(drawRect, paint)
        }
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

    fun drawTest3(canvas: Canvas) {
        val rect = RectF(100f, 100f, 300f, 300f)

        val scaleX = 1.5f
        val scaleY = 1.2f
        val scaleRect = rect.scale(scaleX, scaleY)

        val color = Color.BLACK
        canvas.withSave {
            paint.color = color
            drawRect(rect, paint)
            drawRect(scaleRect, paint)

            val skewMatrix = Matrix()
            skewMatrix.setSkew(subSkewX, subSkewY, scaleRect.centerX(), scaleRect.centerY())
            withMatrix(skewMatrix) {
                drawRect(scaleRect, paint)
            }
        }
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

    //---

    /**测试缩放矩阵
     * 结论: 不指定锚点时, 默认就是0f 0f的锚点
     *       缩放操作, 本质就是更改了sx sy
     *       不同的锚点, 不影响sx sy, 只影响tx ty
     * */
    fun testScaleMatrix() {
        val m = Matrix()
        val m1 = Matrix()
        val m2 = Matrix()
        val m3 = Matrix()
        val m4 = Matrix()
        val m5 = Matrix()

        m.setScale(0.5f, 0.3f)
        m1.setScale(0.5f, 0.3f, 0f, 0f)
        m2.setScale(0.5f, 0.3f, 50f, 50f)
        m3.setScale(0.5f, 0.3f, 100f, 100f)

        m4.postScale(0.1f, 0.1f)
        m4.postScale(0.2f, 0.2f)
        m4.postScale(0.3f, 0.3f)
        m4.postScale(0.4f, 0.4f)
        m4.postScale(0.5f, 0.5f)

        //postScale scale的值会进行相乘
        m5.postScale(0.1f, 0f)
        m5.postScale(0.2f, 0f)
        m5.postScale(0.3f, 0f)
        m5.postScale(0.4f, 0f)
        m5.postScale(0.5f, 0f)
        L.d()
    }

    /**测试旋转矩阵
     * 结论: 不指定锚点时, 默认就是0f 0f的锚点
     *       旋转操作, 本质就是更改了sx sy和kx ky
     *       不同的锚点, 不影响sx sy/kx ky, 只影响tx ty
     * */
    fun testRotateMatrix() {
        val m = Matrix()
        val m1 = Matrix()
        val m2 = Matrix()
        val m3 = Matrix()

        m.setRotate(30f)
        m.getRotateDegrees()
        m1.setRotate(30f, 0f, 0f)
        m2.setRotate(30f, 50f, 50f)
        m3.setRotate(30f, 100f, 100f)

        L.d()
    }

    /**测试旋转缩放/缩放旋转矩阵
     * 结论:  操作本质就是更改了sx sy和kx ky
     *       不管是先旋转后缩放/先缩放后旋转sx sy和kx ky都一样
     *       不同的锚点, 不影响sx sy/kx ky, 只影响tx ty
     * */
    fun testRotateScaleMatrix() {
        val m = Matrix()
        val m1 = Matrix()
        val m2 = Matrix()
        val m3 = Matrix()
        val m4 = Matrix()

        m.setRotate(30f)
        m.postScale(0.5f, 0.5f)
        m1.setScale(0.5f, 0.5f)
        m1.postRotate(30f)

        m2.setRotate(30f, 50f, 50f)
        m2.postScale(0.5f, 0.5f, 50f, 50f)
        m3.setScale(0.5f, 0.5f, 50f, 50f)
        m3.postRotate(30f, 50f, 50f)

        m4.setRotate(30f)
        m4.postScale(0.5f, 0.5f, 50f, 50f)

        L.d()
    }

    fun testSkewMatrix() {
        val m = Matrix()
        val m1 = Matrix()
        val m2 = Matrix()
        val m3 = Matrix()
        val m4 = Matrix()
        val m5 = Matrix()
        val m6 = Matrix()

        m.setRotate(30f)

        m1.set(m)
        m2.set(m)
        m3.set(m)
        m4.set(m)
        //m5.set(m)

        m1.postSkew(1f, 1f)
        m2.postSkew(1f, 1f, 50f, 50f)
        m3.postSkew(1f, 1f, 100f, 100f)

        m4.postSkew(0.5f, 0.5f, 100f, 100f)

        m5.postSkew(0.1f, 0f)
        m5.postSkew(0.2f, 0.2f)
        m5.postSkew(0.3f, 0.3f)
        m5.postSkew(0.4f, 0.4f)
        m5.postSkew(0.5f, 0.5f)

        //多次postSkew skew的值会进行相加
        m6.postSkew(0.1f, 0f)
        m6.postSkew(0.2f, 0f)
        m6.postSkew(0.3f, 0f)
        m6.postSkew(0.4f, 0f)
        m6.postSkew(0.5f, 0f)

        L.d()
    }

    fun testSaveVaule(canvas: Canvas, matrix: Matrix) {
        val angle = atan2(matrix.getSkewY(), matrix.getScaleX()).toDegrees()
        val denom = pow(matrix.getScaleX().toDouble(), 2.0) +
                pow(matrix.getSkewY().toDouble(), 2.0)
        val sx = sqrt(denom)
        val sy =
            (matrix.getScaleX() * matrix.getScaleY() - matrix.getSkewX() * matrix.getSkewY()) / sx
        val kx = Math.atan2(
            (matrix.getScaleX() * matrix.getSkewX() + matrix.getSkewY() * matrix.getScaleY()).toDouble(),
            denom
        )//x倾斜的角度, 弧度单位
        val ky = 0.0//y倾斜的角度, 弧度单位

        //---

        val rotateMatrix = rotateMatrix(angle)
        val newMatrix = matrix.removeMatrix(rotateMatrix)

        val t = Matrix(rotateMatrix)
        t.postConcat(newMatrix)

        //---

        val color = Color.RED
        val rect = RectF(subRect)
        val rectMatrix = Matrix()

        val rect2 = RectF(subRect)
        rectMatrix.setScale(sx.toFloat(), sy.toFloat(), rect.left, rect.top)
        rectMatrix.mapRect(rect2)

        val rect3 = RectF(subRect)
        rectMatrix.postSkew(tan(kx).toFloat(), tan(ky).toFloat(), rect.left, rect.top)
        rectMatrix.mapRect(rect3)

        //rectMatrix.setRotate(angle, rect.centerX(), rect.centerY())
        //rectMatrix.postScale(sx.toFloat(), sy.toFloat(), rect.centerX(), rect.centerY())
        //rectMatrix.postSkew(kx.toFloat(), ky.toFloat(), rect.centerX(), rect.centerY())

        /*rectMatrix.setScale(sx.toFloat(), sy.toFloat(), rect.centerX(), rect.centerY())
        //rectMatrix.postSkew(kx.toFloat(), ky.toFloat(), rect.left, rect.top)
        rectMatrix.postSkew(tan(kx).toFloat(), ky.toFloat(), rect.centerX(), rect.centerY())*/

        //rect.offsetCenterTo(targetRectBounds().centerX(), targetRectBounds().centerY())

        //rect.scale(sx.toFloat(), sy.toFloat())

        val skewRect = RectF(rect)
        rectMatrix.mapRect(skewRect)

        val path = Path()
        path.addRect(rect, Path.Direction.CW)
        //val skewMatrix = Matrix()
        //skewMatrix.setSkew(kx.toFloat(), ky.toFloat())
        path.transform(rectMatrix)

        val targetRectBounds = targetRectBounds()
        val centerPath = Path()
        centerPath.moveTo(0f, skewRect.centerY())
        centerPath.lineTo(measuredWidth.toFloat(), skewRect.centerY())
        centerPath.moveTo(skewRect.centerX(), 0f)
        centerPath.lineTo(skewRect.centerX(), measuredHeight.toFloat())

        canvas.withTranslation(0f, 0f) {
            /*targetRectBounds.centerX() - skewRect.centerX(),
            targetRectBounds.centerY() - skewRect.centerY()*/

            canvas.withSave {
                paint.color = Color.YELLOW
                drawPath(centerPath, paint)
            }

            canvas.withRotation(
                0f,
                skewRect.centerX(),
                skewRect.centerY()
            ) {

                paint.color = color
                //drawRect(rect, paint)
                drawPath(path, paint)

                canvas.withSave {
                    paint.color = Color.YELLOW
                    drawRect(skewRect, paint)
                }
            }
        }

        /*val testRect = RectF(subRect)
        val testMatrix = Matrix()
        testMatrix.setRotate(angle, testRect.left, testRect.top)

        val testMatrix2 = Matrix()
        testMatrix2.setScale(sx.toFloat(), sy.toFloat(), testRect.left, testRect.top)
        testMatrix2.postSkew(
            tan(kx).toFloat(),
            tan(ky).toFloat(),
            testRect.left, testRect.top
        )

        testMatrix.postConcat(testMatrix2)
        //testMatrix2.postConcat(testMatrix)

        canvas.withTranslation(0f, 0f) {
            canvas.withMatrix(testMatrix) {
                paint.color = Color.YELLOW
                drawRect(testRect, paint)
            }
        }*/

        L.i()
    }

    fun testRect() {
        val rect = RectF(0f, 0f, 100f, 0f)
        val result = RectF()
        val matrix = Matrix()
        matrix.setRotate(30f)
        matrix.mapRect(result, rect)

        matrix.setScale(3f, 3f)
        matrix.mapRect(result, rect)

        L.d(rect)
    }
}