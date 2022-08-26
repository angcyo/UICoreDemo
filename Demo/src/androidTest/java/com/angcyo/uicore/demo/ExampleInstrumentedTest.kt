package com.angcyo.uicore.demo

import android.graphics.Matrix
import android.graphics.Path
import android.graphics.PathMeasure
import android.graphics.RectF
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.angcyo.canvas.core.BoundsOperateHandler
import com.angcyo.library.L
import com.angcyo.library.ex.eachPath
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.angcyo.uicore.demo", appContext.packageName)
    }

    @Test
    fun testOvalIntersect() {
        //测试矩形是否在椭圆范围内
    }

    @Test
    fun testMaxRect() {
        //测试矩形在椭圆内的最大矩形
    }

    @Test
    fun testRotateBounds() {
        val bounds = RectF(0f, 0f, 100f, 100f)

        val matrix = Matrix()
        val rotate = 30f
        matrix.postRotate(rotate, bounds.centerX(), bounds.centerY())

        val rotateBounds = RectF(bounds)
        matrix.mapRect(rotateBounds)
        L.i("\n矩形:$bounds\n旋转:$rotateBounds")

        //调整旋转后的矩形高度
        val rotateBoundsAfter = RectF(rotateBounds)
        rotateBoundsAfter.bottom += 10f //高度增加10f

        //------
        val resultBounds = RectF(bounds)
        val resultRotateBounds = RectF()

        /*//不行
        val scale = rotateBoundsAfter.height() / rotateBounds.height()
        resultBounds.bottom = resultBounds.height() * scale
        resultRotateBounds.set(resultBounds)*/

        BoundsOperateHandler().calcBoundsWidthHeightWithFrame(
            bounds,
            rotateBounds,
            rotateBoundsAfter,
            rotate,
            true
        ).apply {
            resultBounds.right = this[0]
            resultBounds.bottom = this[1]

            resultRotateBounds.set(resultBounds)
        }

        matrix.mapRect(resultRotateBounds)
        L.i("\n->矩形:$resultBounds\n->旋转:$resultRotateBounds\n->${rotateBounds.height()} ${rotateBoundsAfter.height()} ${resultRotateBounds.height()}")
        L.w("->${rotateBoundsAfter.height() == resultRotateBounds.height()}")
    }

    @Test
    fun testPath() {
        val path = Path()
        path.moveTo(0f, 0f)
        path.lineTo(10f, 0f)

        val pathMeasure = PathMeasure(path, false)
        println(pathMeasure.length)
        //path.toBitmap()
        path.eachPath { index, ratio, posArray ->
            println("$index -> ${posArray[0]} ${posArray[1]}")
        }

        println("---")

        val path2 = Path()
        path2.moveTo(0f, 0f)
        path2.lineTo(10f, 0f)
        path2.moveTo(10f, 10f)
        path2.lineTo(20f, 20f)

        val pathMeasure2 = PathMeasure(path2, true)
        println(pathMeasure2.length)
        //path2.toBitmap()
        path2.eachPath { index, ratio, posArray ->
            println("$index -> ${posArray[0]} ${posArray[1]}")
        }

        println("---")
    }
}
