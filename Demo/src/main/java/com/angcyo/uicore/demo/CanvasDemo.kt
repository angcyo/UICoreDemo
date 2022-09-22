package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.base.removeThis
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bluetooth.fsc.IReceiveBeanAction
import com.angcyo.bluetooth.fsc.enqueue
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.command.EngravePreviewCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.ExitCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.FileModeCmd
import com.angcyo.bluetooth.fsc.laserpacker.parse.FileTransferParser
import com.angcyo.bluetooth.fsc.laserpacker.parse.QuerySettingParser
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryStateParser
import com.angcyo.bluetooth.fsc.parse
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.Strategy
import com.angcyo.canvas.items.PictureShapeItem
import com.angcyo.canvas.items.PictureTextItem
import com.angcyo.canvas.items.renderer.PictureItemRenderer
import com.angcyo.canvas.laser.pecker.CanvasLayoutHelper
import com.angcyo.canvas.laser.pecker.canvasRegulateWindow2
import com.angcyo.canvas.laser.pecker.loadingAsync
import com.angcyo.canvas.laser.pecker.openCanvasFile
import com.angcyo.canvas.utils.*
import com.angcyo.component.getPhoto
import com.angcyo.core.component.dslPermissions
import com.angcyo.core.component.fileSelector
import com.angcyo.core.loadingAsyncTg
import com.angcyo.core.showIn
import com.angcyo.core.vmApp
import com.angcyo.dialog.normalIosDialog
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.bindItem
import com.angcyo.engrave.EngraveLayoutHelper
import com.angcyo.engrave.EngravePreviewLayoutHelper
import com.angcyo.engrave.EngraveProductLayoutHelper
import com.angcyo.engrave.ble.DeviceConnectTipActivity
import com.angcyo.engrave.ble.DeviceSettingFragment
import com.angcyo.engrave.ble.EngraveHistoryFragment
import com.angcyo.engrave.ble.bluetoothSearchListDialog
import com.angcyo.engrave.data.EngraveReadyInfo
import com.angcyo.engrave.model.EngraveModel
import com.angcyo.engrave.transition.EngraveTransitionManager
import com.angcyo.gcode.GCodeDrawable
import com.angcyo.gcode.GCodeHelper
import com.angcyo.gcode.GCodeWriteHandler
import com.angcyo.http.base.toJson
import com.angcyo.http.rx.doMain
import com.angcyo.item.component.DebugFragment
import com.angcyo.library.L
import com.angcyo.library.component.MultiFingeredHelper
import com.angcyo.library.component._delay
import com.angcyo.library.ex.*
import com.angcyo.library.libCacheFile
import com.angcyo.library.libFolderPath
import com.angcyo.library.toast
import com.angcyo.library.unit.InchValueUnit
import com.angcyo.library.unit.MmValueUnit
import com.angcyo.library.unit.PixelValueUnit
import com.angcyo.library.utils.fileName
import com.angcyo.library.utils.fileNameUUID
import com.angcyo.library.utils.writeTo
import com.angcyo.lifecycle.onStateChanged
import com.angcyo.server.file.bindFileServer
import com.angcyo.svg.Svg
import com.angcyo.uicore.MainFragment.Companion.CLICK_COUNT
import com.angcyo.uicore.activity.CanvasOpenActivity
import com.angcyo.uicore.activity.CanvasOpenInfo
import com.angcyo.uicore.activity.CanvasOpenModel
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.SvgDemo.Companion.gCodeNameList
import com.angcyo.uicore.demo.SvgDemo.Companion.svgResList
import com.angcyo.websocket.service.bindLogWSServer
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

    val canvasView: CanvasView?
        get() = _vh.v<CanvasView>(R.id.canvas_view)

    override fun onBackPressed(): Boolean {
        val backPressed = super.onBackPressed()
        if (backPressed) {
            val count = canvasView?.canvasDelegate?.itemRendererCount ?: 0
            if (count <= 0) {
                return backPressed
            } else {
                fContext().normalIosDialog {
                    dialogTitle = getString(R.string.canvas_tips)
                    dialogMessage = getString(R.string.canvas_exit_edit)
                    positiveButton { dialog, dialogViewHolder ->
                        dialog.dismiss()
                        dslFHelper {
                            removeThis()
                        }
                    }
                }
                return false
            }
        } else {
            return false
        }
    }

    override fun canSwipeBack(): Boolean = false

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
                        val path = Path()
                        path.addRect(limitRect, Path.Direction.CW)

                        val renderer = PictureItemRenderer<PictureShapeItem>(this)
                        val item = PictureShapeItem(path)
                        renderer.setRendererRenderItem(item)

                        addItemRenderer(renderer, Strategy.normal)
                        showRectBounds(limitRect)
                    }
                }
                itemHolder.clickAndInit(R.id.smart_button, {
                    (it as TextView).text =
                        "Smart ${canvasView?.canvasDelegate?.smartAssistant?.enable.toDC()}"
                }) {
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
                itemHolder.click(R.id.add_picture_text) {
                    canvasView?.canvasDelegate?.apply {
                        addPictureTextRender(getRandomText())
                    }
                }
                itemHolder.click(R.id.add_svg) {
                    canvasView?.canvasDelegate?.apply {
                        addPictureDrawableRenderer(
                            Sharp.loadResource(
                                resources,
                                R.raw.issue_19
                            ).drawable
                        )
                    }
                }
                itemHolder.click(R.id.add_image_svg) {
                    /*canvasView?.canvasDelegate?.apply {
                        addDrawableRenderer(Sharp.loadResource(resources, R.raw.issue_19).drawable)
                    }*/
                    getPhoto {
                        it?.let {
                            loadingAsyncTg({
                                val file = libCacheFile(fileNameUUID(".png"))
                                it.save(file.absolutePath)
                                val svg = Svg.imageToSVG(file.absolutePath)
                                libCacheFile(fileNameUUID(".svg")).appendText(svg!!)
                                Svg.loadSvgDrawable(svg)
                            }) { svgDrawable ->
                                canvasView?.canvasDelegate?.apply {
                                    addPictureDrawableRenderer(svgDrawable!!)
                                }
                            }
                        }
                    }
                }
                itemHolder.click(R.id.add_gcode) {
                    canvasView?.canvasDelegate?.apply {
                        val text = fContext().readAssets("gcode/LaserPecker.gcode")
                        val drawable = GCodeHelper.parseGCode(text)
                        addPictureDrawableRenderer(drawable)
                    }
                }
                itemHolder.click(R.id.random_add_svg) {
                    canvasView?.canvasDelegate?.apply {
                        /*loadSvgDrawable().apply {
                            addDrawableRenderer(second)
                        }*/
                        loadSvgPathDrawable().apply {
                            addPictureSharpRenderer(first, second)
                        }
                    }
                }
                itemHolder.click(R.id.random_add_gcode) {
                    canvasView?.canvasDelegate?.apply {
                        loadGCodeDrawable().apply {
                            addPictureDrawableRenderer(second)
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
                    engravePreviewLayoutHelper.showPreviewSafetyTips(fContext()) {
                        engravePreviewLayoutHelper.canvasDelegate = canvasView?.canvasDelegate
                        engravePreviewLayoutHelper.showIn(this@CanvasDemo)
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
                        engraveLayoutHelper.showIn(this@CanvasDemo)
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

                //清除历史
                itemHolder.click(R.id.clear_file_button) {
                    FileModeCmd.deleteAllHistory().enqueue { bean, error ->
                        if (bean?.parse<FileTransferParser>()?.isFileDeleteSuccess() == true) {
                            toast("删除成功")
                        }
                        error?.let { toast(it.message) }
                    }
                }

                //切换设备中心点
                itemHolder.click(R.id.device_origin_button) {
                    LaserPeckerHelper.switchDeviceCenter()
                    canvasView?.canvasDelegate?.refresh()
                }

                //切换4点预览
                itemHolder.tv(R.id.four_points_preview_button)?.text =
                    if (QuerySettingParser.USE_FOUR_POINTS_PREVIEW) {
                        "4点预览√"
                    } else {
                        "4点预览×"
                    }
                itemHolder.click(R.id.four_points_preview_button) {
                    QuerySettingParser.USE_FOUR_POINTS_PREVIEW =
                        !QuerySettingParser.USE_FOUR_POINTS_PREVIEW

                    //update
                    itemHolder.tv(R.id.four_points_preview_button)?.text =
                        if (QuerySettingParser.USE_FOUR_POINTS_PREVIEW) {
                            "4点预览√"
                        } else {
                            "4点预览×"
                        }
                }

                //设置
                itemHolder.click(R.id.setting_button) {
                    dslFHelper {
                        show(DeviceSettingFragment::class)
                    }
                }

                //预处理数据
                itemHolder.click(R.id.pre_process_button) {
                    canvasView?.canvasDelegate?.getSelectedRenderer()?.let { renderer ->
                        loadingAsync({
                            EngraveTransitionManager().apply {
                                val engraveReadyInfo =
                                    transitionReadyData(renderer) ?: EngraveReadyInfo()
                                transitionEngraveData(renderer, engraveReadyInfo)
                                L.i(engraveReadyInfo.dataPath, engraveReadyInfo.previewDataPath)
                            }
                        })
                    }
                }

                //tip
                itemHolder.click(R.id.tip_button) {
                    dslAHelper {
                        start(DeviceConnectTipActivity::class)
                    }
                }

                //test
                itemHolder.click(R.id.test_button) {
                    //test(itemHolder, canvasView)
                    /*dslAHelper {
                        start(FirmwareUpdateActivity::class)
                    }*/
                    /*itemHolder.view(R.id.canvas_device_state_wrap_layout)?.apply {
                        *//*reveal {
                            duration = 1_000
                        }*//*
                        //clipBoundsAnimator()
                        clipBoundsAnimatorFromLeft()
                    }*/
                    /*fContext().cropDialog {
                        cropBitmap = BitmapFactory.decodeResource(resources, R.drawable.face)
                        onCropResultAction = {
                            it?.let {
                                canvasView?.canvasDelegate?.addPictureBitmapRenderer(it)
                            }
                        }
                    }*/
                    /*val path = "/system/fonts/"
                    val file = path.file()
                    val list = file.listFiles()
                    list.logi()*/
                    fContext().canvasRegulateWindow2(it) {

                    }
                }

                //启动ws log服务
                itemHolder.click(R.id.log_button) {
                    bindFileServer()
                    bindLogWSServer()
                    it.isEnabled = false
                }

                //save
                itemHolder.click(R.id.save_button) {
                    canvasView?.canvasDelegate?.apply {
                        loadingAsync({
                            getCanvasDataBean().let {
                                val json = it.toJson()
                                json.writeTo(libCacheFile(fileName(suffix = ".lp")))
                                L.i(json)
                            }
                        })
                    }
                }

                //open
                itemHolder.click(R.id.open_button) {
                    dslFHelper {
                        fileSelector({
                            showFileMd5 = true
                            showFileMenu = true
                            showHideFile = true
                            targetPath = libFolderPath()
                        }) {
                            it?.let {
                                canvasView?.canvasDelegate?.apply {
                                    removeAllItemRenderer()
                                    openCanvasFile(this@CanvasDemo, it.fileUri)
                                }
                            }
                        }
                    }
                }

                //canvas
                bindCanvasRecyclerView(itemHolder, adapterItem)

                //product
                engraveProductLayoutHelper.bindCanvasView(
                    itemHolder,
                    _vh.itemView as ViewGroup,
                    itemHolder.v<CanvasView>(R.id.canvas_view)!!
                )

                //test
                //canvasView?.canvasDelegate?.engraveMode()
            }
        }

        //状态监听和恢复
        engravePreviewLayoutHelper.laserPeckerModel.initializeData.observeOnce() { init ->
            if (init == true) {
                //监听第一次初始化
                val stateParser = engravePreviewLayoutHelper.laserPeckerModel.deviceStateData.value
                if (vmApp<EngraveModel>().isRestore()) {
                    //恢复状态
                    if (stateParser?.isModeEngravePreview() == true) {
                        //设备已经在雕刻预览中
                        engravePreviewLayoutHelper.showIn(this@CanvasDemo)
                    } else if (stateParser?.isModeEngrave() == true) {
                        //设备已经在雕刻中
                        engraveLayoutHelper.showIn(this@CanvasDemo)
                    }
                }
            }
            init == true
        }

        //有需要打开的数据
        vmApp<CanvasOpenModel>().openPendingData.observe {
            it?.let {
                openCanvasInfo(it)
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
        return text!! to Svg.loadSvgPathDrawable(
            text,
            Color.BLACK,
            Paint.Style.STROKE,
            null,
            0,
            0
        )!!
    }

    fun loadGCodeDrawable(): Pair<String, GCodeDrawable> {
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
                        canvasView.canvasDelegate.addPictureDrawableRenderer(
                            GCodeHelper.parseGCode(gCode)
                        )
                    }
                }
            } else {
                val pathList = renderer.getPathList()
                if (!pathList.isNullOrEmpty()) {
                    //path list
                    loadingAsync({
                        CanvasDataHandleOperate.pathToGCode(
                            pathList,
                            renderer.getBounds(),
                            renderer.rotate
                        )
                    }) {
                        //no
                        it?.readText()?.let { gCode ->
                            canvasView.canvasDelegate.addPictureDrawableRenderer(
                                GCodeHelper.parseGCode(gCode)
                            )
                        }
                    }
                } else {
                    //bitmap to gcode
                    val lineSpace = if (renderer.getRendererRenderItem() is PictureTextItem) {
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
                            canvasView.canvasDelegate.addPictureDrawableRenderer(
                                GCodeHelper.parseGCode(gCode)
                            )
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

    /**Canvas控制*/
    fun bindCanvasRecyclerView(itemHolder: DslViewHolder, adapterItem: DslAdapterItem) {
        val canvasView = itemHolder.v<CanvasView>(R.id.canvas_view)
        val itemRecyclerView = itemHolder.v<RecyclerView>(R.id.canvas_item_view)

        itemRecyclerView?.initDslAdapter {
            canvasLayoutHelper.bindItems(itemHolder, canvasView!!, this)
        }
    }

    //</editor-fold desc="bindCanvasRecyclerView">

    //<editor-fold desc="init">

    /**打开外部数据*/
    fun openCanvasInfo(info: CanvasOpenInfo) {
        val canvasDelegate = _vh.v<CanvasView>(R.id.canvas_view)?.canvasDelegate
        if (canvasDelegate == null) {
            _delay(160L) {
                openCanvasInfo(info)
            }
            return
        }
        when (info.type) {
            CanvasOpenActivity.JPG -> {
                val bitmap = info.url.toBitmap()
                if (bitmap == null) {
                    "数据异常:${info.url}"
                    toast("data exception!")
                } else {
                    canvasDelegate.addPictureBitmapRenderer(bitmap)
                }
            }
            CanvasOpenActivity.GCODE -> {
                val drawable = info.url.file().readText()?.run {
                    GCodeHelper.parseGCode(this)
                }
                if (drawable == null) {
                    "数据异常:${info.url}"
                    toast("data exception!")
                } else {
                    canvasDelegate.addPictureDrawableRenderer(drawable)
                }
            }
            CanvasOpenActivity.SVG -> {
                val text = info.url.file().readText()
                val drawable = text?.run {
                    loadTextSvgPath(this)
                }
                if (drawable == null) {
                    "数据异常:${info.url}"
                    toast("data exception!")
                } else {
                    canvasDelegate.addPictureSharpRenderer(text, drawable)
                }
            }
        }
    }

    /**Canvas布局*/
    val canvasLayoutHelper = CanvasLayoutHelper(this)

    /**产品布局*/
    val engraveProductLayoutHelper = EngraveProductLayoutHelper(this)

    /**雕刻布局*/
    val engraveLayoutHelper = EngraveLayoutHelper().apply {
        backPressedDispatcherOwner = this@CanvasDemo

        onIViewEvent = { iView, event ->
            //禁止手势
            if (event == Lifecycle.Event.ON_RESUME) {
                _vh.v<CanvasView>(R.id.canvas_view)?.canvasDelegate?.engraveMode(true)
            } else if (event == Lifecycle.Event.ON_DESTROY) {
                _vh.v<CanvasView>(R.id.canvas_view)?.canvasDelegate?.engraveMode(false)
            }
        }
    }

    /**雕刻预览布局*/
    val engravePreviewLayoutHelper = EngravePreviewLayoutHelper(this).apply {
        backPressedDispatcherOwner = this@CanvasDemo

        //next
        onNextAction = {
            _vh.v<CanvasView>(R.id.canvas_view)?.canvasDelegate?.let { canvasDelegate ->
                canvasDelegate.getSelectedRenderer()?.let { renderer ->
                    engraveLayoutHelper.renderer = renderer
                    engraveLayoutHelper.canvasDelegate = canvasDelegate
                    engraveLayoutHelper.showIn(this@CanvasDemo)
                }
            }
        }

        //lifecycle
        onIViewEvent = { iView, ev ->
            val canvasDelegate = _vh.v<CanvasView>(R.id.canvas_view)?.canvasDelegate
            if (ev == Lifecycle.Event.ON_CREATE) {
                lifecycle.onStateChanged(true) { source, event, observer ->
                    _vh.v<CanvasView>(R.id.canvas_view)?.let { canvasView ->
                        laserPeckerModel.productInfoData.value?.let { productInfo ->
                            if (event == Lifecycle.Event.ON_START) {
                                //界面显示
                                canvasView.canvasDelegate.showRectBounds(
                                    productInfo.previewBounds,
                                    offsetRectTop = true
                                )
                            } else if (event == Lifecycle.Event.ON_STOP) {
                                //界面隐藏
                                //canvasViewBox.translateTo(0f, dy)
                            }
                        }
                    }
                }
            } else if (ev == Lifecycle.Event.ON_RESUME) {
                canvasDelegate?.progressRenderer?.apply {
                    drawBorderMode = true
                    progressRenderer = canvasDelegate.getSelectedRenderer()
                }
            } else if (ev == Lifecycle.Event.ON_DESTROY) {
                canvasDelegate?.progressRenderer?.apply {
                    drawBorderMode = false
                    progressRenderer = canvasDelegate.getSelectedRenderer()
                }
            }
        }
    }

    //</editor-fold desc="init">

    //<editor-fold desc="touch">

    val pinchGestureDetector = MultiFingeredHelper.PinchGestureDetector().apply {
        onPinchAction = {
            dslFHelper {
                show(DebugFragment::class)
            }
        }
    }

    override fun onDispatchTouchEvent(event: MotionEvent) {
        super.onDispatchTouchEvent(event)
        pinchGestureDetector.onTouchEvent(event)
    }

    //</editor-fold desc="touch">
}