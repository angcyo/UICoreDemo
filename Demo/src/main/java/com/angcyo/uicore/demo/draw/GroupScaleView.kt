package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withRotation
import com.angcyo.library.L
import com.angcyo.library.ex.toRadians
import com.angcyo.library.unit.toPixel
import com.angcyo.uicore.component.CanvasRectProperty
import kotlin.math.tan


/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/02/01
 */
class GroupScaleView(context: Context, attributeSet: AttributeSet? = null) :
    BaseMatrixView(context, attributeSet) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawGroup()
        //canvas.drawSub(Color.WHITE, subDataMatrix(false))

        canvas.drawSub()
        //canvas.drawSubPath(Color.RED)
        //canvas.drawSubPath(Color.RED, getSaveCanvasProperty(subDataMatrix(true)))

        //canvas.drawRectProperty(Color.RED, subRectProperty)
        //test()
        test2(canvas)

        //testMatrix()
    }

    fun test() {
        val matrix = subDataMatrix(true)
        val property = getSaveCanvasProperty(matrix)

        val scaleRect = RectF(subRect)
        val test = Matrix()
        /*test.setScale(property.scaleX, property.scaleY, scaleRect.left, scaleRect.top)
        test.postSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            scaleRect.left,
            scaleRect.top
        )
        test.mapRect(scaleRect)
        test.postRotate(property.angle, scaleRect.centerX(), scaleRect.centerY())*/

        val anchor = PointF(scaleRect.centerX(), scaleRect.centerY())
        //val anchor = PointF(0f, 0f)
        test.setSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            anchor.x, anchor.y
        )
        test.postScale(
            property.scaleX, property.scaleY,
            anchor.x, anchor.y
        )
        test.mapRect(scaleRect)
        test.postRotate(property.angle, anchor.x, anchor.y)

        testDrawAction = {
            withMatrix(test) {
                paint.color = Color.GREEN
                drawRect(subRect, paint)
            }
            withRotation(property.angle, scaleRect.centerX(), scaleRect.centerY()) {
                paint.color = Color.BLUE
                drawRect(scaleRect, paint)
            }

            val scaleRect2 = RectF(subRect)
            test.mapRect(scaleRect2)
            paint.color = Color.BLACK
            drawRect(scaleRect2, paint)
        }
        L.d()
    }

    fun test2(canvas: Canvas) {
        val property = CanvasRectProperty().apply {
            /*left = 10.098119f.toPixel()
            top = 7.090558f.toPixel()
            width = 26.933332f.toPixel()
            height = 9.2f.toPixel()

            scaleX = 0.5651549f
            scaleY = 0.4007788f
            angle = 69.81055f

            skewX = 51.981655f
            skewY = 0f*/

            left = 2.8f.toPixel()
            top = 0.26666668f.toPixel()
            width = 12.933333f.toPixel()
            height = 10.8f.toPixel()

            scaleX = 1.7841423f
            scaleY = 0.8901337f
            angle = 14.873828f

            skewX = -37.52056f
            skewY = 0f
        }

        val left = 100f
        val top = 100f
        val rect = RectF(left, top, left + property.width, top + property.height)
        val scaleRect = RectF(rect)

        val matrix = Matrix()
        val anchor = PointF(rect.centerX(), rect.centerY())
        //val anchor = PointF(0f, 0f)
        matrix.setSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            anchor.x, anchor.y
        )
        matrix.postScale(
            property.scaleX, property.scaleY,
            anchor.x, anchor.y
        )
        matrix.mapRect(scaleRect)
        matrix.postRotate(property.angle, anchor.x, anchor.y)

        canvas.apply {
            withMatrix(matrix) {
                paint.color = Color.GREEN
                drawRect(rect, paint)
            }
            withRotation(property.angle, scaleRect.centerX(), scaleRect.centerY()) {
                paint.color = Color.BLUE
                drawRect(scaleRect, paint)
            }

            val scaleRect2 = RectF(rect)
            matrix.mapRect(scaleRect2)
            paint.color = Color.BLACK
            drawRect(scaleRect2, paint)
        }

    }

    fun testMatrix() {
        val t = Matrix()
        t.setTranslate(100f, 100f)

        val m = Matrix()
        m.setScale(0.5f, 0.5f, 100f, 100f)

        val m2 = Matrix()
        m2.setScale(0.5f, 0.5f)
        m2.preConcat(t)

        L.d()
    }

}