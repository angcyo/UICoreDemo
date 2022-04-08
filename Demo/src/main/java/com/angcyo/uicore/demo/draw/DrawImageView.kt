package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withMatrix
import com.angcyo.library.ex.setBounds
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/08
 */
class DrawImageView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    var drawable: Drawable? = null

    var drawableMatrix: Matrix = Matrix()
        set(value) {
            field = value
            invalidate()
        }

    init {
        drawable = resources.getDrawable(R.drawable.face, getContext().theme)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withMatrix(drawableMatrix) {
            drawable?.setBounds(measuredWidth, measuredHeight)
            drawable?.draw(canvas)
        }

    }

}