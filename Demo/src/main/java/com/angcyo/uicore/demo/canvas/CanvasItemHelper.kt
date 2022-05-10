package com.angcyo.uicore.demo.canvas

import android.graphics.Paint
import android.graphics.Typeface
import android.text.InputType
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.core.CanvasUndoManager
import com.angcyo.canvas.core.ICanvasListener
import com.angcyo.canvas.items.PictureBitmapItem
import com.angcyo.canvas.items.PictureShapeItem
import com.angcyo.canvas.items.PictureTextItem
import com.angcyo.canvas.items.renderer.*
import com.angcyo.canvas.utils.ShapesHelper
import com.angcyo.canvas.utils.addPictureBitmapRenderer
import com.angcyo.core.dslitem.IFragmentItem
import com.angcyo.coroutine.launchLifecycle
import com.angcyo.coroutine.withBlock
import com.angcyo.dialog.hideLoading
import com.angcyo.dialog.inputDialog
import com.angcyo.dialog.loading
import com.angcyo.drawable.loading.TGStrokeLoadingDrawable
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.DslAdapterStatusItem
import com.angcyo.gcode.GCodeHelper
import com.angcyo.library.ex.*
import com.angcyo.library.model.loadPath
import com.angcyo.opencv.OpenCV
import com.angcyo.picker.dslSinglePickerImage
import com.angcyo.qrcode.createBarCode
import com.angcyo.qrcode.createQRCode
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.recycler.initDslAdapter

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/09
 */
class CanvasItemHelper(val fragment: Fragment) {

    var _selectedCanvasItem: DslAdapterItem? = null

    var _undoCanvasItem: CanvasControlItem? = null
    var _redoCanvasItem: CanvasControlItem? = null

    /**监听, 并赋值[IFragmentItem]*/
    fun DslAdapter.hookUpdateDepend() {
        observeItemUpdateDepend {
            adapterItems.forEach {
                if (it is IFragmentItem) {
                    it.itemFragment = fragment
                }
            }
        }
    }

    /**异步加载*/
    fun <T> loadingAsync(block: () -> T?, action: (T?) -> Unit) {
        fragment.launchLifecycle {
            fragment.loading(layoutId = R.layout.canvas_loading_layout, config = {
                cancelable = false
                onDialogInitListener = { dialog, dialogViewHolder ->
                    val loadingDrawable = TGStrokeLoadingDrawable().apply {
                        loadingOffset = 6 * dp
                        loadingWidth = 6 * dp
                        indeterminateSweepAngle = 1f
                        loadingBgColor = "#ffffff".toColorInt()
                        loadingColor = loadingBgColor
                    }
                    dialogViewHolder.view(R.id.lib_loading_view)?.setBgDrawable(loadingDrawable)
                }
            }) { dialog ->
                //cancel
            }

            val result = withBlock { block() }

            hideLoading()
            action(result)
        }
    }

