package com.angcyo.uicore.demo.draw

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.view.View
import com.angcyo.canvas.utils.createPaint

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/15
 */
class DrawPathView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    val pathList = mutableListOf<Path>()

    val paint = createPaint()

    @SuppressLint("DrawAllocation")
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.translate(100f, 100f)

        pathList.forEach {
            paint.color = Color.GRAY
            canvas.drawPath(it, paint)

            val pathBounds = RectF()
            it.computeBounds(pathBounds, true)

            //获取路径上的所有点
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val pointComponents = it.approximate(0.5f)
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
            val pathMeasure = PathMeasure(it, false)
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

}