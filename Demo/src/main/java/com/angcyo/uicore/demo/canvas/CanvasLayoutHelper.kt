package com.angcyo.uicore.demo.canvas

import android.graphics.Paint
import android.graphics.RectF
import android.text.InputType
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryStateParser
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.Reason
import com.angcyo.canvas.Strategy
import com.angcyo.canvas.core.CanvasUndoManager
import com.angcyo.canvas.core.ICanvasListener
import com.angcyo.canvas.core.IRenderer
import com.angcyo.canvas.core.renderer.SelectGroupRenderer
import com.angcyo.canvas.items.PictureBitmapItem
import com.angcyo.canvas.items.PictureShapeItem
import com.angcyo.canvas.items.PictureTextItem
import com.angcyo.canvas.items.renderer.*
import com.angcyo.canvas.utils.*
import com.angcyo.core.vmApp
import com.angcyo.dialog.inputDialog
import com.angcyo.dsladapter.*
import com.angcyo.dsladapter.item.IFragmentItem
import com.angcyo.gcode.GCodeHelper
import com.angcyo.library.ex.*
import com.angcyo.library.model.loadPath
import com.angcyo.library.toast
import com.angcyo.library.utils.fileName
import com.angcyo.library.utils.filePath
import com.angcyo.opencv.OpenCV
import com.angcyo.picker.dslSinglePickerImage
import com.angcyo.qrcode.createBarCode
import com.angcyo.qrcode.createQRCode
import com.angcyo.transition.dslTransition
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.recycler.renderDslAdapter

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/09
 */
class CanvasLayoutHelper(val fragment: Fragment) {

    /**当前选中的[DslAdapterItem], 用来实现底部控制按钮互斥操作*/
    var _selectedCanvasItem: DslAdapterItem? = null

    var _undoCanvasItem: CanvasControlItem? = null
    var _redoCanvasItem: CanvasControlItem? = null

    /**图层item*/
    var _layerCanvasItem: DslAdapterItem? = null

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

    /**取消正在选中的项状态*/
    fun cancelSelectedItem() {
        if (_selectedCanvasItem?.itemIsSelected == true) {
            _selectedCanvasItem?.itemIsSelected = false
            _selectedCanvasItem?.updateAdapterItem()
        }
        _selectedCanvasItem = null
    }

    /**选中一个新的Item, 并取消之前选中的*/
    fun selectedItemWith(item: DslAdapterItem) {
        if (_selectedCanvasItem == item) {
            return
        }
        cancelSelectedItem()
        _selectedCanvasItem = item
    }

