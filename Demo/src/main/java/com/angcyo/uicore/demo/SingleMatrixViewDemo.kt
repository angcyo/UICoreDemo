package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.Path
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import com.angcyo.dsladapter.bindItem
import com.angcyo.item.DslStepAdjustItem
import com.angcyo.library.canvas.SingleMatrixView
import com.angcyo.library.canvas.component.ElementClickComponent
import com.angcyo.library.canvas.core.ICanvasTouchListener
import com.angcyo.library.canvas.core.IRenderElement
import com.angcyo.library.canvas.element.PathElement
import com.angcyo.uicore.base.AppDslFragment
import kotlin.math.max
import kotlin.math.min

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/06/28
 */
class SingleMatrixViewDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.item_single_matrix_view) { itemHolder, _, _, _ ->
                itemHolder.v<SingleMatrixView>(R.id.single_matrix_view)?.apply {
                    testCircleElement(this)
                }
            }

            DslStepAdjustItem()()
        }
    }

    private val _tempPoint = PointF()

    fun testCircleElement(singleMatrixView: SingleMatrixView) {
        val delegate = singleMatrixView.delegate
        val canvasViewBox = delegate.getCanvasViewBox()

        val elementList = mutableListOf<IRenderElement>()
        val count = 10
        val size = 30f
        val gap = size / 2

        var x = gap + size
        for (i in 0..count) {
            elementList.add(PathElement().apply {
                updatePath {
                    addCircle(x, size + gap, size, Path.Direction.CW)
                }
            })
            x += size * 2 + gap
        }
        singleMatrixView.post {
            delegate.renderManager.rendererList.add(PathElement().apply {
                updatePath {
                    addRect(canvasViewBox.renderBounds, Path.Direction.CW)
                }
                paint.color = Color.RED
                canSelect = false
            })
            delegate.showRectBounds()
        }
        delegate.renderManager.rendererList.addAll(elementList)
        delegate.touchManager.touchListenerList.add(
            ElementClickComponent(delegate) {
                _vh.tv(R.id.lib_result_view)?.text = "点击了元素:$it"
            }
        )

        val touchElement = PathElement()
        touchElement.canSelect = false
        touchElement.paint.color = Color.BLUE
        delegate.touchManager.touchListenerList.add(object : ICanvasTouchListener {
            override fun dispatchTouchEvent(event: MotionEvent) {
                val eventX = event.getX(event.actionIndex)
                val eventY = event.getY(event.actionIndex)
                _tempPoint.set(eventX, eventY)
                canvasViewBox.transformToInside(_tempPoint)
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                        touchElement.updatePath {
                            val l = min(canvasViewBox.renderBounds.left, _tempPoint.x)
                            val t = min(canvasViewBox.renderBounds.top, _tempPoint.y)
                            val r = max(canvasViewBox.renderBounds.right, _tempPoint.x)
                            val b = max(canvasViewBox.renderBounds.bottom, _tempPoint.y)

                            moveTo(l, _tempPoint.y)
                            lineTo(r, _tempPoint.y)
                            moveTo(_tempPoint.x, t)
                            lineTo(_tempPoint.x, b)
                            delegate.renderManager.addRenderer(touchElement)
                            delegate.refresh()
                        }
                    }

                    MotionEvent.ACTION_UP -> {
                        delegate.renderManager.removeRenderer(touchElement)
                    }
                }
            }
        })
    }

}