    /**绑定画图支持的功能列表*/
    fun bindItems(vh: DslViewHolder, canvasView: CanvasView, adapter: DslAdapter) {
        adapter.render {
            hookUpdateDepend()

            CanvasControlItem()() {
                itemIco = R.drawable.canvas_material_ico
                itemText = "素材"
                itemEnable = false
            }

            AddTextItem(canvasView)()
            AddImageItem(canvasView)()
            AddShapesItem()() {
                itemClick = {
                    if (_selectedCanvasItem == this) {
                        vh.goneControlLayout()
                    } else {
                        _selectedCanvasItem = this
                        showShapeSelectLayout(vh, canvasView)
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
                    fragment.context?.inputDialog {
                        dialogTitle = "条形码内容"
                        inputType = InputType.TYPE_CLASS_NUMBER
                        onInputResult = { dialog, inputText ->
                            if (inputText.isNotEmpty()) {
                                inputText.createBarCode()?.let {
                                    canvasView.addPictureBitmapRenderer(it).apply {
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
                    fragment.context?.inputDialog {
                        dialogTitle = "二维码内容"
                        onInputResult = { dialog, inputText ->
                            if (inputText.isNotEmpty()) {
                                inputText.createQRCode()?.let {
                                    canvasView.addPictureBitmapRenderer(it).apply {
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
                itemEnable = true
                itemClick = {
                    vh.gone(R.id.canvas_layer_layout, itemIsSelected)
                    itemIsSelected = !itemIsSelected
                    updateAdapterItem()

                    if (itemIsSelected) {
                        vh.rv(R.id.canvas_layer_view)?.initDslAdapter {
                            showLayerControlItem(vh, canvasView)
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_undo_ico
                itemText = "撤销"
                itemEnable = false

                _undoCanvasItem = this
                itemClick = {
                    canvasView.canvasDelegate.getCanvasUndoManager().undo()
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_redo_ico
                itemText = "重做"
                itemEnable = false

                _redoCanvasItem = this
                itemClick = {
                    canvasView.canvasDelegate.getCanvasUndoManager().redo()
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_setting_ico
                itemText = "设置"
                itemEnable = false
            }
        }

        initCanvasListener(vh, canvasView)
    }

    /**事件监听*/
    fun initCanvasListener(vh: DslViewHolder, canvasView: CanvasView) {
        //事件监听
        canvasView.canvasDelegate.canvasListenerList.add(object : ICanvasListener {

            override fun onDoubleTapItem(itemRenderer: IItemRenderer<*>) {
                super.onDoubleTapItem(itemRenderer)
                if (itemRenderer is TextItemRenderer) {
                    fragment.context?.inputDialog {
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
                    fragment.context?.inputDialog {
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
                    fragment.dslSinglePickerImage {
                        it?.firstOrNull()?.let { media ->
                            media.loadPath()?.apply {
                                itemRenderer.updateBitmap(toBitmap())
                            }
                        }
                    }
                } else if (itemRenderer is PictureItemRenderer) {
                    val renderItem = itemRenderer.rendererItem
                    if (renderItem is PictureTextItem) {
                        fragment.context?.inputDialog {
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
                            fragment.context?.inputDialog {
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
                            fragment.context?.inputDialog {
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
                vh.goneControlLayout()
            }

            override fun onSelectedItem(
                itemRenderer: IItemRenderer<*>,
                oldItemRenderer: IItemRenderer<*>?
            ) {
                super.onSelectedItem(itemRenderer, oldItemRenderer)
                vh.goneControlLayout(false)
                if (itemRenderer is TextItemRenderer || itemRenderer is PictureTextItemRenderer) {
                    //选中TextItemRenderer时的控制菜单
                    vh.rv(R.id.canvas_control_view)?.initDslAdapter {
                        showTextControlItem(vh, canvasView, itemRenderer)
                    }
                } else if (itemRenderer is PictureItemRenderer) {
                    val renderItem = itemRenderer.rendererItem
                    if (renderItem is PictureTextItem) {
                        //选中TextItemRenderer时的控制菜单
                        vh.rv(R.id.canvas_control_view)?.initDslAdapter {
                            showTextControlItem(vh, canvasView, itemRenderer)
                        }
                    } else if (renderItem is PictureShapeItem) {
                        vh.rv(R.id.canvas_control_view)?.initDslAdapter {
                            showShapeControlItem(vh, canvasView, itemRenderer)
                        }
                    } else {
                        vh.goneControlLayout()
                    }
                } else if (itemRenderer is BitmapItemRenderer) {
                    vh.rv(R.id.canvas_control_view)?.initDslAdapter {
                        showBitmapControlItem(vh, canvasView, itemRenderer)
                    }
                } else {
                    vh.goneControlLayout()
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

    /**隐藏控制布局*/
    fun DslViewHolder.goneControlLayout(gone: Boolean = true) {
        gone(R.id.canvas_control_layout, gone)
        _selectedCanvasItem = null
    }

    /**显示形状选择布局*/
    fun showShapeSelectLayout(vh: DslViewHolder, canvasView: CanvasView) {
        vh.visible(R.id.canvas_control_layout)
        vh.rv(R.id.canvas_control_view)?.initDslAdapter {
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

    //<editor-fold desc="文本属性控制">

    /**文本属性控制item*/
    fun DslAdapter.showTextControlItem(
        vh: DslViewHolder,
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
                    showFontSelectLayout(vh, itemRenderer)
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
    fun showFontSelectLayout(vh: DslViewHolder, renderer: IItemRenderer<*>) {
        val fontControlView = vh.rv(R.id.font_control_view)
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

    //</editor-fold desc="文本属性控制">

    //<editor-fold desc="形状控制">

    /**形状属性控制item*/
    fun DslAdapter.showShapeControlItem(
        vh: DslViewHolder,
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

    //</editor-fold desc="形状控制">

    //<editor-fold desc="图片控制">

    /**图片属性控制item*/
    fun DslAdapter.showBitmapControlItem(
        vh: DslViewHolder,
        canvasView: CanvasView,
        itemRenderer: IItemRenderer<*>
    ) {
        hookUpdateDepend()
        render {
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_prints
                itemText = "版画"
                itemTintColor = false
                itemClick = {
                    if (itemRenderer is BitmapItemRenderer) {
                        loadingAsync({
                            itemRenderer.rendererItem?.bitmap?.let { bitmap ->
                                OpenCV.bitmapToPrint(fragment.requireContext(), bitmap)
                            }
                        }) {
                            it?.let {
                                itemRenderer.updateBitmap(it, true)
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_gcode
                itemText = "GCode"
                itemTintColor = false
                itemClick = {
                    if (itemRenderer is BitmapItemRenderer) {
                        loadingAsync({
                            itemRenderer.rendererItem?.bitmap?.let { bitmap ->
                                OpenCV.bitmapToGCode(fragment.requireContext(), bitmap).let {
                                    GCodeHelper.parseGCode(
                                        fragment.requireContext(),
                                        it.readText().toString()
                                    ).toBitmap()
                                }
                            }
                        }) {
                            it?.let {
                                itemRenderer.updateBitmap(it, true)
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_black_white
                itemText = "黑白画"
                itemTintColor = false
                itemClick = {
                    if (itemRenderer is BitmapItemRenderer) {
                        loadingAsync({
                            itemRenderer.rendererItem?.bitmap?.let { bitmap ->
                                OpenCV.bitmapToBlackWhite(bitmap)
                            }
                        }) {
                            it?.let {
                                itemRenderer.updateBitmap(it, true)
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_dithering
                itemText = "抖动"
                itemTintColor = false
                itemClick = {
                    if (itemRenderer is BitmapItemRenderer) {
                        loadingAsync({
                            itemRenderer.rendererItem?.bitmap?.let { bitmap ->
                                OpenCV.bitmapToDithering(fragment.requireContext(), bitmap)
                            }
                        }) {
                            it?.let {
                                itemRenderer.updateBitmap(it, true)
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_grey
                itemText = "灰度"
                itemTintColor = false
                itemClick = {
                    if (itemRenderer is BitmapItemRenderer) {
                        loadingAsync({
                            itemRenderer.rendererItem?.bitmap?.let { bitmap ->
                                OpenCV.bitmapToGrey(bitmap)
                            }
                        }) {
                            it?.let {
                                itemRenderer.updateBitmap(it, true)
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_seal
                itemText = "印章"
                itemTintColor = false
                itemClick = {
                    if (itemRenderer is BitmapItemRenderer) {
                        loadingAsync({
                            itemRenderer.rendererItem?.bitmap?.let { bitmap ->
                                OpenCV.bitmapToSeal(fragment.requireContext(), bitmap)
                            }
                        }) {
                            it?.let {
                                itemRenderer.updateBitmap(it, true)
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_invert
                itemText = "反色"
                itemTintColor = false
                itemClick = {
                    if (itemRenderer is BitmapItemRenderer) {
                        loadingAsync({
                            itemRenderer.rendererItem?.bitmap?.let { bitmap ->
                                OpenCV.bitmapToBlackWhite(bitmap, 240, 1)
                            }
                        }) {
                            it?.let {
                                itemRenderer.updateBitmap(it, true)
                            }
                        }
                    }
                }
            }
        }
    }

    //</editor-fold desc="图片控制">

    //<editor-fold desc="图层控制">

    /**显示图层item*/
    fun DslAdapter.showLayerControlItem(vh: DslViewHolder, canvasView: CanvasView) {
        hookUpdateDepend()
        render {
            canvasView.canvasDelegate.itemsRendererList.forEach {
                CanvasLayerItem()() {
                    itemCanvasDelegate = canvasView.canvasDelegate
                    itemRenderer = it
                }
            }
            if (canvasView.canvasDelegate.itemsRendererList.isEmpty()) {
                setAdapterStatus(DslAdapterStatusItem.ADAPTER_STATUS_EMPTY)
            } else {
                setAdapterStatus(DslAdapterStatusItem.ADAPTER_STATUS_NONE)
            }
        }
    }

    //</editor-fold desc="图层控制">
}