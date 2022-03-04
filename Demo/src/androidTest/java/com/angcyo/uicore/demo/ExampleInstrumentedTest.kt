package com.angcyo.uicore.demo

import android.graphics.Path
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
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
}
