package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.Gravity
import com.angcyo.canvas.CanvasRenderView
import com.angcyo.canvas.render.core.CanvasUndoManager
import com.angcyo.canvas.render.core.ICanvasRenderListener
import com.angcyo.canvas.render.operation.RectElement
import com.angcyo.canvas.render.renderer.CanvasElementRenderer
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.size
import com.angcyo.library.unit.toPixel
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023-2-11
 */
class Canvas2Demo : AppDslFragment() {

    init {
        enableSoftInput = false
    }

    override fun canSwipeBack(): Boolean = false

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.canvas2_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_render_view)
                itemHolder.click(R.id.canvas_render_view) {
                    canvasRenderView?.invalidate()
                }

                bindCanvasRenderListener(itemHolder)
                testCanvasRenderView(itemHolder)
            }
        }
    }

    /**[CanvasRenderView]事件监听*/
    fun bindCanvasRenderListener(itemHolder: DslViewHolder) {
        val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_render_view)
        itemHolder.enable(R.id.undo_button, false)
        itemHolder.enable(R.id.redo_button, false)

        itemHolder.click(R.id.undo_button) {
            canvasRenderView?.delegate?.undoManager?.undo()
        }
        itemHolder.click(R.id.redo_button) {
            canvasRenderView?.delegate?.undoManager?.redo()
        }
        canvasRenderView?.delegate?.renderListenerList?.add(object : ICanvasRenderListener {
            override fun onRenderUndoChange(undoManager: CanvasUndoManager) {
                itemHolder.tv(R.id.undo_button)?.text = span {
                    append("撤销")
                    append("${undoManager.undoStack.size()}") {
                        isSuperscript = true
                    }
                }
                itemHolder.tv(R.id.redo_button)?.text = span {
                    append("重做")
                    append("${undoManager.redoStack.size()}") {
                        isSuperscript = true
                    }
                }
                itemHolder.enable(R.id.undo_button, undoManager.canUndo())
                itemHolder.enable(R.id.redo_button, undoManager.canRedo())
            }
        })
    }

    fun testCanvasRenderView(itemHolder: DslViewHolder) {
        val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_render_view)
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
}