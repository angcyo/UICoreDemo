package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.CanvasRenderView
import com.angcyo.canvas.render.operation.RectElement
import com.angcyo.canvas.render.renderer.CanvasElementRenderer
import com.angcyo.canvas2.laser.pecker.CanvasLayoutHelper
import com.angcyo.dsladapter.bindItem
import com.angcyo.engrave.EngraveFlowLayoutHelper
import com.angcyo.engrave.IEngraveCanvasFragment
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.library.unit.toPixel
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.DslViewHolder

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023-2-11
 */
class Canvas2Demo : AppDslFragment(), IEngraveCanvasFragment {

    val canvasLayoutHelper = CanvasLayoutHelper(this)

    init {
        enableSoftInput = false
    }

    override fun canSwipeBack(): Boolean = false

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.canvas2_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_view)

                //1.
                canvasLayoutHelper.bindCanvasLayout(itemHolder)

                itemHolder.click(R.id.canvas_view) {
                    canvasRenderView?.invalidate()
                }

                testCanvasRenderView(itemHolder)
            }
        }
    }

    fun testCanvasRenderView(itemHolder: DslViewHolder) {
        val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_view)
        canvasRenderView?.delegate?.apply {
            renderViewBox.originGravity = Gravity.CENTER
            renderManager.elementRendererList.add(CanvasElementRenderer().apply {
                element = RectElement()
            })
            renderManager.elementRendererList.add(CanvasElementRenderer().apply {
                element = RectElement().apply {
                    renderProperty.apply {
                        anchorX = (-4.8f).toPixel()
                        anchorY = (-37.704956f).toPixel()
                        width = 12.933333f.toPixel()
                        height = 10.8f.toPixel()

                        scaleX = 1.7841423f
                        scaleY = 0.8901337f
                        angle = 297f

                        skewX = -37.52056f
                        skewY = 0f
                    }
                }
            })
            renderManager.elementRendererList.add(CanvasElementRenderer().apply {
                element = RectElement().apply {
                    renderProperty.apply {
                        anchorX = (-40.8f).toPixel()
                        anchorY = (-40.704956f).toPixel()
                        width = 12.933333f.toPixel()
                        height = 10.8f.toPixel()

                        scaleX = 1.7841423f
                        scaleY = 2.8901336f
                        angle = 297f

                        skewX = -37.52056f
                        skewY = 0f
                    }
                }
            })
        }
    }

    //<editor-fold desc="IEngraveCanvasFragment">

    override val fragment: AbsLifecycleFragment
        get() = this

    override val engraveFlowLayoutHelper: EngraveFlowLayoutHelper
        get() = EngraveFlowLayoutHelper()

    override val canvasDelegate: CanvasDelegate?
        get() = null

    override val flowLayoutContainer: ViewGroup?
        get() = fragment._vh.group(R.id.engrave_flow_wrap_layout)

    //</editor-fold desc="IEngraveCanvasFragment">
}