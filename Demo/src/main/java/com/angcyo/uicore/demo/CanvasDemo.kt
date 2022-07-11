package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bluetooth.fsc.IReceiveBeanAction
import com.angcyo.bluetooth.fsc.enqueue
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.command.EngravePreviewCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.ExitCmd
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryStateParser
import com.angcyo.bluetooth.fsc.parse
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.Strategy
import com.angcyo.canvas.core.InchValueUnit
import com.angcyo.canvas.core.MmValueUnit
import com.angcyo.canvas.core.PixelValueUnit
import com.angcyo.canvas.items.PictureTextItem
import com.angcyo.canvas.items.renderer.ShapeItemRenderer
import com.angcyo.canvas.items.setHoldData
import com.angcyo.canvas.laser.pecker.CanvasLayoutHelper
import com.angcyo.canvas.laser.pecker.loadingAsync
import com.angcyo.canvas.utils.*
import com.angcyo.core.component.dslPermissions
import com.angcyo.core.vmApp
import com.angcyo.dialog.messageDialog
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.bindItem
import com.angcyo.engrave.EngraveHelper
import com.angcyo.engrave.EngraveLayoutHelper
import com.angcyo.engrave.EngravePreviewLayoutHelper
import com.angcyo.engrave.ProductLayoutHelper
import com.angcyo.engrave.ble.DeviceConnectTipActivity
import com.angcyo.engrave.ble.DeviceSettingFragment
import com.angcyo.engrave.ble.EngraveHistoryFragment
import com.angcyo.engrave.ble.bluetoothSearchListDialog
import com.angcyo.engrave.data.EngraveReadyDataInfo
import com.angcyo.gcode.GCodeHelper
import com.angcyo.gcode.GCodeWriteHandler
import com.angcyo.http.rx.doMain
import com.angcyo.library.ex.*
import com.angcyo.library.toast
import com.angcyo.svg.Svg
import com.angcyo.uicore.MainFragment.Companion.CLICK_COUNT
import com.angcyo.uicore.activity.FirmwareUpdateActivity
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.SvgDemo.Companion.gCodeNameList
import com.angcyo.uicore.demo.SvgDemo.Companion.svgResList
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.recycler.initDslAdapter
import com.angcyo.widget.span.span
import com.pixplicity.sharp.Sharp
import com.pixplicity.sharp.SharpDrawable
import org.jetbrains.annotations.TestOnly
import kotlin.random.Random

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/01
 */
