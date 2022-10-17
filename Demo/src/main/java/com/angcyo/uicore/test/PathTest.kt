package com.angcyo.uicore.test

import android.graphics.Path
import android.graphics.RectF
import com.angcyo.library.L
import com.angcyo.library.utils.sdFolderPath
import kotlin.math.atan
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/04
 */
object PathTest {

    fun test() {
        testOvalIntersect()
        testMaxRectInOval()

        /*val def = sdFolderPath()
        val def2 = sdFolderPath("test")
        L.i(def)
        L.i(def2)*/
    }

    fun testOvalIntersect() {
        //测试矩形是否在椭圆范围内
        val ovalPath = Path().apply {
            addOval(0f, 0f, 100f, 100f, Path.Direction.CW)
        }

        val r1 = Path().apply {
            addRect(10f, 10f, 30f, 30f, Path.Direction.CW)
        }

        val r2 = Path().apply {
            addRect(100f, 100f, 300f, 300f, Path.Direction.CW)
        }

        val r3 = Path().apply {
            addRect(80f, 80f, 300f, 300f, Path.Direction.CW)
        }

        val r4 = Path().apply {
            addRect(50f, 50f, 60f, 60f, Path.Direction.CW)
        }

        //相交 返回:false
        L.w("测试结果1:" + Path().apply {
            op(ovalPath, r1, Path.Op.REVERSE_DIFFERENCE) //从r1路径中, 减去ovalPath路径
        }.isEmpty)

        //相交 返回:false
        L.w("测试结果2:" + Path().apply {
            op(ovalPath, r2, Path.Op.REVERSE_DIFFERENCE)
        }.isEmpty)

        //相交 返回:false
        L.w("测试结果3:" + Path().apply {
            op(ovalPath, r3, Path.Op.REVERSE_DIFFERENCE)
        }.isEmpty)

        //完全包含 返回:true
        L.w("测试结果4:" + Path().apply {
            op(ovalPath, r4, Path.Op.REVERSE_DIFFERENCE)
        }.isEmpty)
    }

    fun testMaxRectInOval() {
        //测试矩形在椭圆内的最大矩形
        val ovalRect = RectF(0f, 0f, 100f, 100f)
        val ovalPath = Path().apply {
            addOval(ovalRect, Path.Direction.CW)
        }
        val inputRect = RectF(40f, 40f, 70f, 50f)
        val maxRect = RectF()
        maxRect.set(inputRect)

        val dSize = if (isRectInOval(ovalPath, maxRect)) {
            -1f
        } else {
            1f
        }

        val widthRectRatio = inputRect.width() / inputRect.height()
        val heightRectRatio = inputRect.height() / inputRect.width()

        val widthPriority =
            (inputRect.width() / ovalRect.width()) > (inputRect.height() / ovalRect.height())

        while (true) {
            if (!isRectInOval(ovalPath, maxRect)) {
                break
            }
            if (widthPriority) {
                maxRect.inset(dSize, dSize / widthRectRatio)
            } else {
                maxRect.inset(dSize / heightRectRatio, dSize)
            }
        }
        L.w("结果1: 原矩形:$inputRect ${inputRect.width()}:${inputRect.height()} 最大矩形:$maxRect ${maxRect.width()}:${maxRect.height()}")

        //方法2
        val cAngle = atan((inputRect.height() / inputRect.width()).toDouble())//斜边的角度, 弧度制

        val a = ovalRect.width() / 2
        val b = ovalRect.height() / 2
        //当前角度在椭圆上的x坐标
        val oX = a * b / sqrt(b * b + a * a * tan(cAngle) * tan(cAngle))
        //当前角度在椭圆上的y坐标
        val oY = a * b * tan(cAngle) / sqrt(b * b + a * a * tan(cAngle) * tan(cAngle))

        //焦点长度
        val oC = sqrt(oX * oX + oY * oY)
        //矩形斜边的晨读
        val c =
            sqrt(inputRect.width() * inputRect.width() + inputRect.height() * inputRect.height())

        val ratio = oC / c

        //最大的宽度
        val width = inputRect.width() * ratio
        val height = inputRect.height() * ratio

        val maxRect2 = RectF(inputRect)
        maxRect2.inset((-width / 2).toFloat(), (-height / 2).toFloat())
        L.w("结果2: 原矩形:$inputRect ${inputRect.width()}:${inputRect.height()} 最大矩形:$maxRect2 ${maxRect.width()}:${maxRect.height()}")
    }

    /**矩形是否完全在椭圆内
     * 如果相交了, 则返回false*/
    fun isRectInOval(ovalPath: Path, rect: RectF): Boolean {
        return Path().apply {
            op(ovalPath, Path().apply {
                addRect(rect, Path.Direction.CW)
            }, Path.Op.REVERSE_DIFFERENCE)
        }.isEmpty
    }

    /**计算矩形在椭圆内, 允许的最大宽度.*/
    fun maxRectInOval(
        //椭圆的宽度
        ovalWidth: Float,
        //椭圆的高度
        ovalHeight: Float,
        //测量矩形的宽度
        rectWidth: Float,
        //测量矩形的高度
        rectHeight: Float
    ): IntArray {
        val x = ovalWidth / 2
        val y = ovalHeight / 2
        val heightRectRatio = rectHeight / rectWidth
        val maxWidth =
            sqrt((4 * x * x * y * y) / (y * y + heightRectRatio * heightRectRatio * x * x))
        val maxHeight = maxWidth * heightRectRatio
        return intArrayOf(maxWidth.toInt(), maxHeight.toInt())
    }

}