package com.angcyo.uicore.demo

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.core.ICanvasListener
import com.angcyo.canvas.items.DrawableItem
import com.angcyo.canvas.items.TextItem
import com.angcyo.canvas.items.renderer.DrawableItemRenderer
import com.angcyo.canvas.items.renderer.IItemRenderer
import com.angcyo.canvas.items.renderer.TextItemRenderer
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.bindItem
import com.angcyo.gcode.GCodeHelper
import com.angcyo.library.ex.randomGetOnce
import com.angcyo.library.ex.randomString
import com.angcyo.library.ex.readAssets
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.SvgDemo.Companion.gCodeNameList
import com.angcyo.uicore.demo.SvgDemo.Companion.svgResList
import com.angcyo.uicore.demo.canvas.*
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.gone
import com.angcyo.widget.base.isVisible
import com.angcyo.widget.recycler.initDslAdapter
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

                //preview
                itemHolder.click(R.id.preview_button) {
                    render {
                        PreviewBitmapItem()() {
                            canvasView?.let {
                                val left = it.canvasViewBox.valueUnit.convertValueToPixel(-10f)
                                val top = it.canvasViewBox.valueUnit.convertValueToPixel(-10f)
                                val width = it.canvasViewBox.valueUnit.convertValueToPixel(20f)
                                val height = it.canvasViewBox.valueUnit.convertValueToPixel(20f)
                                bitmap = it.getBitmap(left, top, width, height)
                            }
                        }
                    }
                }

                //canvas
                bindCanvasRecyclerView(itemHolder, adapterItem)
            }
        }
    }

    fun loadSvgDrawable(): Drawable =
        Sharp.loadResource(resources, svgResList.randomGetOnce()!!).drawable

    fun loadGCodeDrawable(): Drawable = GCodeHelper.parseGCode(
        fContext(), fContext().readAssets(gCodeNameList.randomGetOnce()!!)!!
    )

    /**Canvas控制*/
    fun bindCanvasRecyclerView(itemHolder: DslViewHolder, adapterItem: DslAdapterItem) {
        val canvasView = itemHolder.v<CanvasView>(R.id.canvas_view)
        val itemRecyclerView = itemHolder.v<RecyclerView>(R.id.canvas_item_view)

        itemRecyclerView?.initDslAdapter {
            hookUpdateDepend()
            render {
                AddTextItem(canvasView!!)()
                AddImageItem(canvasView!!)()
                AddShapesItem(canvasView!!)()
                AddDoodleItem(canvasView!!)()
            }
        }

        canvasView?.canvasListenerList?.add(object : ICanvasListener {

            override fun onClearSelectItem(itemRenderer: IItemRenderer<*>) {
                super.onClearSelectItem(itemRenderer)
                itemHolder.gone(R.id.canvas_control_layout)
            }

            override fun onSelectedItem(
                itemRenderer: IItemRenderer<*>,
                oldItemRenderer: IItemRenderer<*>?
            ) {
                super.onSelectedItem(itemRenderer, oldItemRenderer)
                itemHolder.visible(R.id.canvas_control_layout)
                if (itemRenderer is TextItemRenderer) {
                    itemHolder.rv(R.id.canvas_control_view)?.initDslAdapter {
                        hookUpdateDepend()
                        render {
                            this + TextSolidStyleItem(itemRenderer, canvasView)

                            this + CanvasTextStyleItem(
                                itemRenderer,
                                TextItem.TEXT_STYLE_BOLD,
                                R.drawable.text_bold_style_ico,
                                canvasView
                            )
                            this + CanvasTextStyleItem(
                                itemRenderer,
                                TextItem.TEXT_STYLE_ITALIC,
                                R.drawable.text_italic_style_ico,
                                canvasView
                            )
                            this + CanvasTextStyleItem(
                                itemRenderer,
                                TextItem.TEXT_STYLE_UNDER_LINE,
                                R.drawable.text_under_line_style_ico,
                                canvasView
                            )
                            this + CanvasTextStyleItem(
                                itemRenderer,
                                TextItem.TEXT_STYLE_DELETE_LINE,
                                R.drawable.text_delete_line_style_ico,
                                canvasView
                            )

                            TextFontItem(canvasView)() {
                                itemClick = {
                                    showFontSelectLayout(itemHolder, itemRenderer)
                                }
                            }
                        }
                    }
                }
            }
        })
    }

    fun showFontSelectLayout(itemHolder: DslViewHolder, itemRenderer: TextItemRenderer) {
        val fontControlView = itemHolder.rv(R.id.font_control_view)
        if (fontControlView.isVisible()) {
            fontControlView.gone()
        } else {
            fontControlView.gone(false)

            fontControlView?.initDslAdapter {
                hookUpdateDepend()
                render {
                    TypefaceItem()() {
                        displayName = "normal"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.DEFAULT
                        itemClick = {
                            itemRenderer.updatePaintTypeface(typeface)
                        }
                    }
                    TypefaceItem()() {
                        displayName = "monospace"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.MONOSPACE
                        itemClick = {
                            itemRenderer.updatePaintTypeface(typeface)
                        }
                    }
                    TypefaceItem()() {
                        displayName = "sans"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.SANS_SERIF
                        itemClick = {
                            itemRenderer.updatePaintTypeface(typeface)
                        }
                    }
                    TypefaceItem()() {
                        displayName = "serif"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.SERIF
                        itemClick = {
                            itemRenderer.updatePaintTypeface(typeface)
                        }
                    }
                }
            }
        }
    }
}