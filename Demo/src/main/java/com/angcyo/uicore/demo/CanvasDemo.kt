package com.angcyo.uicore.demo

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.core.ICanvasListener
import com.angcyo.canvas.core.InchValueUnit
import com.angcyo.canvas.core.MmValueUnit
import com.angcyo.canvas.items.TextItem
import com.angcyo.canvas.items.renderer.*
import com.angcyo.dialog.inputDialog
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.bindItem
import com.angcyo.gcode.GCodeHelper
import com.angcyo.library.ex.randomGetOnce
import com.angcyo.library.ex.randomString
import com.angcyo.library.ex.readAssets
import com.angcyo.library.ex.toBitmap
import com.angcyo.library.model.loadPath
import com.angcyo.picker.dslSinglePickerImage
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

                //switch_origin_button
                itemHolder.click(R.id.switch_origin_button) {
                    canvasView?.apply {
                        val left = canvasViewBox.getContentLeft()
                        val top = canvasViewBox.getContentTop()

                        val centerX = canvasViewBox.getContentCenterX()
                        val centerY = canvasViewBox.getContentCenterY()

                        //更新坐标系
                        if (canvasViewBox.coordinateSystemOriginPoint.x == left) {
                            canvasViewBox.updateCoordinateSystemOriginPoint(centerX, centerY)
                        } else {
                            canvasViewBox.updateCoordinateSystemOriginPoint(left, top)
                        }
                    }
                }
                itemHolder.click(R.id.switch_unit_button) {
                    canvasView?.apply {
                        canvasViewBox.updateCoordinateSystemUnit(
                            if (canvasViewBox.valueUnit is MmValueUnit) {
                                InchValueUnit()
                            } else {
                                MmValueUnit()
                            }
                        )
                    }
                }

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
                        addTextRenderer("angcyo${randomString(Random.nextInt(0, 3))}")
                    }
                }
                itemHolder.click(R.id.add_text2) {
                    canvasView?.apply {
                        addDrawableRenderer("angcyo${randomString(Random.nextInt(0, 3))}")
                    }
                }
                itemHolder.click(R.id.add_text3) {
                    canvasView?.apply {
                        addPictureTextRenderer("angcyo${randomString(Random.nextInt(0, 3))}")
                    }
                }
                itemHolder.click(R.id.add_svg) {
                    canvasView?.apply {
                        addDrawableRenderer(Sharp.loadResource(resources, R.raw.issue_19).drawable)
                    }
                }
                itemHolder.click(R.id.random_add_svg) {
                    canvasView?.apply {
                        addDrawableRenderer(loadSvgDrawable())
                    }
                }
                itemHolder.click(R.id.random_add_gcode) {
                    canvasView?.apply {
                        addDrawableRenderer(loadGCodeDrawable())
                    }
                }

                //preview
                itemHolder.click(R.id.preview_button) {
                    render {
                        PreviewBitmapItem()() {
                            canvasView?.let {
                                bitmap = it.getBitmap()
                            }
                        }
                    }
                }
                itemHolder.click(R.id.preview_rect_button) {
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
                AddShapesItem(canvasView!!)() {
                    itemClick = {
                        showShapeSelectLayout(itemHolder, canvasView!!)
                    }
                }
                AddDoodleItem(canvasView!!)()
            }
        }

        //事件监听
        canvasView?.canvasListenerList?.add(object : ICanvasListener {

            override fun onDoubleTapItem(itemRenderer: IItemRenderer<*>) {
                super.onDoubleTapItem(itemRenderer)
                if (itemRenderer is TextItemRenderer) {
                    fContext().inputDialog {
                        defaultInputString = itemRenderer.rendererItem?.text
                        onInputResult = { dialog, inputText ->
                            if (inputText.isNotEmpty()) {
                                itemRenderer.updateText("$inputText")
                            }
                            false
                        }
                    }
                } else if (itemRenderer is PictureTextItemRenderer) {
                    fContext().inputDialog {
                        defaultInputString = itemRenderer.rendererItem?.text
                        onInputResult = { dialog, inputText ->
                            if (inputText.isNotEmpty()) {
                                itemRenderer.updateText("$inputText")
                            }
                            false
                        }
                    }
                } else if (itemRenderer is BitmapItemRenderer) {
                    dslSinglePickerImage {
                        it?.firstOrNull()?.let { media ->
                            media.loadPath()?.apply {
                                itemRenderer.updateBitmap(toBitmap())
                            }
                        }
                    }
                }
            }

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
                if (itemRenderer is TextItemRenderer || itemRenderer is PictureTextItemRenderer) {
                    //选中TextItemRenderer时的控制菜单
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

                            TextFontItem()() {
                                itemClick = {
                                    showFontSelectLayout(itemHolder, itemRenderer)
                                }
                            }
                        }
                    }
                } else {
                    itemHolder.gone(R.id.canvas_control_layout)
                }
            }
        })
    }

    /**显示字体选择布局*/
    fun showFontSelectLayout(itemHolder: DslViewHolder, renderer: IItemRenderer<*>) {
        val fontControlView = itemHolder.rv(R.id.font_control_view)
        if (fontControlView.isVisible()) {
            fontControlView.gone()
        } else {
            fontControlView.gone(false)

            //更新字体
            fun updatePaintTypeface(typeface: Typeface?) {
                if (renderer is TextItemRenderer) {
                    renderer.updatePaintTypeface(typeface)
                } else if (renderer is PictureTextItemRenderer) {
                    renderer.updatePaintTypeface(typeface)
                }
            }

            //初始化控制item
            fontControlView?.initDslAdapter {
                hookUpdateDepend()
                render {
                    TypefaceItem()() {
                        displayName = "normal"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.DEFAULT
                        itemClick = {
                            updatePaintTypeface(typeface)
                        }
                    }
                    TypefaceItem()() {
                        displayName = "monospace"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.MONOSPACE
                        itemClick = {
                            updatePaintTypeface(typeface)
                        }
                    }
                    TypefaceItem()() {
                        displayName = "sans"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.SANS_SERIF
                        itemClick = {
                            updatePaintTypeface(typeface)
                        }
                    }
                    TypefaceItem()() {
                        displayName = "serif"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.SERIF
                        itemClick = {
                            updatePaintTypeface(typeface)
                        }
                    }
                }
            }
        }
    }

    /**显示形状选择布局*/
    fun showShapeSelectLayout(itemHolder: DslViewHolder, canvasView: CanvasView) {
        val controlLayout = itemHolder.view(R.id.canvas_control_layout)
        if (controlLayout.isVisible()) {
            itemHolder.gone(R.id.canvas_control_layout)
        } else {
            itemHolder.visible(R.id.canvas_control_layout)

            itemHolder.rv(R.id.canvas_control_view)?.initDslAdapter {
                hookUpdateDepend()
                render {
                    ShapeLineItem(canvasView)()
                    ShapeItem(canvasView)() {
                        addIco = R.drawable.shape_circle_ico
                        addText = "圆形"
                        shapePath = null
                    }
                    ShapeItem(canvasView)() {
                        addIco = R.drawable.shape_triangle_ico
                        addText = "三角形"
                        shapePath = null
                    }
                    ShapeItem(canvasView)() {
                        addIco = R.drawable.shape_square_ico
                        addText = "正方形"
                        shapePath = null
                    }
                    ShapeItem(canvasView)() {
                        addIco = R.drawable.shape_pentagon_ico
                        addText = "五角形"
                        shapePath = null
                    }
                    ShapeItem(canvasView)() {
                        addIco = R.drawable.shape_hexagon_ico
                        addText = "六角形"
                        shapePath = null
                    }
                    ShapeItem(canvasView)() {
                        addIco = R.drawable.shape_octagon_ico
                        addText = "八角形"
                        shapePath = null
                    }
                    ShapeItem(canvasView)() {
                        addIco = R.drawable.shape_rhombus_ico
                        addText = "菱形"
                        shapePath = null
                    }
                    ShapeItem(canvasView)() {
                        addIco = R.drawable.shape_pentagram_ico
                        addText = "星星"
                        shapePath = null
                    }
                    ShapeItem(canvasView)() {
                        addIco = R.drawable.shape_love_ico
                        addText = "心形"
                        shapePath = null
                    }
                }
            }
        }
    }
}