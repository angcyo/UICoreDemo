package com.angcyo.uicore.demo

import android.graphics.drawable.Drawable
import android.os.Bundle
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.items.DrawableItem
import com.angcyo.canvas.items.TextItem
import com.angcyo.canvas.items.renderer.DrawableItemRenderer
import com.angcyo.canvas.items.renderer.TextItemRenderer
import com.angcyo.dsladapter.bindItem
import com.angcyo.gcode.GCodeHelper
import com.angcyo.library.ex.randomGetOnce
import com.angcyo.library.ex.randomString
import com.angcyo.library.ex.readAssets
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.SvgDemo.Companion.gCodeNameList
import com.angcyo.uicore.demo.SvgDemo.Companion.svgResList
import com.pixplicity.sharp.Sharp
import kotlin.random.Random

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/01
 */
class CanvasDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.item_canvas_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val canvasView = itemHolder.v<CanvasView>(R.id.canvas_view)
                //?.setBgDrawable(_colorDrawable("#20000000".toColorInt()))
                //?.setBgDrawable(CheckerboardDrawable.create())

                itemHolder.click(R.id.translate_x_minus_button) {
                    canvasView?.canvasViewBox?.translateBy(-100f, 0f)
                }
                itemHolder.click(R.id.translate_x_plus_button) {
                    canvasView?.canvasViewBox?.translateBy(100f, 0f)
                }
                itemHolder.click(R.id.translate_y_minus_button) {
                    canvasView?.canvasViewBox?.translateBy(0f, -100f)
                }
                itemHolder.click(R.id.translate_y_plus_button) {
                    canvasView?.canvasViewBox?.translateBy(0f, 100f)
                }
                //放大
                itemHolder.click(R.id.scale_in_button) {
                    canvasView?.canvasViewBox?.scaleBy(1.2f, 1.2f)
                }
                //缩小
                itemHolder.click(R.id.scale_out_button) {
                    canvasView?.canvasViewBox?.scaleBy(.8f, .8f)
                }
                //add
                itemHolder.click(R.id.add_text) {
                    canvasView?.apply {
                        addCentreItemRenderer(TextItemRenderer(canvasViewBox).apply {
                            rendererItem = TextItem().apply {
                                text = "angcyo${randomString(Random.nextInt(0, 3))}"
                            }
                        })
                    }
                }
                itemHolder.click(R.id.add_svg) {
                    canvasView?.apply {
                        addCentreItemRenderer(DrawableItemRenderer(canvasViewBox).apply {
                            rendererItem = DrawableItem().apply {
                                drawable = Sharp.loadResource(resources, R.raw.issue_19).drawable
                            }
                        })
                    }
                }
                itemHolder.click(R.id.random_add_svg) {
                    canvasView?.apply {
                        addCentreItemRenderer(DrawableItemRenderer(canvasViewBox).apply {
                            rendererItem = DrawableItem().apply {
                                drawable = loadSvgDrawable()
                            }
                        })
                    }
                }
                itemHolder.click(R.id.random_add_gcode) {
                    canvasView?.apply {
                        addCentreItemRenderer(DrawableItemRenderer(canvasViewBox).apply {
                            rendererItem = DrawableItem().apply {
                                drawable = loadGCodeDrawable()
                            }
                        })
                    }
                }
            }
        }
    }

    fun loadSvgDrawable(): Drawable =
        Sharp.loadResource(resources, svgResList.randomGetOnce()!!).drawable

    fun loadGCodeDrawable(): Drawable = GCodeHelper.parseGCode(
        fContext(), fContext().readAssets(gCodeNameList.randomGetOnce()!!)!!
    )
}