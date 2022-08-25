package com.angcyo.uicore.demo.draw

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatImageView
import com.angcyo.library.ex.interceptParentTouchEvent
import com.angcyo.uicore.demo.R
import kotlin.math.sqrt


/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/25
 */
class MeshImageView(context: Context, attributeSet: AttributeSet? = null) :
    AppCompatImageView(context, attributeSet) {

    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.face)

    //扭曲网格的格子数量
    val meshWidth = 30
    val meshHeight = 30
    val count = (meshWidth + 1) * (meshHeight + 1)

    //扭曲前后点的坐标
    val before = FloatArray(count * 2)
    val after = FloatArray(count * 2)

    init {
        val width = bitmap.width
        val height = bitmap.height

        var index = 0
        for (y in 0..meshHeight) {
            val fy = height * 1f / meshHeight * y
            for (x in 0..meshWidth) {
                val fx = width * 1f / meshWidth * x

                //在图片当中, 每个点的坐标
                before[index * 2 + 0] = fx
                after[index * 2 + 0] = fx

                before[index * 2 + 1] = if (y >= meshHeight / 2) {
                    fy * 0.5f
                } else {
                    fy * 1.5f
                }
                after[index * 2 + 1] = fy
                index += 1
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        //super.onDraw(canvas)
        testMesh(canvas)
    }

    fun testMesh(canvas: Canvas) {
        canvas.drawBitmapMesh(
            bitmap,
            meshWidth, meshHeight,
            before, 0,
            null, 0,
            null
        )
    }

    //工具方法，用于根据触摸事件的位置计算verts数组里各元素的值
    private fun warp(cx: Float, cy: Float) {
        var i = 0
        while (i < count * 2) {
            val dx: Float = cx - after[i + 0]
            val dy: Float = cy - after[i + 1]
            val dd = dx * dx + dy * dy
            //计算每个座标点与当前点（cx、cy）之间的距离
            val d = sqrt(dd.toDouble()).toFloat()
            //计算扭曲度，距离当前点（cx、cy）越远，扭曲度越小
            val pull = 80000f / (dd * d)
            //对verts数组（保存bitmap上21 * 21个点经过扭曲后的座标）重新赋值
            if (pull >= 1) {
                before[i + 0] = cx
                before[i + 1] = cy
            } else {
                //控制各顶点向触摸事件发生点偏移
                before[i + 0] = after[i + 0] + dx * pull
                before[i + 1] = after[i + 1] + dy * pull
            }
            i += 2
        }
        //通知View组件重绘
        invalidate()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        interceptParentTouchEvent(event)
        //调用warp方法根据触摸屏事件的座标点来扭曲verts数组
        warp(event.x, event.y)
        return true
    }
}