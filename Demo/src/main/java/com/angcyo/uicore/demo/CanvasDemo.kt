package com.angcyo.uicore.demo

import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.InputType
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.Strategy
import com.angcyo.canvas.core.CanvasUndoManager
import com.angcyo.canvas.core.ICanvasListener
import com.angcyo.canvas.core.InchValueUnit
import com.angcyo.canvas.core.MmValueUnit
import com.angcyo.canvas.items.PictureBitmapItem
import com.angcyo.canvas.items.PictureShapeItem
import com.angcyo.canvas.items.PictureTextItem
import com.angcyo.canvas.items.renderer.*
import com.angcyo.canvas.utils.*
import com.angcyo.dialog.inputDialog
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.bindItem
import com.angcyo.gcode.GCodeHelper
import com.angcyo.library.ex.*
import com.angcyo.library.model.loadPath
import com.angcyo.picker.dslSinglePickerImage
import com.angcyo.qrcode.createBarCode
import com.angcyo.qrcode.createQRCode
import com.angcyo.uicore.MainFragment.Companion.CLICK_COUNT
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.SvgDemo.Companion.gCodeNameList
import com.angcyo.uicore.demo.SvgDemo.Companion.svgResList
import com.angcyo.uicore.demo.canvas.*
import com.angcyo.widget.DslViewHolder
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

                            val unit = MmValueUnit()
                            val limitRect = RectF(
                                unit.convertValueToPixel(-50f),
                                unit.convertValueToPixel(-50f),
                                unit.convertValueToPixel(50f),
                                unit.convertValueToPixel(50f)
                            )// 宽*高 100*100mm

                            limitRenderer.updateLimit {
                                addRect(limitRect, Path.Direction.CW)// 宽*高 100*100mm
                            }

                            showRectBounds(limitRect)
                        } else {
                            canvasViewBox.updateCoordinateSystemOriginPoint(left, top)

                            val unit = MmValueUnit()
                            val limitRect = RectF(
                                0f,
                                0f,
                                unit.convertValueToPixel(300f),
                                unit.convertValueToPixel(400f)
                            )// 宽*高 300*400mm

                            limitRenderer.updateLimit {
                                addRect(limitRect, Path.Direction.CW)// 宽*高 300*400mm
                            }

                            showRectBounds(limitRect)
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
                itemHolder.click(R.id.bounds_button) {
                    canvasView?.apply {
                        val unit = MmValueUnit()
                        val limitRect = when {
                            CLICK_COUNT++ % 3 == 2 -> RectF(
                                unit.convertValueToPixel(100f),
                                unit.convertValueToPixel(100f),
                                unit.convertValueToPixel(300f),
                                unit.convertValueToPixel(300f)
                            )
                            CLICK_COUNT++ % 2 == 0 -> RectF(
                                unit.convertValueToPixel(-30f),
                                unit.convertValueToPixel(-30f),
                                unit.convertValueToPixel(-10f),
                                unit.convertValueToPixel(-10f)
                            )
                            else -> RectF(
                                unit.convertValueToPixel(10f),
                                unit.convertValueToPixel(10f),
                                unit.convertValueToPixel(30f),
                                unit.convertValueToPixel(30f)
                            )
                        }
                        val renderer = ShapeItemRenderer(canvasViewBox).apply {
                            addRect(limitRect)
                        }
                        addItemRenderer(renderer, Strategy(Strategy.STRATEGY_TYPE_NORMAL))
                        showRectBounds(limitRect)
                    }
                }
                itemHolder.click(R.id.smart_button) {
                    canvasView?.apply {
                        smartAssistant.enable = !smartAssistant.enable
                        if (smartAssistant.enable) {
                            longFeedback()
                        }
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
                        addTextRenderer(getRandomText())
                    }
                }
                itemHolder.click(R.id.add_text2) {
                    canvasView?.apply {
                        addDrawableRenderer(getRandomText())
                    }
                }
                itemHolder.click(R.id.add_text3) {
                    canvasView?.apply {
                        addPictureTextRenderer(getRandomText())
                    }
                }
                itemHolder.click(R.id.add_picture_text) {
                    canvasView?.apply {
                        addPictureTextRender(getRandomText())
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

    fun getRandomText() =
        "angcyo${randomString(Random.nextInt(0, 3))}\n${randomString(Random.nextInt(0, 3))}"

    fun loadSvgDrawable(): Drawable =
        Sharp.loadResource(resources, svgResList.randomGetOnce()!!).drawable

    fun loadGCodeDrawable(): Drawable = GCodeHelper.parseGCode(
        fContext(), fContext().readAssets(gCodeNameList.randomGetOnce()!!)!!
    )

    // region =====

    var _selectedCanvasItem: DslAdapterItem? = null

    var _undoCanvasItem: CanvasControlItem? = null
    var _redoCanvasItem: CanvasControlItem? = null

    /**Canvas控制*/
    fun bindCanvasRecyclerView(itemHolder: DslViewHolder, adapterItem: DslAdapterItem) {
        val canvasView = itemHolder.v<CanvasView>(R.id.canvas_view)
        val itemRecyclerView = itemHolder.v<RecyclerView>(R.id.canvas_item_view)

        itemRecyclerView?.initDslAdapter {
            hookUpdateDepend()
            render {
                CanvasControlItem()() {
                    itemIco = R.drawable.canvas_material_ico
                    itemText = "素材"
                    itemEnable = false
                }

                AddTextItem(canvasView!!)()
                AddImageItem(canvasView!!)()
                AddShapesItem()() {
                    itemClick = {
                        if (_selectedCanvasItem == this) {
                            itemHolder.goneControlLayout()
                        } else {
                            _selectedCanvasItem = this
                            showShapeSelectLayout(itemHolder, canvasView!!)
                        }
                    }
                }
                AddDoodleItem()() {
                    itemEnable = false
                }
                CanvasControlItem()() {
                    itemIco = R.drawable.canvas_barcode_ico
                    itemText = "条形码"
                    itemClick = {
                        fContext().inputDialog {
                            dialogTitle = "条形码内容"
                            inputType = InputType.TYPE_CLASS_NUMBER
                            onInputResult = { dialog, inputText ->
                                if (inputText.isNotEmpty()) {
                                    inputText.createBarCode()?.let {
                                        canvasView?.addPictureBitmapRenderer(it)?.apply {
                                            tag = "barcode"
                                            data = inputText
                                        }
                                    }
                                }
                                false
                            }
                        }
                    }
                }
                CanvasControlItem()() {
                    itemIco = R.drawable.canvas_qrcode_ico
                    itemText = "二维码"
                    itemClick = {
                        fContext().inputDialog {
                            dialogTitle = "二维码内容"
                            onInputResult = { dialog, inputText ->
                                if (inputText.isNotEmpty()) {
                                    inputText.createQRCode()?.let {
                                        canvasView?.addPictureBitmapRenderer(it)?.apply {
                                            tag = "qrcode"
                                            data = inputText
                                        }
                                    }
                                }
                                false
                            }
                        }
                    }
                }

                CanvasControlItem()() {
                    itemIco = R.drawable.canvas_edit_ico
                    itemText = "编辑"
                    itemEnable = false
                }
                CanvasControlItem()() {
                    itemIco = R.drawable.canvas_layer_ico
                    itemText = "图层"
                    itemEnable = false
                }
                CanvasControlItem()() {
                    itemIco = R.drawable.canvas_undo_ico
                    itemText = "撤销"
                    itemEnable = false

                    _undoCanvasItem = this
                    itemClick = {
                        canvasView?.getCanvasUndoManager()?.undo()
                    }
                }
                CanvasControlItem()() {
                    itemIco = R.drawable.canvas_redo_ico
                    itemText = "重做"
                    itemEnable = false

                    _redoCanvasItem = this
                    itemClick = {
                        canvasView?.getCanvasUndoManager()?.redo()
                    }
                }
                CanvasControlItem()() {
                    itemIco = R.drawable.canvas_setting_ico
                    itemText = "设置"
                    itemEnable = false
                }
            }
        }

        //事件监听
        canvasView?.canvasListenerList?.add(object : ICanvasListener {

            override fun onDoubleTapItem(itemRenderer: IItemRenderer<*>) {
                super.onDoubleTapItem(itemRenderer)
                if (itemRenderer is TextItemRenderer) {
                    fContext().inputDialog {
                        inputViewHeight = 100 * dpi
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
                        inputViewHeight = 100 * dpi
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
                } else if (itemRenderer is PictureItemRenderer) {
                    val renderItem = itemRenderer.rendererItem
                    if (renderItem is PictureTextItem) {
                        fContext().inputDialog {
                            inputViewHeight = 100 * dpi
                            defaultInputString = renderItem.text
                            onInputResult = { dialog, inputText ->
                                if (inputText.isNotEmpty()) {
                                    itemRenderer.updateItemText("$inputText")
                                }
                                false
                            }
                        }
                    } else if (renderItem is PictureBitmapItem) {
                        if (renderItem.tag == "barcode") {
                            //条形码
                            fContext().inputDialog {
                                dialogTitle = "条形码内容"
                                inputType = InputType.TYPE_CLASS_NUMBER
                                //defaultInputString = renderItem.data as CharSequence?
                                onInputResult = { dialog, inputText ->
                                    if (inputText.isNotEmpty()) {
                                        inputText.createBarCode()?.let {
                                            renderItem.data = inputText
                                            itemRenderer.updateItemBitmap(it)
                                        }
                                    }
                                    false
                                }
                            }
                        } else if (renderItem.tag == "qrcode") {
                            //二维码
                            fContext().inputDialog {
                                dialogTitle = "二维码内容"
                                //defaultInputString = renderItem.data as CharSequence?
                                onInputResult = { dialog, inputText ->
                                    if (inputText.isNotEmpty()) {
                                        inputText.createQRCode()?.let {
                                            renderItem.data = inputText
                                            itemRenderer.updateItemBitmap(it)
                                        }
                                    }
                                    false
                                }
                            }
                        } else {
                            //图片
                        }
                    }
                }
            }

            override fun onClearSelectItem(itemRenderer: IItemRenderer<*>) {
                super.onClearSelectItem(itemRenderer)
                itemHolder.goneControlLayout()
            }

            override fun onSelectedItem(
                itemRenderer: IItemRenderer<*>,
                oldItemRenderer: IItemRenderer<*>?
            ) {
                super.onSelectedItem(itemRenderer, oldItemRenderer)
                itemHolder.visible(R.id.canvas_control_layout)
                _selectedCanvasItem = null
                if (itemRenderer is TextItemRenderer || itemRenderer is PictureTextItemRenderer) {
                    //选中TextItemRenderer时的控制菜单
                    itemHolder.rv(R.id.canvas_control_view)?.initDslAdapter {
                        showTextControlItem(itemHolder, canvasView, itemRenderer)
                    }
                } else if (itemRenderer is PictureItemRenderer) {
                    val renderItem = itemRenderer.rendererItem
                    if (renderItem is PictureTextItem) {
                        //选中TextItemRenderer时的控制菜单
                        itemHolder.rv(R.id.canvas_control_view)?.initDslAdapter {
                            showTextControlItem(itemHolder, canvasView, itemRenderer)
                        }
                    } else if (renderItem is PictureShapeItem) {
                        itemHolder.rv(R.id.canvas_control_view)?.initDslAdapter {
                            showShapeControlItem(itemHolder, canvasView, itemRenderer)
                        }
                    } else {
                        itemHolder.goneControlLayout()
                    }
                } else {
                    itemHolder.goneControlLayout()
                }
            }

            override fun onCanvasUndoChanged(undoManager: CanvasUndoManager) {
                super.onCanvasUndoChanged(undoManager)
                _undoCanvasItem?.apply {
                    itemEnable = undoManager.canUndo()
                    itemTextSuperscript = "${undoManager.undoStack.size()}"
                    updateAdapterItem()
                }
                _redoCanvasItem?.apply {
                    itemEnable = undoManager.canRedo()
                    itemTextSuperscript = "${undoManager.redoStack.size()}"
                    updateAdapterItem()
                }
            }
        })
    }

    fun DslViewHolder.goneControlLayout() {
        gone(R.id.canvas_control_layout)
        _selectedCanvasItem = null
    }

    /**文本控制item*/
    fun DslAdapter.showTextControlItem(
        itemHolder: DslViewHolder,
        canvasView: CanvasView,
        itemRenderer: IItemRenderer<*>
    ) {
        hookUpdateDepend()
        render {
            this + TextSolidStyleItem(itemRenderer, canvasView)

            this + CanvasTextStyleItem(
                itemRenderer,
                PictureTextItem.TEXT_STYLE_BOLD,
                R.drawable.canvas_text_bold_style_ico,
                canvasView
            )
            this + CanvasTextStyleItem(
                itemRenderer,
                PictureTextItem.TEXT_STYLE_ITALIC,
                R.drawable.canvas_text_italic_style_ico,
                canvasView
            )
            this + CanvasTextStyleItem(
                itemRenderer,
                PictureTextItem.TEXT_STYLE_UNDER_LINE,
                R.drawable.canvas_text_under_line_style_ico,
                canvasView
            )
            this + CanvasTextStyleItem(
                itemRenderer,
                PictureTextItem.TEXT_STYLE_DELETE_LINE,
                R.drawable.canvas_text_delete_line_style_ico,
                canvasView
            )

            TextFontItem()() {
                itemClick = {
                    showFontSelectLayout(itemHolder, itemRenderer)
                }
            }

            CanvasIconItem()() {
                itemIco = R.drawable.canvas_text_style_standard_ico
                itemClick = {
                    if (itemRenderer is PictureItemRenderer) {
                        val renderItem = itemRenderer.rendererItem
                        if (renderItem is PictureTextItem) {
                            itemRenderer.updateTextOrientation(LinearLayout.HORIZONTAL)
                        }
                    }
                }
            }
            CanvasIconItem()() {
                itemIco = R.drawable.canvas_text_style_vertical_ico
                itemClick = {
                    if (itemRenderer is PictureItemRenderer) {
                        val renderItem = itemRenderer.rendererItem
                        if (renderItem is PictureTextItem) {
                            itemRenderer.updateTextOrientation(LinearLayout.VERTICAL)
                        }
                    }
                }
            }
            CanvasIconItem()() {
                itemIco = R.drawable.canvas_text_style_align_left_ico
                itemClick = {
                    if (itemRenderer is PictureItemRenderer) {
                        val renderItem = itemRenderer.rendererItem
                        if (renderItem is PictureTextItem) {
                            itemRenderer.updatePaintAlign(Paint.Align.LEFT)
                        }
                    }
                }
            }
            CanvasIconItem()() {
                itemIco = R.drawable.canvas_text_style_align_center_ico
                itemClick = {
                    if (itemRenderer is PictureItemRenderer) {
                        val renderItem = itemRenderer.rendererItem
                        if (renderItem is PictureTextItem) {
                            itemRenderer.updatePaintAlign(Paint.Align.CENTER)
                        }
                    }
                }
            }
            CanvasIconItem()() {
                itemIco = R.drawable.canvas_text_style_align_right_ico
                itemClick = {
                    if (itemRenderer is PictureItemRenderer) {
                        val renderItem = itemRenderer.rendererItem
                        if (renderItem is PictureTextItem) {
                            itemRenderer.updatePaintAlign(Paint.Align.RIGHT)
                        }
                    }
                }
            }
        }
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
                } else if (renderer is PictureItemRenderer) {
                    val renderItem = renderer.rendererItem
                    if (renderItem is PictureTextItem) {
                        renderer.updateTextTypeface(typeface)
                    }
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
                    //Typeface.DEFAULT
                    TypefaceItem()() {
                        displayName = "Default-Normal"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                        itemClick = {
                            updatePaintTypeface(typeface)
                        }
                    }
                    TypefaceItem()() {
                        displayName = "Default-Bold"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                        itemClick = {
                            updatePaintTypeface(typeface)
                        }
                    }
                    TypefaceItem()() {
                        displayName = "Default-Italic"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.ITALIC)
                        itemClick = {
                            updatePaintTypeface(typeface)
                        }
                    }
                    TypefaceItem()() {
                        displayName = "Default-Bold-Italic"
                        previewText = "激光啄木鸟"
                        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD_ITALIC)
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
        itemHolder.visible(R.id.canvas_control_layout)
        itemHolder.rv(R.id.canvas_control_view)?.initDslAdapter {
            hookUpdateDepend()
            render {
                ShapeLineItem(canvasView)()
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_line_ico
                    itemText = "线条"
                    shapePath = ShapesHelper.linePath()
                }
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_circle_ico
                    itemText = "圆形"
                    shapePath = ShapesHelper.circlePath()
                }
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_triangle_ico
                    itemText = "三角形"
                    shapePath = ShapesHelper.trianglePath()
                }
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_square_ico
                    itemText = "正方形"
                    shapePath = ShapesHelper.squarePath()
                }
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_pentagon_ico
                    itemText = "五角形"
                    shapePath = ShapesHelper.pentagonPath()
                }
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_hexagon_ico
                    itemText = "六角形"
                    shapePath = ShapesHelper.hexagonPath()
                }
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_octagon_ico
                    itemText = "八角形"
                    shapePath = ShapesHelper.octagonPath()
                }
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_rhombus_ico
                    itemText = "菱形"
                    shapePath = ShapesHelper.rhombusPath()
                }
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_pentagram_ico
                    itemText = "星星"
                    shapePath = ShapesHelper.pentagramPath()
                }
                ShapeItem(canvasView)() {
                    itemIco = R.drawable.canvas_shape_love_ico
                    itemText = "心形"
                    shapePath = ShapesHelper.lovePath()
                }
            }
        }
    }

    /**形状控制item*/
    fun DslAdapter.showShapeControlItem(
        itemHolder: DslViewHolder,
        canvasView: CanvasView,
        itemRenderer: IItemRenderer<*>
    ) {
        hookUpdateDepend()
        render {
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_style_stroke_ico
                itemText = "描边"
                itemClick = {
                    if (itemRenderer is PictureItemRenderer) {
                        itemRenderer.updatePaintStyle(Paint.Style.STROKE)
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_style_fill_ico
                itemText = "填充"
                itemClick = {
                    if (itemRenderer is PictureItemRenderer) {
                        itemRenderer.updatePaintStyle(Paint.Style.FILL_AND_STROKE)
                    }
                }
            }
        }
    }
}