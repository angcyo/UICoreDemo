package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import com.angcyo.library.L

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/02/01
 */
class GroupScaleView(context: Context, attributeSet: AttributeSet? = null) :
    BaseMatrixView(context, attributeSet) {

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawGroup()
        canvas.drawSub()
        //canvas.drawSubPath(Color.RED)
        canvas.drawSubPath(Color.RED, getSaveCanvasProperty(subDataMatrix(true)))

        test()
    }

    fun test() {
        val matrix = subDataMatrix(true)
        val property = getSaveCanvasProperty(matrix)
        L.d()
    }

}