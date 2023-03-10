package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withRotation
import androidx.core.graphics.withTranslation
import com.angcyo.widget.base.createPaint
import com.angcyo.library.component.pool.release
import com.angcyo.library.ex.*
import com.angcyo.library.ex.isTouchDown
import com.angcyo.uicore.component.CanvasProperty
import com.angcyo.uicore.component.CanvasRectProperty
import java.lang.Math.pow
import kotlin.math.atan2
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/02/01
 */
open class BaseMatrixView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet), IMatrixView {

    override var groupRotate: Float = 0f
        set(value) {
            field = value % 360
            invalidate()
        }

    override var subRotate: Float = 0f
        set(value) {
            field = value % 360
            invalidate()
        }

    override var groupScaleX: Float = 1f
        set(value) {
            field = value
            invalidate()
        }
    override var groupScaleY: Float = 1f
        set(value) {
            field = value
            invalidate()
        }
    override var subScaleX: Float = 1f
        set(value) {
            field = value
            invalidate()
        }
    override var subScaleY: Float = 1f
        set(value) {
            field = value
            invalidate()
        }

    override var subSkewX: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    override var subSkewY: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    //---

    val paint = createPaint().apply {
        strokeWidth = 1 * dp
    }

    val anchorPoint = PointF()
    val subAnchorPoint = PointF(0f, 0f)

    val subRect = RectF()
    val groupRect = RectF()

    //---

    var groupRectProperty = CanvasRectProperty()

    var subRectProperty = CanvasRectProperty()

    init {
        groupRotate = 0f
        subRotate = 30f
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

        resetGroupRectProperty()
        resetSubRectProperty()
    }

    fun resetSubRectProperty() {
        subRectProperty = CanvasRectProperty()
        subRectProperty.initWithRect(subRect)
        subRectProperty.angle = subRotate
        subRectProperty.scaleX = subScaleX
        subRectProperty.scaleY = subScaleY
        subRectProperty.skewX = subSkewX
        subRectProperty.skewY = subSkewY
    }

    fun resetGroupRectProperty() {
        groupRectProperty = CanvasRectProperty()
        groupRectProperty.initWithRect(groupRect)
        groupRectProperty.angle = groupRotate
    }

    var _subMatrix: Matrix? = null

