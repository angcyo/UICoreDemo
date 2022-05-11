package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withMatrix
import androidx.core.graphics.withRotation
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.mH
import com.angcyo.library.ex.mW
import com.angcyo.library.ex.toColor

/**
 *
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/10
 */
class PaintView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 40 * dp
    }

    var titleMode: Shader.TileMode = Shader.TileMode.CLAMP

    var colors = intArrayOf(
        Color.RED,
        Color.MAGENTA,
        Color.BLUE,
        Color.CYAN,
        Color.GREEN,
        Color.YELLOW,
        Color.RED
    )

    /**https://cloud.tencent.com/developer/article/1383152*/
    fun createBitmapShader(): BitmapShader {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        /*val drawable = resources.getDrawable(R.drawable.face)
        drawable.setBounds(0, 0, bitmap.width, bitmap.height)
        drawable.draw(canvas)*/

        canvas.withRotation(-45f, 50f, 50f) {
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            paint.style = Paint.Style.FILL
            val left = -100f
            var top = -100f
            val right = 200f
            var bottom = left + 20f

            for (i in 0..40) {
                if (i % 2 == 0) {
                    paint.color = Color.RED
                } else {
                    paint.color = Color.BLUE
                }
                canvas.drawRect(left, top, right, bottom, paint)
                top = bottom
                bottom += 20f
            }
        }

        return BitmapShader(bitmap, titleMode, titleMode)
    }

    fun createLinearShader(): LinearGradient {
        return LinearGradient(0f, 0f, mW().toFloat(), 0f, colors, null, titleMode)
    }

    fun createSweepShader(): SweepGradient {
        return SweepGradient(mW() / 2f, mH() / 2f, colors, null)
    }

    fun createRadialShader(): RadialGradient {
        return RadialGradient(
            mW() / 2f, mH() / 2f, mW() / 2f,
            Color.WHITE, 0x00FFFFFF, titleMode
        )
    }

    var rotate = -45f

    var height1 = 20f
    var height2 = 14f

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.actionMasked == MotionEvent.ACTION_UP) {
            rotate++
            invalidate()
        }
        super.onTouchEvent(event)
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.style = Paint.Style.FILL

        //canvas.drawRect(mW() / 4f, mH() / 4f, mW() * 3 / 4f, mH() * 3 / 4f, paint)
        //canvas.withRotation(-45f) {
        canvas.drawRect(0f, 0f, mW().toFloat(), mH().toFloat(), paint)
        //}

        if (paint.shader == null) {
            val offset = 100 * dp
            val bounds = RectF(offset, offset, mW().toFloat() - offset, mH().toFloat() - offset)
            val rotateBounds = RectF()

            val matrix = Matrix()
            matrix.postRotate(rotate, bounds.centerX(), bounds.centerY())
            matrix.mapRect(rotateBounds, bounds)
            paint.style = Paint.Style.FILL
            canvas.withMatrix(matrix) {
                val left = rotateBounds.left
                var top = rotateBounds.top
                val right = rotateBounds.right
                var bottom = top

                var index = 0
                while (bottom <= rotateBounds.bottom) {
                    if (index++ % 2 == 0) {
                        paint.color = "#cccccc".toColor()
                        bottom += height1
                    } else {
                        paint.color = "#e2e2e2".toColor()
                        bottom += height2
                    }
                    canvas.drawRect(left, top, right, bottom, paint)
                    top = bottom
                }
            }

            paint.style = Paint.Style.STROKE
            paint.color = Color.RED
            canvas.drawRect(bounds, paint)
        }

        canvas.drawText("angcyo", 0f, mH() / 2f, paint)
    }
}