class CanvasDemo : AppDslFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmApp<FscBleApiModel>().connectDeviceListData.observe {
            if (it.isNullOrEmpty()) {
                fragmentTitle = "未连接设备"
            } else {
                it.first().let { deviceState ->
                    fragmentTitle = span {
                        appendLine(deviceState.device.name)
                        append(deviceState.device.address) {
                            fontSize = 12 * dpi
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        return super.onBackPressed()
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.canvas_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val canvasView = itemHolder.v<CanvasView>(R.id.canvas_view)
                //?.setBgDrawable(_colorDrawable("#20000000".toColorInt()))
                //?.setBgDrawable(CheckerboardDrawable.create())

                //单位恢复
                canvasView?.canvasDelegate?.getCanvasViewBox()
                    ?.updateCoordinateSystemUnit(CanvasConstant.valueUnit)
                //智能指南恢复
                canvasView?.canvasDelegate?.smartAssistant?.enable =
                    CanvasConstant.CANVAS_SMART_ASSISTANT

                //switch_origin_button
                itemHolder.click(R.id.switch_origin_button) {
                    canvasView?.canvasDelegate?.apply {
                        val left = getCanvasViewBox().getContentLeft()
                        val top = getCanvasViewBox().getContentTop()

                        val centerX = getCanvasViewBox().getContentCenterX()
                        val centerY = getCanvasViewBox().getContentCenterY()

                        //更新坐标系
                        if (getCanvasViewBox().coordinateSystemOriginPoint.x == left) {
                            getCanvasViewBox().updateCoordinateSystemOriginPoint(centerX, centerY)

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
                            getCanvasViewBox().updateCoordinateSystemOriginPoint(left, top)

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
                    canvasView?.canvasDelegate?.apply {
                        val valueUnit = getCanvasViewBox().valueUnit
                        getCanvasViewBox().updateCoordinateSystemUnit(
                            when (valueUnit) {
                                is MmValueUnit -> InchValueUnit()
                                is InchValueUnit -> PixelValueUnit()
                                else -> MmValueUnit()
                            }
                        )
                    }
                }
                itemHolder.click(R.id.bounds_button) {
                    canvasView?.canvasDelegate?.apply {
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
                        val renderer = ShapeItemRenderer(this).apply {
                            addRect(limitRect)
                        }
                        addItemRenderer(renderer, Strategy.normal)
                        showRectBounds(limitRect)
                    }
                }
                itemHolder.click(R.id.smart_button) {
                    canvasView?.canvasDelegate?.apply {
                        smartAssistant.enable = !smartAssistant.enable
                        if (smartAssistant.enable) {
                            longFeedback()
                        }
                    }
                }

                itemHolder.click(R.id.translate_x_minus_button) {
                    canvasView?.canvasDelegate?.getCanvasViewBox()?.translateBy(-100f, 0f)
                }
                itemHolder.click(R.id.translate_x_plus_button) {
                    canvasView?.canvasDelegate?.getCanvasViewBox()?.translateBy(100f, 0f)
                }
                itemHolder.click(R.id.translate_y_minus_button) {
                    canvasView?.canvasDelegate?.getCanvasViewBox()?.translateBy(0f, -100f)
                }
                itemHolder.click(R.id.translate_y_plus_button) {
                    canvasView?.canvasDelegate?.getCanvasViewBox()?.translateBy(0f, 100f)
                }
                //放大
                itemHolder.click(R.id.scale_in_button) {
                    canvasView?.canvasDelegate?.getCanvasViewBox()?.scaleBy(1.2f, 1.2f)
                }
                //缩小
                itemHolder.click(R.id.scale_out_button) {
                    canvasView?.canvasDelegate?.getCanvasViewBox()?.scaleBy(.8f, .8f)
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
                itemHolder.click(R.id.add_gcode) {
                    canvasView?.apply {
                        val text = fContext().readAssets("gcode/LaserPecker.gcode")
                        val drawable = GCodeHelper.parseGCode(text)!!
                        addDrawableRenderer(drawable).setHoldData(
                            CanvasDataHandleOperate.KEY_GCODE,
                            text
                        )
                    }
                }
                itemHolder.click(R.id.random_add_svg) {
                    canvasView?.apply {
                        /*loadSvgDrawable().apply {
                            addDrawableRenderer(second)
                        }*/
                        loadSvgPathDrawable().apply {
                            addDrawableRenderer(second).setHoldData(
                                CanvasDataHandleOperate.KEY_SVG,
                                second.pathList
                            )
                        }
                    }
                }
                itemHolder.click(R.id.random_add_gcode) {
                    canvasView?.apply {
                        loadGCodeDrawable().apply {
                            addDrawableRenderer(second).setHoldData(
                                CanvasDataHandleOperate.KEY_GCODE,
                                first
                            )
                        }
                    }
                }

                //preview
                itemHolder.click(R.id.preview_button) { view ->
                    render {
                        PreviewBitmapItem()() {
                            canvasView?.canvasDelegate?.let {
                                bitmap = it.getBitmap(view.tag != null)
                                if (view.tag == null) {
                                    view.tag = nowTime()
                                } else {
                                    view.tag = null
                                }
                            }
                        }
                    }
                }
                itemHolder.click(R.id.preview_rect_button) {
                    render {
                        PreviewBitmapItem()() {
                            canvasView?.canvasDelegate?.let {
                                val left = it.getCanvasViewBox().valueUnit.convertValueToPixel(-10f)
                                val top = it.getCanvasViewBox().valueUnit.convertValueToPixel(-10f)
                                val width = it.getCanvasViewBox().valueUnit.convertValueToPixel(20f)
                                val height =
                                    it.getCanvasViewBox().valueUnit.convertValueToPixel(20f)
                                bitmap = it.getBitmap(left, top, width.toInt(), height.toInt())
                            }
                        }
                    }
                }

                //bluetooth
                itemHolder.click(R.id.bluetooth_button) {
                    dslPermissions(FscBleApiModel.bluetoothPermissionList()) { allGranted, foreverDenied ->
                        if (allGranted) {
                            //vmApp<FscBleApiModel>().connect("DC:0D:30:10:05:E7")
                            fContext().bluetoothSearchListDialog {
                                connectedDismiss = true
                            }
                        } else {
                            toast("蓝牙权限被禁用!")
                        }
                    }
                }

                //雕刻预览
                var cmdString: String = ""
                val receiveAction: IReceiveBeanAction = { bean, error ->
                    val text = span {
                        append(Thread.currentThread().name)
                        append(" ${vmApp<LaserPeckerModel>().productInfoData.value?.name}")
                        append(" ${vmApp<LaserPeckerModel>().deviceVersionData.value?.softwareVersion}")
                        appendln()
                        if (cmdString.isNotEmpty()) {
                            append("发送:${cmdString}")
                        }
                        appendln()
                        error?.let {
                            append("接收:${it.message}")
                        }
                        bean?.let {
                            append("接收:${it.receivePacket.toHexString(true)}")
                            appendln()
                            append("耗时:${it.receiveFinishTime - it.receiveStartTime} ms")
                        }
                    }
                    doMain {
                        itemHolder.tv(R.id.result_text_view)?.text = text
                    }
                    //查询工作状态
                    vmApp<LaserPeckerModel>().queryDeviceState()
                }

                itemHolder.click(R.id.engrave_preview_button) {
                    //安全提示弹窗
                    fContext().messageDialog {
                        dialogMessageLeftIco = _drawable(com.angcyo.engrave.R.mipmap.safe_tips)
                        dialogTitle = _string(com.angcyo.engrave.R.string.size_safety_tips)
                        dialogMessage = _string(com.angcyo.engrave.R.string.size_safety_content)
                        negativeButtonText = _string(com.angcyo.engrave.R.string.dialog_negative)

                        positiveButton { dialog, dialogViewHolder ->
                            dialog.dismiss()
                            engravePreviewLayoutHelper.canvasDelegate = canvasView?.canvasDelegate
                            engravePreviewLayoutHelper.show(itemHolder.group(R.id.lib_content_wrap_layout))
                        }
                    }
                }

                //结束预览
                itemHolder.click(R.id.preview_stop_button) {
                    val cmd = EngravePreviewCmd.previewStop()
                    cmdString = cmd.toHexCommandString()
                    //LaserPeckerHelper.sendCommand(cmd, action = receiveAction)
                    cmd.enqueue(action = receiveAction)
                }

                //退出指令
                itemHolder.click(R.id.exit_button) {
                    val cmd = ExitCmd()
                    cmdString = cmd.toHexCommandString()
                    //LaserPeckerHelper.sendCommand(cmd, action = receiveAction)
                    cmd.enqueue(action = receiveAction)
                }

                //雕刻
                itemHolder.click(R.id.engrave_button) {
                    canvasView?.canvasDelegate?.getSelectedRenderer()?.let { renderer ->
                        /*fContext().engraveDialog(renderer) {

                        }*/

                        engraveLayoutHelper.renderer = renderer
                        engraveLayoutHelper.canvasDelegate = canvasView.canvasDelegate
                        engraveLayoutHelper.show(itemHolder.group(R.id.lib_content_wrap_layout))
                    }
                }

                //状态
                itemHolder.click(R.id.state_button) {
                    vmApp<LaserPeckerModel>().queryDeviceState { bean, error ->
                        bean?.parse<QueryStateParser>()?.let {
                            doMain {
                                itemHolder.tv(R.id.result_text_view)?.text = "$it"
                            }
                        }
                    }
                }
                itemHolder.click(R.id.version_button) {
                    vmApp<LaserPeckerModel>().productInfoData.value?.let {
                        itemHolder.tv(R.id.result_text_view)?.text = "$it"
                    }
                }

                //历史
                itemHolder.click(R.id.file_button) {
                    dslFHelper {
                        show(EngraveHistoryFragment::class)
                    }
                    /*QueryCmd.fileList.sendCommand { bean, error ->
                        doMain {
                            bean?.parse<QueryEngraveFileParser>()?.let {
                                fContext().itemsDialog {
                                    dialogTitle = "打印历史"
                                    it.nameList?.forEach { name ->
                                        addDialogItem {
                                            itemText = "$name"
                                            itemClick = {
                                                //开始雕刻
                                                EngraveCmd(name).sendCommand { bean, error ->
                                                    doMain {
                                                        _dialog?.dismiss()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }*/
                }

                //设置
                itemHolder.click(R.id.setting_button) {
                    dslFHelper {
                        show(DeviceSettingFragment::class)
                    }
                }

                //预处理数据
                itemHolder.click(R.id.preproccess_button) {
                    canvasView?.canvasDelegate?.getSelectedRenderer()?.let { renderer ->
                        loadingAsync({
                            EngraveHelper.handleEngraveData(renderer, EngraveReadyDataInfo())
                        })
                    }
                }

                //test
                itemHolder.click(R.id.test_button) {
                    //test(itemHolder, canvasView)
                    dslAHelper {
                        start(FirmwareUpdateActivity::class)
                    }
                }

                //canvas
                bindCanvasRecyclerView(itemHolder, adapterItem)

                //product
                productLayoutHelper.bindCanvasView(itemHolder.v<CanvasView>(R.id.canvas_view)!!)

                //engrave
                engraveLayoutHelper.bindDeviceState(itemHolder.itemView as ViewGroup)

                //test
                //canvasView?.canvasDelegate?.engraveMode()
            }
        }
    }

    fun getRandomText() =
        "angcyo${randomString(Random.nextInt(0, 3))}\n${randomString(Random.nextInt(0, 3))}"

    fun loadSvgDrawable(): Pair<String, SharpDrawable> {
        val resId = svgResList.randomGetOnce()!!
        val text = fContext().readResource(resId)
        return text!! to Sharp.loadString(text).drawable
    }

    fun loadSvgPathDrawable(): Pair<String, SharpDrawable> {
        val resId = svgResList.randomGetOnce()!!
        val text = fContext().readResource(resId)
        return text!! to Svg.loadSvgPathDrawable(text, Color.BLACK, Paint.Style.STROKE)
    }

    fun loadGCodeDrawable(): Pair<String, Drawable> {
        val text = fContext().readAssets(gCodeNameList.randomGetOnce()!!)
        return text!! to GCodeHelper.parseGCode(text)!!
    }

    @TestOnly
    fun test(viewHolder: DslViewHolder, canvasView: CanvasView?) {
        canvasView?.canvasDelegate?.getSelectedRenderer()?.let { renderer ->
            val text = renderer.getGCodeText()
            if (!text.isNullOrEmpty()) {
                //GCode
                loadingAsync({
                    CanvasDataHandleOperate.gCodeAdjust(
                        text,
                        renderer.getBounds(),
                        renderer.rotate
                    )
                }) {
                    //no
                    it?.readText()?.let { gCode ->
                        canvasView.addDrawableRenderer(GCodeHelper.parseGCode(gCode)!!)
                            .setHoldData(CanvasDataHandleOperate.KEY_GCODE, gCode)
                    }
                }
            } else {
                val pathList = renderer.getPathList()
                if (!pathList.isNullOrEmpty()) {
                    //path list
                    loadingAsync({
                        CanvasDataHandleOperate.pathStrokeToGCode(
                            pathList,
                            renderer.getBounds(),
                            renderer.rotate
                        )
                    }) {
                        //no
                        it?.readText()?.let { gCode ->
                            canvasView.addDrawableRenderer(GCodeHelper.parseGCode(gCode)!!)
                                .setHoldData(CanvasDataHandleOperate.KEY_GCODE, gCode)
                        }
                    }
                } else {
                    //bitmap to gcode
                    val lineSpace = if (renderer.getRendererItem() is PictureTextItem) {
                        GCodeWriteHandler.GCODE_SPACE_4K
                    } else {
                        GCodeWriteHandler.GCODE_SPACE_1K
                    }
                    val bitmap = renderer.preview()?.toBitmap()
                    loadingAsync({
                        if (bitmap != null) {
                            CanvasDataHandleOperate.bitmapToGCode(bitmap, Gravity.LEFT, lineSpace)
                            CanvasDataHandleOperate.bitmapToGCode(bitmap, Gravity.TOP, lineSpace)
                            CanvasDataHandleOperate.bitmapToGCode(bitmap, Gravity.RIGHT, lineSpace)
                            CanvasDataHandleOperate.bitmapToGCode(bitmap, Gravity.BOTTOM, lineSpace)
                        } else {
                            null
                        }
                    }) {
                        //no
                        it?.readText()?.let { gCode ->
                            canvasView.addDrawableRenderer(GCodeHelper.parseGCode(gCode)!!)
                                .setHoldData(CanvasDataHandleOperate.KEY_GCODE, gCode)
                        }
                    }
                }
            }
        }.elseNull {
            dslAHelper {
                start(DeviceConnectTipActivity::class)
            }
        }
    }

    //<editor-fold desc="bindCanvasRecyclerView">

    /**Canvas布局*/
    val canvasLayoutHelper = CanvasLayoutHelper(this)

    /**产品布局*/
    val productLayoutHelper = ProductLayoutHelper(this)

    /**雕刻布局*/
    val engraveLayoutHelper = EngraveLayoutHelper().apply {
        backPressedDispatcherOwner = this@CanvasDemo
    }

    /**雕刻预览布局*/
    val engravePreviewLayoutHelper = EngravePreviewLayoutHelper(this).apply {
        backPressedDispatcherOwner = this@CanvasDemo
    }

    /**Canvas控制*/
    fun bindCanvasRecyclerView(itemHolder: DslViewHolder, adapterItem: DslAdapterItem) {
        val canvasView = itemHolder.v<CanvasView>(R.id.canvas_view)
        val itemRecyclerView = itemHolder.v<RecyclerView>(R.id.canvas_item_view)

        itemRecyclerView?.initDslAdapter {
            canvasLayoutHelper.bindItems(itemHolder, canvasView!!, this)
        }
    }

    //</editor-fold desc="bindCanvasRecyclerView">
}