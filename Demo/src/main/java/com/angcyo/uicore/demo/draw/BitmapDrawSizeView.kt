package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.angcyo.library.ex.randomColor

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/02/27
 */
class BitmapDrawSizeView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    var drawWidth = 1000
    var drawHeight = 1000
    var drawDpi = 1

    var drawBitmap: Bitmap? = null

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    var onExceptionAction: (e: Exception) -> Unit = {}

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //Canvas: trying to draw too large(216442204bytes) bitmap.
        //Canvas: trying to draw too large(174555040bytes) bitmap.
        //Canvas: trying to draw too large(106439528bytes) bitmap.
        try {
            drawBitmap?.let { bitmap ->
                canvas.drawBitmap(bitmap, 0f, 0f, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            onExceptionAction(e)
        }
    }

    /**测试*/
    fun test(): Exception? {
        try {
            val w = drawWidth * drawDpi
            val h = drawHeight * drawDpi
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            canvas.drawColor(randomColor())
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            drawBitmap = bitmap
            invalidate()
        } catch (e: Exception) {
            onExceptionAction(e)
            return e
        }
        return null
    }

}