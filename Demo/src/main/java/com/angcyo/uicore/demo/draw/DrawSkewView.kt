package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withRotation
import androidx.core.graphics.withSave
import androidx.core.graphics.withSkew
import com.angcyo.canvas.utils.createPaint
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
    var groupRotate = 0f
        set(value) {
            field = value
            subRotate += value
        }

    var groupScaleX = 1f
    var groupScaleY = 1f

    var subSkewX = 0f
    var subSkewY = 0f

    val subMatrix = Matrix()
    val subMatrix2 = Matrix()

    val paint = createPaint()

    init {
        groupRotate = 0f
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val centerX = w / 2
        val centerY = h / 2

        val width = 100 * dpi
        val height = width

        anchorPoint.set(centerX - 2f * width, centerY - 2f * height)
//        anchorPoint.set(0f, 0f)

        subRect.set(
            centerX - width / 2f,
            centerY - height / 2f,
            centerX + width / 2f,
            centerY + height / 2f
        )

        groupRect.set(
            minOf(anchorPoint.x, subRect.left),
            minOf(anchorPoint.y, subRect.top),
            maxOf(anchorPoint.x, subRect.right),
            maxOf(anchorPoint.y, subRect.bottom),
        )
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        drawOriginRect(canvas)
        //drawTest1(canvas)

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