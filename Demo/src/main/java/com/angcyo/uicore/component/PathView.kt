package com.angcyo.uicore.component

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.angcyo.library.L
import com.angcyo.library.ex.dp
import com.angcyo.uicore.test.PathTest
import kotlin.math.atan
import kotlin.math.sqrt
import kotlin.math.tan

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/04
 */
class PathView(context: Context, attributeSet: AttributeSet? = null) : View(context, attributeSet) {

    var ovalWidth = 400 * dp
    var ovalHeight = 300 * dp

    var inputWidth = 30 * dp
    var inputHeight = 50 * dp

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val ovalRect = RectF(0f, 0f, ovalWidth, ovalHeight)
        val ovalPath = Path().apply {
            addOval(ovalRect, Path.Direction.CW)
        }

        val inputRect = RectF(
            (ovalRect.width() - inputWidth) / 2,
            (ovalRect.height() - inputHeight) / 2,
            ovalRect.width() / 2 + inputWidth / 2,
            ovalRect.height() / 2 + inputHeight / 2
        )
        val maxRect = RectF()
        maxRect.set(inputRect)

        val dSize = if (PathTest.isRectInOval(ovalPath, maxRect)) {
            -1f
        } else {
            1f
        }

        val widthRectRatio = inputRect.width() / inputRect.height()
        val heightRectRatio = inputRect.height() / inputRect.width()

        val widthPriority =
            (inputRect.width() / ovalRect.width()) > (inputRect.height() / ovalRect.height())

        while (true) {
            if (dSize > 0 && PathTest.isRectInOval(ovalPath, maxRect)) {
                break
            } else if (!PathTest.isRectInOval(ovalPath, maxRect)) {
                break
            }
            if (widthPriority) {
                maxRect.inset(dSize, dSize / widthRectRatio)
            } else {
                maxRect.inset(dSize / heightRectRatio, dSize)
            }
        }
        L.w("结果1: 原矩形:$inputRect ${inputRect.width()}:${inputRect.height()} 最大矩形:$maxRect ${maxRect.width()}:${maxRect.height()}")

        canvas.drawPath(ovalPath, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.RED
        })
        canvas.drawRect(inputRect, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1 * dp
        })
        canvas.drawRect(maxRect, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLUE
            style = Paint.Style.STROKE
            strokeWidth = 1 * dp
        })

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

        canvas.drawRect(maxRect2, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.GREEN
            style = Paint.Style.STROKE
            strokeWidth = 1 * dp
        })

        //方法3
        val x = ovalRect.width() / 2
        val y = ovalRect.height() / 2
        val maxWidth =
            sqrt((4 * x * x * y * y) / (y * y + heightRectRatio * heightRectRatio * x * x))
        val maxHeight = maxWidth * heightRectRatio
        val maxRect3 = RectF(inputRect)

        maxRect3.set(
            ((ovalRect.width() - maxWidth) / 2).toFloat(),
            ((ovalRect.height() - maxHeight) / 2).toFloat(),
            (ovalRect.width() / 2 + maxWidth / 2).toFloat(),
            (ovalRect.height() / 2 + maxHeight / 2).toFloat()
        )

        canvas.drawRect(maxRect3, Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.YELLOW
            style = Paint.Style.STROKE
            strokeWidth = 1 * dp
        })
    }

}