    /**绑定画图支持的功能列表*/
    fun bindItems(vh: DslViewHolder, canvasView: CanvasView, adapter: DslAdapter) {
        adapter.render {
            hookUpdateDepend()

            CanvasControlItem()() {
                itemIco = R.drawable.canvas_undo_ico
                itemText = _string(R.string.canvas_undo)
                itemEnable = false

                _undoCanvasItem = this
                itemClick = {
                    canvasView.canvasDelegate.getCanvasUndoManager().undo()
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_redo_ico
                itemText = _string(R.string.canvas_redo)
                drawCanvasRight()
                itemEnable = false

                _redoCanvasItem = this
                itemClick = {
                    canvasView.canvasDelegate.getCanvasUndoManager().redo()
                }
            }

            CanvasControlItem()() {
                itemIco = R.drawable.canvas_material_ico
                itemText = _string(R.string.canvas_material)
                itemEnable = true

                itemClick = {
                    toast("功能开发中...")
                }
            }

            AddTextItem(canvasView)()
            AddImageItem(canvasView)()
            AddShapesItem()() {
                itemClick = {
                    vh.showControlLayout(!itemIsSelected)
                    itemIsSelected = !itemIsSelected
                    updateAdapterItem()

                    if (itemIsSelected) {
                        selectedItemWith(this)
                        showShapeSelectLayout(vh, canvasView)
                    }
                }
            }
            AddDoodleItem()() {
                itemEnable = true
                itemClick = {
                    toast("功能开发中...")
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_barcode_ico
                itemText = _string(R.string.canvas_barcode)
                itemClick = {
                    inputBarcode(canvasView, null)
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_qrcode_ico
                itemText = _string(R.string.canvas_qrcode)
                itemClick = {
                    inputQrCode(canvasView, null)
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_edit_ico
                itemText = _string(R.string.canvas_edit)
                itemEnable = true
                itemClick = {
                    vh.showControlLayout(!itemIsSelected)
                    itemIsSelected = !itemIsSelected
                    updateAdapterItem()

                    if (itemIsSelected) {
                        selectedItemWith(this)
                        showEditControlLayout(
                            vh,
                            canvasView,
                            canvasView.canvasDelegate.getSelectedRenderer()
                        )
                    }
                }
            }
            CanvasControlItem()() {
                _layerCanvasItem = this
                itemIco = R.drawable.canvas_layer_ico
                itemText = _string(R.string.canvas_layer)
                itemEnable = true
                itemClick = {
                    vh.gone(R.id.canvas_layer_layout, itemIsSelected)
                    itemIsSelected = !itemIsSelected
                    updateAdapterItem()

                    if (itemIsSelected) {
                        showLayerControlLayout(vh, canvasView)
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_actions_ico
                itemText = _string(R.string.canvas_operate)
                itemEnable = true
                itemClick = {
                    canvasView.canvasDelegate.getSelectedRenderer()?.let { renderer ->
                        if (renderer is PictureItemRenderer) {
                            renderer.getRendererItem()?.let { item ->
                                if (item is PictureShapeItem) {
                                    fragment.loadingAsync({
                                        item.shapePath?.let { path ->
                                            EngraveHelper.pathStrokeToGCode(
                                                path,
                                                renderer.getRotateBounds(),
                                                renderer.rotate,
                                                filePath(
                                                    "GCode",
                                                    fileName(suffix = ".gcode")
                                                ).file()
                                            )
                                        }
                                    }) {
                                        //no op
                                    }
                                }
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_setting_ico
                itemText = _string(R.string.canvas_setting)
                itemEnable = true

                itemClick = {
                    itemIsSelected = !itemIsSelected
                    updateAdapterItem()

                    if (itemIsSelected) {
                        it.context.canvasSettingWindow(it) {
                            canvasDelegate = canvasView.canvasDelegate
                            onDismiss = {
                                itemIsSelected = false
                                updateAdapterItem()
                                false
                            }
                        }
                    }
                }
            }
        }

        initCanvasListener(vh, canvasView)
    }

    /**输入条码*/
    fun inputBarcode(canvasView: CanvasView?, itemRenderer: PictureItemRenderer?) {
        fragment.context?.inputDialog {
            dialogTitle = _string(R.string.canvas_barcode)
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            digits = _string(R.string.lib_barcode_digits)
            maxInputLength = AddTextItem.MAX_INPUT_LENGTH
            defaultInputString = itemRenderer?._rendererItem?.data as CharSequence?
            onInputResult = { dialog, inputText ->
                if (itemRenderer == null) {
                    //添加条码
                    if (inputText.isNotEmpty()) {
                        inputText.createBarCode()?.let {
                            canvasView?.addPictureBitmapRenderer(it)?.apply {
                                tag = "barcode"
                                data = inputText
                            }
                        }
                    }
                } else {
                    //修改条码
                    val renderItem = itemRenderer._rendererItem
                    if (inputText.isNotEmpty()) {
                        inputText.createBarCode()?.let {
                            if (renderItem is PictureBitmapItem) {
                                renderItem.originBitmap = it
                            }
                            renderItem?.data = inputText
                            itemRenderer.updateItemBitmap(it)
                        }
                    }
                }

                false
            }
        }
    }

    /**输入二维码*/
    fun inputQrCode(canvasView: CanvasView?, itemRenderer: PictureItemRenderer?) {
        fragment.context?.inputDialog {
            dialogTitle = _string(R.string.canvas_qrcode)
            maxInputLength = AddTextItem.MAX_INPUT_LENGTH
            defaultInputString = itemRenderer?._rendererItem?.data as CharSequence?
            onInputResult = { dialog, inputText ->
                if (itemRenderer == null) {
                    if (inputText.isNotEmpty()) {
                        inputText.createQRCode()?.let {
                            canvasView?.addPictureBitmapRenderer(it)?.apply {
                                tag = "qrcode"
                                data = inputText
                            }
                        }
                    }
                } else {
                    val renderItem = itemRenderer._rendererItem
                    if (inputText.isNotEmpty()) {
                        inputText.createQRCode()?.let {
                            if (renderItem is PictureBitmapItem) {
                                renderItem.originBitmap = it
                            }
                            renderItem?.data = inputText
                            itemRenderer.updateItemBitmap(it)
                        }
                    }
                }
                false
            }
        }

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
                        defaultInputString = itemRenderer._rendererItem?.text
                        maxInputLength = AddTextItem.MAX_INPUT_LENGTH
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
                        defaultInputString = itemRenderer._rendererItem?.text
                        maxInputLength = AddTextItem.MAX_INPUT_LENGTH
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
                    val renderItem = itemRenderer._rendererItem
                    if (renderItem is PictureTextItem) {
                        fragment.context?.inputDialog {
                            inputViewHeight = 100 * dpi
                            defaultInputString = renderItem.text
                            maxInputLength = AddTextItem.MAX_INPUT_LENGTH
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
                            inputBarcode(canvasView, itemRenderer)
                        } else if (renderItem.tag == "qrcode") {
                            //二维码
                            inputQrCode(canvasView, itemRenderer)
                        } else {
                            //图片
                        }
                    }
                }
            }

            override fun onItemRendererAdd(itemRenderer: IItemRenderer<*>) {
                super.onItemRendererAdd(itemRenderer)
                if (itemRenderer is BaseItemRenderer<*>) {
                    addLayerItem(vh, canvasView, itemRenderer)
                }
            }

            override fun onItemRendererRemove(itemRenderer: IItemRenderer<*>) {
                super.onItemRendererRemove(itemRenderer)
                if (itemRenderer is BaseItemRenderer<*>) {
                    removeLayerItem(vh, canvasView, itemRenderer)
                }
            }

            override fun onItemVisibleChanged(itemRenderer: IRenderer, visible: Boolean) {
                super.onItemVisibleChanged(itemRenderer, visible)
                updateLayerLayout(vh)
            }

            override fun onItemBoundsChanged(item: IRenderer, reason: Reason, oldBounds: RectF) {
                super.onItemBoundsChanged(item, reason, oldBounds)
                updateControlLayout(vh, canvasView)
                updateLayerLayout(vh)

                val peckerModel = vmApp<LaserPeckerModel>()
                if (peckerModel.deviceModelData.value == QueryStateParser.WORK_MODE_ENGRAVE_PREVIEW &&
                    item == canvasView.canvasDelegate.getSelectedRenderer()
                ) {
                    //设备正在预览模式, 更新预览
                    if (item is BaseItemRenderer<*>) {
                        peckerModel.sendUpdatePreviewRange(item.getRotateBounds())
                    }
                }
            }

            override fun onItemLockScaleRatioChanged(item: BaseItemRenderer<*>) {
                super.onItemLockScaleRatioChanged(item)
                updateControlLayout(vh, canvasView)
            }

            override fun onItemSortChanged(itemList: List<BaseItemRenderer<*>>) {
                super.onItemSortChanged(itemList)
                updateControlLayout(vh, canvasView)
                showLayerControlLayout(vh, canvasView)
            }

            override fun onClearSelectItem(itemRenderer: IItemRenderer<*>) {
                super.onClearSelectItem(itemRenderer)
                cancelSelectedItem()
                vh.showControlLayout(false)
                updateLayerLayout(vh)
            }

            override fun onSelectedItem(
                itemRenderer: IItemRenderer<*>,
                oldItemRenderer: IItemRenderer<*>?
            ) {
                super.onSelectedItem(itemRenderer, oldItemRenderer)
                if (itemRenderer == oldItemRenderer) {
                    //重复选择
                    return
                }
                cancelSelectedItem()
                vh.showControlLayout()
                updateLayerLayout(vh)
                if (itemRenderer is TextItemRenderer || itemRenderer is PictureTextItemRenderer) {
                    //选中TextItemRenderer时的控制菜单
                    showTextControlLayoutOld(vh, canvasView, itemRenderer)
                } else if (itemRenderer is PictureItemRenderer) {
                    val renderItem = itemRenderer._rendererItem
                    if (renderItem is PictureTextItem) {
                        //选中TextItemRenderer时的控制菜单
                        showTextControlLayout(vh, canvasView, itemRenderer)
                    } else if (renderItem is PictureShapeItem) {
                        showShapeControlLayout(vh, canvasView, itemRenderer)
                    } else if (renderItem is PictureBitmapItem) {
                        showBitmapControlLayout(vh, canvasView, itemRenderer)
                    } else {
                        vh.showControlLayout(false)
                    }
                } else if (itemRenderer is BitmapItemRenderer) {
                    showBitmapControlLayout(vh, canvasView, itemRenderer)
                } else if (itemRenderer is SelectGroupRenderer) {
                    showGroupControlLayout(vh, canvasView, itemRenderer)
                } else {
                    vh.showControlLayout(false)
                }

                //预览选中的元素边框
                val peckerModel = vmApp<LaserPeckerModel>()
                if (peckerModel.deviceModelData.value == QueryStateParser.WORK_MODE_ENGRAVE_PREVIEW) {
                    //设备正在预览模式, 更新预览
                    if (itemRenderer is BaseItemRenderer<*>) {
                        peckerModel.sendUpdatePreviewRange(itemRenderer.getRotateBounds())
                    }
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

            override fun onCanvasInterceptTouchEvent(
                canvasDelegate: CanvasDelegate,
                event: MotionEvent
            ): Boolean {
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    //点击画布, 隐藏图层布局
                    vh.gone(R.id.canvas_layer_layout, true)
                    _layerCanvasItem?.apply {
                        itemIsSelected = false
                        updateAdapterItem()
                    }
                }
                return super.onCanvasInterceptTouchEvent(canvasDelegate, event)
            }
        })
    }

    /**隐藏控制布局*/
    fun DslViewHolder.showControlLayout(visible: Boolean = true) {
        dslTransition(itemView as ViewGroup) {
            onCaptureStartValues = {
                //invisible(R.id.canvas_control_layout)
            }
            onCaptureEndValues = {
                visible(R.id.canvas_control_layout, visible)
            }
        }
    }

    /**显示形状选择布局*/
    fun showShapeSelectLayout(vh: DslViewHolder, canvasView: CanvasView) {
        vh.rv(R.id.canvas_control_view)?.renderDslAdapter {
            hookUpdateDepend()
            ShapeLineItem(canvasView)()
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_line_ico
                itemText = _string(R.string.canvas_line)
                shapePath = ShapesHelper.linePath()
            }
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_circle_ico
                itemText = _string(R.string.canvas_circle)
                shapePath = ShapesHelper.circlePath()
            }
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_triangle_ico
                itemText = _string(R.string.canvas_triangle)
                shapePath = ShapesHelper.trianglePath()
            }
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_square_ico
                itemText = _string(R.string.canvas_square)
                shapePath = ShapesHelper.squarePath()
            }
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_pentagon_ico
                itemText = _string(R.string.canvas_pentagon)
                shapePath = ShapesHelper.pentagonPath()
            }
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_hexagon_ico
                itemText = _string(R.string.canvas_hexagon)
                shapePath = ShapesHelper.hexagonPath()
            }
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_octagon_ico
                itemText = _string(R.string.canvas_octagon)
                shapePath = ShapesHelper.octagonPath()
            }
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_rhombus_ico
                itemText = _string(R.string.canvas_rhombus)
                shapePath = ShapesHelper.rhombusPath()
            }
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_pentagram_ico
                itemText = _string(R.string.canvas_pentagram)
                shapePath = ShapesHelper.pentagramPath()
            }
            ShapeItem(canvasView)() {
                itemIco = R.drawable.canvas_shape_love_ico
                itemText = _string(R.string.canvas_love)
                shapePath = ShapesHelper.lovePath()
            }
        }
    }

    //<editor-fold desc="文本属性控制">

    /**更新文本样式和其他控制布局*/
    fun updateControlLayout(vh: DslViewHolder, canvasView: CanvasView) {
        if (vh.isVisible(R.id.canvas_control_layout)) {
            vh.rv(R.id.canvas_control_view)?._dslAdapter?.apply {
                eachItem { index, dslAdapterItem ->
                    if (dslAdapterItem is CanvasEditControlItem) {
                        dslAdapterItem.itemRenderer =
                            canvasView.canvasDelegate.getSelectedRenderer()
                        dslAdapterItem.itemCanvasDelegate = canvasView.canvasDelegate
                    } else if (dslAdapterItem is CanvasArrangeItem) {
                        dslAdapterItem.itemRenderer =
                            canvasView.canvasDelegate.getSelectedRenderer()
                        dslAdapterItem.itemCanvasDelegate = canvasView.canvasDelegate
                    }
                }
                updateAllItem()
            }
        }
    }

    /**文本属性控制item*/
    fun showTextControlLayoutOld(
        vh: DslViewHolder,
        canvasView: CanvasView,
        itemRenderer: IItemRenderer<*>
    ) {
        vh.rv(R.id.canvas_control_view)?.renderDslAdapter {
            hookUpdateDepend()
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
                    showFontSelectLayout(this, it, itemRenderer)
                }
            }

            CanvasIconItem()() {
                itemIco = R.drawable.canvas_text_style_standard_ico
                itemClick = {
                    if (itemRenderer is PictureItemRenderer) {
                        val renderItem = itemRenderer._rendererItem
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
                        val renderItem = itemRenderer._rendererItem
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
                        val renderItem = itemRenderer._rendererItem
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
                        val renderItem = itemRenderer._rendererItem
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
                        val renderItem = itemRenderer._rendererItem
                        if (renderItem is PictureTextItem) {
                            itemRenderer.updatePaintAlign(Paint.Align.RIGHT)
                        }
                    }
                }
            }
        }
    }

    fun DslAdapterItem.drawCanvasRight(
        insertRight: Int = _dimen(R.dimen.lib_line_px),
        offsetTop: Int = _dimen(R.dimen.lib_xhdpi),
        offsetBottom: Int = _dimen(R.dimen.lib_xhdpi),
        color: Int = _color(R.color.canvas_dark_gray)
    ) {
        drawRight(insertRight, offsetTop, offsetBottom, color)
    }

    /**统一样式的item*/
    fun showTextControlLayout(
        vh: DslViewHolder,
        canvasView: CanvasView,
        renderer: BaseItemRenderer<*>
    ) {
        vh.rv(R.id.canvas_control_view)?.renderDslAdapter {
            hookUpdateDepend()
            TextStrokeStyleItem()() {
                itemIco = R.drawable.canvas_text_style_solid
                itemText = _string(R.string.canvas_solid)
                itemStyle = Paint.Style.FILL
                itemRenderer = renderer
            }
            TextStrokeStyleItem()() {
                itemIco = R.drawable.canvas_text_style_stroke
                itemText = _string(R.string.canvas_hollow)
                itemStyle = Paint.Style.STROKE
                itemRenderer = renderer
                drawCanvasRight()
            }

            TextStyleItem()() {
                itemIco = R.drawable.canvas_text_bold_style_ico
                itemText = _string(R.string.canvas_bold)
                itemStyle = PictureTextItem.TEXT_STYLE_BOLD
                itemRenderer = renderer
            }
            TextStyleItem()() {
                itemIco = R.drawable.canvas_text_italic_style_ico
                itemText = _string(R.string.canvas_italic)
                itemStyle = PictureTextItem.TEXT_STYLE_ITALIC
                itemRenderer = renderer
            }
            TextStyleItem()() {
                itemIco = R.drawable.canvas_text_under_line_style_ico
                itemText = _string(R.string.canvas_under_line)
                itemStyle = PictureTextItem.TEXT_STYLE_UNDER_LINE
                itemRenderer = renderer
            }
            TextStyleItem()() {
                itemIco = R.drawable.canvas_text_delete_line_style_ico
                itemText = _string(R.string.canvas_delete_line)
                itemStyle = PictureTextItem.TEXT_STYLE_DELETE_LINE
                itemRenderer = renderer
                drawCanvasRight()
            }

            CanvasControlItem()() {
                itemIco = R.drawable.canvas_text_font_ico
                itemText = _string(R.string.canvas_font)
                itemClick = {
                    itemIsSelected = !itemIsSelected
                    updateAdapterItem()

                    if (itemIsSelected) {
                        showFontSelectLayout(this, it, renderer)
                    }
                }
                drawCanvasRight()
            }

            TextOrientationItem()() {
                itemIco = R.drawable.canvas_text_style_standard_ico
                itemText = _string(R.string.canvas_standard)
                itemOrientation = LinearLayout.HORIZONTAL
                itemRenderer = renderer
            }
            TextOrientationItem()() {
                itemIco = R.drawable.canvas_text_style_vertical_ico
                itemText = _string(R.string.canvas_vertical)
                itemOrientation = LinearLayout.VERTICAL
                itemRenderer = renderer
                drawCanvasRight()
            }

            TextAlignItem()() {
                itemIco = R.drawable.canvas_text_style_align_left_ico
                itemText = _string(R.string.canvas_align_left)
                itemAlign = Paint.Align.LEFT
                itemRenderer = renderer
            }
            TextAlignItem()() {
                itemIco = R.drawable.canvas_text_style_align_center_ico
                itemText = _string(R.string.canvas_align_center)
                itemAlign = Paint.Align.CENTER
                itemRenderer = renderer
            }
            TextAlignItem()() {
                itemIco = R.drawable.canvas_text_style_align_right_ico
                itemText = _string(R.string.canvas_align_right)
                itemAlign = Paint.Align.RIGHT
                itemRenderer = renderer
            }
        }
    }

    /**显示字体选择布局*/
    fun showFontSelectLayout(
        dslAdapterItem: DslAdapterItem,
        anchor: View,
        renderer: IItemRenderer<*>
    ) {
        anchor.context.canvasFontWindow(anchor) {
            itemRenderer = renderer
            onDismiss = {
                dslAdapterItem.itemIsSelected = false
                dslAdapterItem.updateAdapterItem()
                false
            }
        }
    }

    //</editor-fold desc="文本属性控制">

    //<editor-fold desc="形状控制">

    /**形状属性控制item*/
    fun showShapeControlLayout(
        vh: DslViewHolder,
        canvasView: CanvasView,
        itemRenderer: IItemRenderer<*>
    ) {
        vh.rv(R.id.canvas_control_view)?.renderDslAdapter {
            hookUpdateDepend()
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_style_stroke_ico
                itemText = _string(R.string.canvas_stroke)
                itemTintColor = false
                itemClick = {
                    if (itemRenderer is PictureItemRenderer) {
                        itemRenderer.updatePaintStyle(Paint.Style.STROKE)
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_style_fill_ico
                itemText = _string(R.string.canvas_fill)
                itemTintColor = false
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
    fun showBitmapControlLayout(
        vh: DslViewHolder,
        canvasView: CanvasView,
        renderer: IItemRenderer<*>
    ) {
        vh.rv(R.id.canvas_control_view)?.renderDslAdapter {
            hookUpdateDepend()
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_prints
                itemText = _string(R.string.canvas_prints)
                itemTintColor = false
                itemClick = {
                    val originBitmap = renderer.getRenderBitmap()
                    val beforeBitmap = renderer.getRenderBitmap(false)
                    it.context.canvasRegulateWindow(it) {
                        itemRenderer = renderer
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_THRESHOLD)
                        onApplyAction = { preview, cancel ->
                            if (cancel) {
                                beforeBitmap?.let {
                                    renderer.updateRenderBitmap(it, Strategy.redo)
                                }
                            } else {
                                fragment.loadingAsync({
                                    originBitmap?.let { bitmap ->
                                        OpenCV.bitmapToPrint(
                                            fragment.requireContext(),
                                            bitmap,
                                            getIntOrDef(
                                                CanvasRegulatePopupConfig.KEY_THRESHOLD,
                                                240
                                            )
                                        )
                                    }
                                }) {
                                    it?.let {
                                        renderer.updateRenderBitmap(
                                            it,
                                            if (preview) Strategy.preview else Strategy.normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_gcode
                itemText = _string(R.string.canvas_gcode)
                itemTintColor = false
                itemClick = {
                    val originBitmap = renderer.getRenderBitmap()
                    val beforeBitmap = renderer.getRenderBitmap(false)
                    it.context.canvasRegulateWindow(it) {
                        itemRenderer = renderer
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_LINE_SPACE)
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_ANGLE)
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_DIRECTION)
                        onApplyAction = { preview, cancel ->
                            if (cancel) {
                                beforeBitmap?.let {
                                    renderer.updateRenderBitmap(it, Strategy.redo)
                                }
                            } else {
                                fragment.loadingAsync({
                                    originBitmap?.let { bitmap ->
                                        OpenCV.bitmapToGCode(
                                            fragment.requireContext(),
                                            bitmap,
                                            lineSpace = getFloatOrDef(
                                                CanvasRegulatePopupConfig.KEY_LINE_SPACE,
                                                0.125f
                                            ).toDouble(),
                                            direction = getIntOrDef(
                                                CanvasRegulatePopupConfig.KEY_DIRECTION,
                                                0
                                            ),
                                            angle = getFloatOrDef(
                                                CanvasRegulatePopupConfig.KEY_ANGLE,
                                                0f
                                            ).toDouble()
                                        ).let {
                                            val gCodeText = it.readText()
                                            gCodeText to GCodeHelper.parseGCode(
                                                fragment.requireContext(),
                                                gCodeText
                                            )
                                        }
                                    }
                                }) {
                                    it?.let {
                                        renderer.updateItemDrawable(
                                            it.second,
                                            if (preview) Strategy.preview else Strategy.normal,
                                            hashMapOf(EngraveHelper.KEY_GCODE to it.first)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_black_white
                itemText = _string(R.string.canvas_black_white)
                itemTintColor = false
                itemClick = {
                    val originBitmap = renderer.getRenderBitmap()
                    val beforeBitmap = renderer.getRenderBitmap(false)
                    it.context.canvasRegulateWindow(it) {
                        itemRenderer = renderer
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_INVERT)
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_THRESHOLD)
                        onApplyAction = { preview, cancel ->
                            if (cancel) {
                                beforeBitmap?.let {
                                    renderer.updateRenderBitmap(it, Strategy.redo)
                                }
                            } else {
                                fragment.loadingAsync({
                                    originBitmap?.let { bitmap ->
                                        OpenCV.bitmapToBlackWhite(
                                            bitmap,
                                            getIntOrDef(
                                                CanvasRegulatePopupConfig.KEY_THRESHOLD,
                                                240
                                            ),
                                            if (getBooleanOrDef(
                                                    CanvasRegulatePopupConfig.KEY_INVERT, false
                                                )
                                            ) {
                                                1
                                            } else {
                                                0
                                            }
                                        )
                                    }
                                }) {
                                    it?.let {
                                        renderer.updateRenderBitmap(
                                            it,
                                            if (preview) Strategy.preview else Strategy.normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_dithering
                itemText = _string(R.string.canvas_dithering)
                itemTintColor = false
                itemClick = {
                    val originBitmap = renderer.getRenderBitmap()
                    val beforeBitmap = renderer.getRenderBitmap(false)
                    it.context.canvasRegulateWindow(it) {
                        itemRenderer = renderer
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_INVERT)
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_CONTRAST)
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_BRIGHTNESS)
                        onApplyAction = { preview, cancel ->
                            if (cancel) {
                                beforeBitmap?.let {
                                    renderer.updateRenderBitmap(it, Strategy.redo)
                                }
                            } else {
                                fragment.loadingAsync({
                                    originBitmap?.let { bitmap ->
                                        OpenCV.bitmapToDithering(
                                            fragment.requireContext(), bitmap,
                                            getBooleanOrDef(
                                                CanvasRegulatePopupConfig.KEY_INVERT, false
                                            ),
                                            getFloatOrDef(
                                                CanvasRegulatePopupConfig.KEY_CONTRAST, 0f
                                            ).toDouble(),
                                            getFloatOrDef(
                                                CanvasRegulatePopupConfig.KEY_BRIGHTNESS, 0f
                                            ).toDouble(),
                                        )
                                    }
                                }) {
                                    it?.let {
                                        renderer.updateRenderBitmap(
                                            it,
                                            if (preview) Strategy.preview else Strategy.normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_grey
                itemText = _string(R.string.canvas_grey)
                itemTintColor = false
                itemClick = {
                    fragment.loadingAsync({
                        renderer.getRenderBitmap()?.let { bitmap ->
                            OpenCV.bitmapToGrey(bitmap)
                        }
                    }) {
                        it?.let {
                            renderer.updateRenderBitmap(it)
                        }
                    }
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_bitmap_seal
                itemText = _string(R.string.canvas_seal)
                itemTintColor = false
                itemClick = {
                    val originBitmap = renderer.getRenderBitmap()
                    val beforeBitmap = renderer.getRenderBitmap(false)
                    it.context.canvasRegulateWindow(it) {
                        itemRenderer = renderer
                        addRegulate(CanvasRegulatePopupConfig.REGULATE_THRESHOLD)
                        onApplyAction = { preview, cancel ->
                            if (cancel) {
                                beforeBitmap?.let {
                                    renderer.updateRenderBitmap(it, Strategy.redo)
                                }
                            } else {
                                fragment.loadingAsync({
                                    originBitmap?.let { bitmap ->
                                        OpenCV.bitmapToSeal(
                                            fragment.requireContext(), bitmap,
                                            getIntOrDef(
                                                CanvasRegulatePopupConfig.KEY_THRESHOLD,
                                                240
                                            )
                                        )
                                    }
                                }) {
                                    it?.let {
                                        renderer.updateRenderBitmap(
                                            it,
                                            if (preview) Strategy.preview else Strategy.normal
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //</editor-fold desc="图片控制">

    //<editor-fold desc="图层控制">

    /**更新图层布局*/
    fun updateLayerLayout(vh: DslViewHolder) {
        if (vh.isVisible(R.id.canvas_layer_layout)) {
            vh.rv(R.id.canvas_layer_view)?._dslAdapter?.updateAllItem()
        }
    }

    /**移除一个渲染图层*/
    fun removeLayerItem(vh: DslViewHolder, canvasView: CanvasView, item: IRenderer) {
        if (vh.isVisible(R.id.canvas_layer_layout)) {
            vh.rv(R.id.canvas_layer_view)?._dslAdapter?.apply {
                render {
                    eachItem { index, dslAdapterItem ->
                        if (dslAdapterItem is CanvasLayerItem && dslAdapterItem.itemRenderer == item) {
                            dslAdapterItem.removeAdapterItem()
                        }
                    }
                    if (canvasView.canvasDelegate.itemsRendererList.isEmpty()) {
                        setAdapterStatus(DslAdapterStatusItem.ADAPTER_STATUS_EMPTY)
                    }
                }
            }
        }
    }

    /**添加一个渲染图层*/
    fun addLayerItem(
        vh: DslViewHolder,
        canvasView: CanvasView,
        item: BaseItemRenderer<*>
    ) {
        if (vh.isVisible(R.id.canvas_layer_layout)) {
            vh.rv(R.id.canvas_layer_view)?._dslAdapter?.apply {
                render {
                    CanvasLayerItem()() {
                        itemCanvasDelegate = canvasView.canvasDelegate
                        itemRenderer = item
                    }
                    setAdapterStatus(DslAdapterStatusItem.ADAPTER_STATUS_NONE)
                }
            }
        }
    }

    /**显示图层item*/
    fun showLayerControlLayout(vh: DslViewHolder, canvasView: CanvasView) {
        vh.rv(R.id.canvas_layer_view)?.renderDslAdapter {
            hookUpdateDepend()
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

    //<editor-fold desc="编辑控制">

    /**编辑控制*/
    fun showEditControlLayout(
        vh: DslViewHolder,
        canvasView: CanvasView,
        renderer: BaseItemRenderer<*>?
    ) {
        vh.rv(R.id.canvas_control_view)?.renderDslAdapter {
            hookUpdateDepend()

            //坐标编辑
            CanvasEditControlItem()() {
                itemRenderer = renderer
                itemCanvasDelegate = canvasView.canvasDelegate
            }

            //图层排序
            CanvasArrangeItem()() {
                itemArrange = CanvasDelegate.ARRANGE_FORWARD
                itemRenderer = renderer
                itemCanvasDelegate = canvasView.canvasDelegate
            }
            CanvasArrangeItem()() {
                itemArrange = CanvasDelegate.ARRANGE_BACKWARD
                itemRenderer = renderer
                itemCanvasDelegate = canvasView.canvasDelegate
            }
            CanvasArrangeItem()() {
                itemArrange = CanvasDelegate.ARRANGE_FRONT
                itemRenderer = renderer
                itemCanvasDelegate = canvasView.canvasDelegate
            }
            CanvasArrangeItem()() {
                itemArrange = CanvasDelegate.ARRANGE_BACK
                itemRenderer = renderer
                itemCanvasDelegate = canvasView.canvasDelegate
            }
        }
    }

    //</editor-fold desc="编辑控制">

    //<editor-fold desc="群组控制">

    /**群组控制*/
    fun showGroupControlLayout(
        vh: DslViewHolder,
        canvasView: CanvasView,
        renderer: SelectGroupRenderer
    ) {
        vh.rv(R.id.canvas_control_view)?.renderDslAdapter {
            hookUpdateDepend()
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_text_style_align_left_ico
                itemText = _string(R.string.canvas_align_left)
                itemRenderer = renderer
                itemClick = {
                    renderer.updateAlign(Gravity.LEFT)
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_text_style_align_center_ico
                itemText = _string(R.string.canvas_align_center)
                itemRenderer = renderer
                itemClick = {
                    renderer.updateAlign(Gravity.CENTER_HORIZONTAL)
                }
            }
            CanvasControlItem()() {
                itemIco = R.drawable.canvas_text_style_align_right_ico
                itemText = _string(R.string.canvas_align_right)
                itemRenderer = renderer
                itemClick = {
                    renderer.updateAlign(Gravity.RIGHT)
                }
            }
        }
    }
    //</editor-fold desc="群组控制">
}