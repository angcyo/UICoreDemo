package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.RectF
import android.util.AttributeSet
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withRotation
import com.angcyo.library.L
import com.angcyo.library.ex.toRadians
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

        //test()
    }

    fun test() {
        val matrix = subDataMatrix(true)
        val property = getSaveCanvasProperty(matrix)

        val scaleRect = RectF(subRect)
        val test = Matrix()
        test.setScale(property.scaleX, property.scaleY, scaleRect.left, scaleRect.top)
        test.postSkew(
            tan(property.skewX.toRadians()),
            tan(property.skewY.toRadians()),
            scaleRect.left,
            scaleRect.top
        )
        test.mapRect(scaleRect)
        test.postRotate(property.angle, scaleRect.centerX(), scaleRect.centerY())

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
            drawRect(scaleRect2, paint)
        }
        L.d()
    }

}