    var testDrawAction: Canvas.() -> Unit = {}
    var nextDrawAction: Canvas.() -> Unit = {}
    var nextDrawAction2: Canvas.() -> Unit = {}

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        //
        nextDrawAction(canvas)
        nextDrawAction2(canvas)
        testDrawAction(canvas)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        _subMatrix?.let { canvas.drawSub(Color.YELLOW, it) }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.isTouchDown()) {
            invalidate()
        }
        return super.onTouchEvent(event)
    }

    /**更新Group包裹范围*/
    fun updateGroupRect() {
        val scaleRect = RectF(subRect)
        val matrix = subDataMatrix(true)
        matrix.mapRect(scaleRect)
        groupRect.set(
            minOf(anchorPoint.x, scaleRect.left),
            minOf(anchorPoint.y, scaleRect.top),
            maxOf(anchorPoint.x, scaleRect.right),
            maxOf(anchorPoint.y, scaleRect.bottom)
        )
        invalidate()
    }

    //---

    fun updateSubAnchor(x: Float = subAnchorPoint.x, y: Float = subAnchorPoint.y) {
        subAnchorPoint.x = x
        subAnchorPoint.y = y
    }

    fun rotateMatrix(rotate: Float): Matrix {
        val matrix = Matrix()
        matrix.setRotate(rotate)
        return matrix
    }

    /**group指定旋转角度的矩阵*/
    fun groupMatrix(rotate: Float = groupRotate): Matrix {
        val scaleRect = RectF(groupRect)
        val matrix = Matrix()
        matrix.setScale(groupScaleX, groupScaleY, scaleRect.left, scaleRect.top)
        matrix.mapRect(scaleRect)
        matrix.postRotate(rotate, scaleRect.centerX(), scaleRect.centerY())
        return matrix
    }

    /**sub数据矩阵或者可以直接绘制的矩阵*/
    fun subDataMatrix(rotate: Boolean = true): Matrix {
        val scaleRect = RectF(subRect)
        val matrix = Matrix()
        val px = scaleRect.centerX()
        val py = scaleRect.centerY()
        matrix.setSkew(
            tan(subSkewX.toRadians()),
            tan(subSkewY.toRadians()),
            px, py
        )
        matrix.postScale(subScaleX, subScaleY, px, py)
        if (rotate) {
            matrix.postRotate(subRotate, px, py)
        }
        return matrix
    }

    fun canvasPropertyMatrix(rect: RectF, property: CanvasProperty): Matrix {
        val scaleRect = RectF(rect)
        val matrix = Matrix()

        //锚点不影响效果, 只影响偏移量
        val anchor = PointF(scaleRect.centerX(), scaleRect.centerY())
        //val anchor = PointF(scaleRect.left, scaleRect.top)
        /*matrix.setScale(property.scaleX, property.scaleY, anchor.x, anchor.y)
        matrix.postSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            anchor.x, anchor.y
        )
        //matrix.postRotate(property.angle, anchor.x, anchor.y)

        *//*matrix.mapRect(scaleRect)
        matrix.postSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            scaleRect.centerX(), scaleRect.centerY()
        )*//*
        //matrix.postRotate(property.angle, scaleRect.centerX(), scaleRect.centerY())*/

        //matrix.setRotate(property.angle, anchor.x, anchor.y)
        /*matrix.postScale(property.scaleX, property.scaleY, anchor.x, anchor.y)
        *//*matrix.postSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            anchor.x, anchor.y
        )*//*
        matrix.mapRect(scaleRect)
        matrix.postRotate(property.angle, scaleRect.centerX(), scaleRect.centerY())
        matrix.postSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            scaleRect.centerX(), scaleRect.centerY()
        )*/


        //√, 使用pre
        /*matrix.setRotate(property.angle, anchor.x, anchor.y)
        matrix.preScale(property.scaleX, property.scaleY, anchor.x, anchor.y)
        matrix.preSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            anchor.x, anchor.y
        )*/

        //×
        /*matrix.postSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            anchor.x, anchor.y
        )
        matrix.preScale(property.scaleX, property.scaleY, anchor.x, anchor.y)*/

        //√, 使用post, 直接把所有属性放在一个矩阵中, 然后直接绘制rect
        /*matrix.setSkew(
             tan(property.skewX.toRadians()),
             tan(property.skewY.toRadians()),
             anchor.x, anchor.y
         )
         matrix.postScale(property.scaleX, property.scaleY, anchor.x, anchor.y)
         matrix.postRotate(property.angle, anchor.x, anchor.y)*/

        //×, 一定要先进行skew, 在scale, 最后rotate, 因为scale操作会影响skew的值, 但是skew操作, 不会响应scale的值
        /*matrix.setSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            anchor.x, anchor.y
        )
        matrix.postRotate(property.angle, anchor.x, anchor.y)
        matrix.postScale(property.scaleX, property.scaleY, anchor.x, anchor.y)*/

        //√, 先post 再pre 代码顺序不影响计算顺序, [pre × post] 执行顺序与代码顺序无关
        matrix.postScale(property.scaleX, property.scaleY, anchor.x, anchor.y)
        matrix.preSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            anchor.x, anchor.y
        )//之后绘制的时候, 再处理按照中心点旋转withRotation

        return matrix
    }

    /**将[matrix]拆解成[CanvasProperty]
     * QR分解
     * https://ristohinno.medium.com/qr-decomposition-903e8c61eaab
     *
     * https://zh.wikipedia.org/zh-hans/QR%E5%88%86%E8%A7%A3
     *
     * https://rosettacode.org/wiki/QR_decomposition#Java
     * */
    fun getSaveCanvasProperty(matrix: Matrix): CanvasProperty {
        val property = CanvasProperty()
        val angle = atan2(matrix.getSkewY(), matrix.getScaleX()).toDegrees()
        val denom = pow(matrix.getScaleX().toDouble(), 2.0) +
                pow(matrix.getSkewY().toDouble(), 2.0)

        val scaleX = sqrt(denom)
        val scaleY = (matrix.getScaleX() * matrix.getScaleY() -
                matrix.getSkewX() * matrix.getSkewY()) / scaleX

        val skewX = atan2(
            (matrix.getScaleX() * matrix.getSkewX() +
                    matrix.getSkewY() * matrix.getScaleY()).toDouble(),
            denom
        ) //x倾斜的角度, 弧度单位
        val skewY = 0.0f//y倾斜的角度, 弧度单位

        property.angle = angle
        property.scaleX = scaleX.toFloat()
        property.scaleY = scaleY.toFloat()
        property.skewX = skewX.toDegrees().toFloat()
        property.skewY = skewY

        return property
    }

    //---

    /**绘制group*/
    fun Canvas.drawGroup(color: Int = Color.WHITE) {
        val rect = RectF(groupRect)
        val matrix = groupMatrix(0f)
        matrix.mapRect(rect)
        withRotation(groupRotate, rect.centerX(), rect.centerY()) {
            paint.color = color
            drawRect(rect, paint)
        }
    }

    /**绘制sub*/
    fun Canvas.drawSub(
        color: Int = Color.BLACK,
        matrix: Matrix = subDataMatrix(),
        rect: RectF = RectF(subRect)
    ) {
        withMatrix(matrix) {
            paint.color = color
            drawRect(rect, paint)
        }
    }

    /**绘制sub使用, 修改数据, 然后旋转的方式绘制*/
    fun Canvas.drawSubPath(color: Int = Color.BLACK, rect: RectF = RectF(subRect)) {
        val matrix = subDataMatrix(false)

        val path = Path()
        path.addRect(rect, Path.Direction.CW)
        path.transform(matrix)

        matrix.mapRect(rect)

        withRotation(subRotate, rect.centerX(), rect.centerY()) {
            paint.color = color
            drawPath(path, paint)
        }
    }

    /**绘制sub使用, 恢复后的属性*/
    fun Canvas.drawSubPath(color: Int, property: CanvasProperty, rect: RectF = RectF(subRect)) {
        val matrix = Matrix()
        matrix.setScale(property.scaleX, property.scaleY, rect.left, rect.top)
        matrix.postSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            rect.left,
            rect.top
        )

        val path = Path()
        path.addRect(rect, Path.Direction.CW)
        path.transform(matrix)

        matrix.mapRect(rect)

        withRotation(property.angle, rect.centerX(), rect.centerY()) {
            paint.color = color
            drawPath(path, paint)
        }
    }

    fun Canvas.drawRectProperty(color: Int, property: CanvasRectProperty) {
        val path = Path()
        val rect = property.getRect()
        val matrix = property.getMatrix(false)

        path.addRect(rect, Path.Direction.CW)
        path.transform(matrix)

        withRotation(property.angle, rect.centerX(), rect.centerY()) {
            //data
            paint.color = color
            drawPath(path, paint)
            //bounds
            matrix.mapRect(rect)
            paint.color = Color.WHITE
            drawRect(rect, paint)
            rect.release()
        }
    }

    //---

    /**缩放Sub到指定比例*/
    fun scaleSubTo(sx: Float = subScaleX, sy: Float = subScaleY) {
        subScaleX = sx
        subScaleY = sy
        updateGroupRect()

        subRectProperty.scaleTo(sx, sy)
    }

    /**错切/倾斜Sub到指定比例*/
    fun skewSubTo(kx: Float = subSkewX, ky: Float = subSkewY) {
        subSkewX = kx
        subSkewY = ky
        updateGroupRect()

        subRectProperty.skewTo(kx, ky)
    }

    /**旋转Group到指定的度数, 角度单位*/
    fun rotateGroupTo(rotate: Float) {
        groupRotate = rotate
        val group = RectF(groupRect)
        val groupMatrix = groupMatrix()
        groupMatrix.mapRect(group)

        val anchorX = group.centerX()
        val anchorY = group.centerY()

        resetSubRectProperty()
        subRectProperty.wrapScale(groupScaleX, groupScaleY, groupRect.left, groupRect.top)
        subRectProperty.wrapRotate(rotate, anchorX, anchorY)
        invalidate()
    }

    fun subRotateTo(rotate: Float) {
        subRotate = rotate
        subRectProperty.angle = rotate
    }

    /**缩放Group到指定比例*/
    fun scaleGroupTo(sx: Float = groupScaleX, sy: Float = groupScaleY) {
        val sub = RectF(subRect)
        val group = RectF(groupRect)
        val anchor = PointF(group.left, group.top)
        val subBounds = RectF(subRect)

        val subMatrix = subDataMatrix(true)
        subMatrix.mapRect(subBounds)
        groupScaleX = sx
        groupScaleY = sy
        val groupMatrix = groupMatrix()
        subMatrix.postConcat(groupMatrix)

        val subBounds2 = RectF(subBounds)
        subMatrix.mapRect(subBounds2)

        val property = getSaveCanvasProperty(subMatrix)
        nextDrawAction = {
            withMatrix(subMatrix) {
                paint.color = Color.RED
                drawRect(sub, paint)
            }
            /*drawSubPath(
                Color.YELLOW,
                property
                *//*,sub.offsetCenterTo(subBounds2.centerX(), subBounds2.centerY())*//*
            )*/

            /*withMatrix(canvasPropertyMatrix(subRect, property)) {
                paint.color = Color.YELLOW
                drawRect(sub, paint)
            }*/

            val canvasPropertyMatrix = canvasPropertyMatrix(subRect, property)
            /*val rect = RectF(sub)
            withMatrix(canvasPropertyMatrix) {
                paint.color = Color.YELLOW
                drawRect(rect, paint)
            }*/

            val rect = RectF(sub)
            canvasPropertyMatrix.mapRect(rect)

            val tx = subBounds2.centerX() - rect.centerX()
            val ty = subBounds2.centerY() - rect.centerY()
            withTranslation(tx, ty) {
                withRotation(property.angle, sub.centerX(), sub.centerY()) {
                    val path = Path()
                    path.addRect(sub, Path.Direction.CW)
                    path.transform(canvasPropertyMatrix)
                    paint.color = Color.YELLOW
                    drawPath(path, paint)

                    paint.color = Color.WHITE
                    drawRect(rect, paint)
                }
            }
        }
        nextDrawAction2 = {
            /*paint.color = Color.GREEN
            drawRect(subBounds, paint)
            paint.color = Color.BLUE
            drawRect(subBounds2, paint)*/
        }
    }

    fun scaleGroupTo2(sx: Float = groupScaleX, sy: Float = groupScaleY) {
        val group = RectF(groupRect)

        val anchorX = group.left
        val anchorY = group.top

        groupScaleX = sx
        groupScaleY = sy

        resetSubRectProperty()
        subRectProperty.wrapScale(sx, sy, anchorX, anchorY)
        invalidate()
    }

}