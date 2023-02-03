package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withRotation
import com.angcyo.canvas.data.CanvasProperty
import com.angcyo.canvas.utils.createPaint
import com.angcyo.library.ex.*
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

    val subRect = RectF()
    val groupRect = RectF()

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
    }

    var _subMatrix: Matrix? = null

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        _subMatrix?.let { canvas.drawSub(Color.YELLOW, it) }
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
        matrix.setScale(subScaleX, subScaleY, scaleRect.left, scaleRect.top)
        matrix.postSkew(
            tan(subSkewX.toRadians()),
            tan(subSkewY.toRadians()),
            scaleRect.left,
            scaleRect.top
        )
        matrix.mapRect(scaleRect)
        if (rotate) {
            matrix.postRotate(subRotate, scaleRect.centerX(), scaleRect.centerY())
        }
        return matrix
    }

    /**将[matrix]拆解成[CanvasProperty]*/
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

        withRotation(groupRotate, rect.centerX(), rect.centerY()) {
            paint.color = color
            drawRect(rect, paint)
        }
    }

    /**绘制sub*/
    fun Canvas.drawSub(color: Int = Color.BLACK, matrix: Matrix = subDataMatrix()) {
        val rect = RectF(subRect)
        withMatrix(matrix) {
            paint.color = color
            drawRect(rect, paint)
        }
    }

    /**绘制sub使用, 修改数据, 然后旋转的方式绘制*/
    fun Canvas.drawSubPath(color: Int = Color.BLACK) {
        val rect = RectF(subRect)
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
    fun Canvas.drawSubPath(color: Int, property: CanvasProperty) {
        val rect = RectF(subRect)
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

    //---

    /**旋转组到指定的角度*/
    fun rotateGroupTo(rotate: Float) {
        val sub = RectF(subRect)
        val group = RectF(groupRect)

        val dr = rotate - groupRotate

        val subMatrix = subDataMatrix()
        val groupMatrix = Matrix() //groupMatrix(dr)
        groupMatrix.setRotate(dr, group.centerX(), group.centerY())

        subMatrix.postConcat(groupMatrix)

        _subMatrix = subMatrix

        subRotate += dr
        groupRotate = rotate
    }

}