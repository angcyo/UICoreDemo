package com.angcyo.uicore.demo

import android.graphics.Matrix
import android.graphics.RectF
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.angcyo.canvas.core.OperateHandler
import com.angcyo.library.L
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

        OperateHandler().calcBoundsWidthHeightWithFrame(
            bounds,
            rotateBounds,
            rotateBoundsAfter,
            rotate
        ).apply {
            resultBounds.right = this[0]
            resultBounds.bottom = this[1]

            resultRotateBounds.set(resultBounds)
        }

        matrix.mapRect(resultRotateBounds)
        L.i("\n->矩形:$resultBounds\n->旋转:$resultRotateBounds\n->${rotateBounds.height()} ${rotateBoundsAfter.height()} ${resultRotateBounds.height()}")
        L.w("->${rotateBoundsAfter.height() == resultRotateBounds.height()}")
    }
}
