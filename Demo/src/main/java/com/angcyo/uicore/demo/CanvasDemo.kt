package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bluetooth.fsc.IReceiveBeanAction
import com.angcyo.bluetooth.fsc.enqueue
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.command.EngravePreviewCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.ExitCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.FileModeCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.QueryCmd
import com.angcyo.bluetooth.fsc.laserpacker.parse.FileTransferParser
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryLogParser
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryStateParser
import com.angcyo.bluetooth.fsc.parse
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.data.CanvasProjectBean
import com.angcyo.canvas.data.CanvasProjectItemBean
import com.angcyo.canvas.data.LimitDataInfo
import com.angcyo.canvas.graphics.GraphicsHelper
import com.angcyo.canvas.graphics.addGCodeRender
import com.angcyo.canvas.graphics.addSvgRender
import com.angcyo.canvas.graphics.addTextRender
import com.angcyo.canvas.items.data.DataItemRenderer
import com.angcyo.canvas.items.data.DataPathItem
import com.angcyo.canvas.laser.pecker.CanvasLayoutHelper
import com.angcyo.canvas.laser.pecker.activity.ProjectListFragment
import com.angcyo.canvas.laser.pecker.mode.CanvasOpenModel
import com.angcyo.canvas.laser.pecker.openCanvasFile
import com.angcyo.canvas.laser.pecker.restoreInstanceState
import com.angcyo.canvas.laser.pecker.saveInstanceState
import com.angcyo.canvas.utils.CanvasDataHandleOperate
import com.angcyo.canvas.utils.engraveMode
import com.angcyo.canvas.utils.parseGCode
import com.angcyo.component.getPhoto
import com.angcyo.core.component.fileSelector
import com.angcyo.core.loadingAsyncTg
import com.angcyo.core.showIn
import com.angcyo.core.vmApp
import com.angcyo.dialog.itemsDialog
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.bindItem
import com.angcyo.engrave.*
import com.angcyo.engrave.ble.DeviceConnectTipActivity
import com.angcyo.engrave.ble.DeviceSettingFragment
import com.angcyo.engrave.ble.EngraveHistoryFragment
import com.angcyo.engrave.data.HawkEngraveKeys
import com.angcyo.engrave.transition.EngraveTransitionManager
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.gcode.GCodeDrawable
import com.angcyo.gcode.GCodeHelper
import com.angcyo.http.base.toJson
import com.angcyo.http.rx.doMain
import com.angcyo.item.component.DebugFragment
import com.angcyo.library.*
import com.angcyo.library.component.MultiFingeredHelper
import com.angcyo.library.component._delay
import com.angcyo.library.component.isNotificationsEnabled
import com.angcyo.library.component.openNotificationSetting
import com.angcyo.library.component.pad.isInPadMode
import com.angcyo.library.ex.*
import com.angcyo.library.unit.InchValueUnit
import com.angcyo.library.unit.MmValueUnit
import com.angcyo.library.unit.PixelValueUnit
import com.angcyo.library.utils.fileNameTime
import com.angcyo.library.utils.fileNameUUID
import com.angcyo.library.utils.writeTo
import com.angcyo.objectbox.laser.pecker.entity.TransferConfigEntity
import com.angcyo.server.bindFileServer
import com.angcyo.svg.Svg
import com.angcyo.uicore.MainFragment.Companion.CLICK_COUNT
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.SvgDemo.Companion.gCodeNameList
import com.angcyo.uicore.demo.SvgDemo.Companion.svgResList
import com.angcyo.uicore.getRandomText
import com.angcyo.websocket.service.bindLogWSServer
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.recycler.initDslAdapter
import com.angcyo.widget.span.span
import com.pixplicity.sharp.Sharp
import com.pixplicity.sharp.SharpDrawable
import org.jetbrains.annotations.TestOnly

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/01
 */
class CanvasDemo : AppDslFragment(), IEngraveCanvasFragment {

    //val restoreProjectPath =

    init {
        enableSoftInput = false
    }

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
        return super.onBackPressed()
        /*val backPressed = super.onBackPressed()
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
        }*/
    }

    override fun canSwipeBack(): Boolean = false

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(if (isInPadMode()) R.layout.canvas_pad_layout else R.layout.canvas_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val canvasView = itemHolder.v<CanvasView>(R.id.canvas_view)
                //?.setBgDrawable(_colorDrawable("#20000000".toColorInt()))
                //?.setBgDrawable(CheckerboardDrawable.create())

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

                            limitRenderer.resetLimit {
                                // 宽*高 100*100mm
                                add(LimitDataInfo(limitRect.toPath()))
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

                            limitRenderer.resetLimit {
                                // 宽*高 300*400mm
                                add(LimitDataInfo(limitRect.toPath()))
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
                        addTextRender(getRandomText())
                    }
                }
                itemHolder.click(R.id.add_svg) {
                    canvasView?.canvasDelegate?.apply {
                        addSvgRender(resources.openRawResource(R.raw.issue_19).readText())
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
                                svg
                                //Svg.loadSvgDrawable(svg)
                            }) { svg ->
                                canvasView?.canvasDelegate?.apply {
                                    addSvgRender(svg)
                                }
                            }
                        }
                    }
                }
                itemHolder.click(R.id.add_gcode) {
                    canvasView?.canvasDelegate?.apply {
                        val text = fContext().readAssets("gcode/LaserPecker.gcode")
                        addGCodeRender(text)
                    }
                }
                itemHolder.click(R.id.random_add_svg) {
                    canvasView?.canvasDelegate?.apply {
                        /*loadSvgDrawable().apply {
                            addDrawableRenderer(second)
                        }*/
                        loadSvgPathDrawable().apply {
                            addSvgRender(first)
                        }
                    }
                }
                itemHolder.click(R.id.random_add_gcode) {
                    canvasView?.canvasDelegate?.apply {
                        loadGCodeDrawable().apply {
                            addGCodeRender(first)
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
                /*itemHolder.click(R.id.bluetooth_button) {
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
                }*/

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

                //结束预览
                itemHolder.click(R.id.preview_stop_button) {
                    val cmd = EngravePreviewCmd.previewStopCmd()
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

                //雕刻预览
                itemHolder.click(R.id.engrave_preview_button) {
                    if (engraveFlowLayoutHelper.isAttach()) {
                        return@click
                    }
                    if (engraveFlowLayoutHelper.checkRestoreEngrave(this@CanvasDemo)) {
                        return@click
                    }
                    if (!engraveFlowLayoutHelper.checkStartPreview()) {
                        return@click
                    }

                    //安全提示弹窗
                    engraveFlowLayoutHelper.showSafetyTips(fContext()) {
                        //如果有第三轴, 还需要检查对应的配置
                        engraveFlowLayoutHelper.startPreview(this@CanvasDemo)
                    }
                }

                //雕刻
                itemHolder.click(R.id.engrave_button) {
                    if (engraveFlowLayoutHelper.isAttach()) {
                        return@click
                    }
                    canvasView?.canvasDelegate?.getSelectedRenderer()?.let { renderer ->
                        engraveFlowLayoutHelper.engraveFlow =
                            BaseFlowLayoutHelper.ENGRAVE_FLOW_TRANSFER_BEFORE_CONFIG
                        engraveFlowLayoutHelper.showIn(this@CanvasDemo)
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

                //项目列表
                itemHolder.click(R.id.project_button) {
                    dslFHelper {
                        show(ProjectListFragment::class)
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
                    if (HawkEngraveKeys.USE_FOUR_POINTS_PREVIEW) {
                        "4点预览√"
                    } else {
                        "4点预览×"
                    }
                itemHolder.click(R.id.four_points_preview_button) {
                    HawkEngraveKeys.USE_FOUR_POINTS_PREVIEW =
                        !HawkEngraveKeys.USE_FOUR_POINTS_PREVIEW

                    //update
                    itemHolder.tv(R.id.four_points_preview_button)?.text =
                        if (HawkEngraveKeys.USE_FOUR_POINTS_PREVIEW) {
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
                    preProcessData(itemHolder, canvasDelegate)
                }

                //tip
                itemHolder.click(R.id.tip_button) {
                    dslAHelper {
                        start(DeviceConnectTipActivity::class)
                    }
                }

                //test
                itemHolder.click(R.id.test_button) {
                    test(itemHolder, canvasView)
                }

                //启动ws log服务
                itemHolder.click(R.id.log_button) {
                    bindFileServer()
                    bindLogWSServer()
                    it.isEnabled = false
                }
                //分享最后一次的雕刻日志
                itemHolder.click(R.id.share_log_button) {
                    EngraveFlowDataHelper.shareEngraveLog()
                }

                //save
                itemHolder.click(R.id.save_button) {
                    canvasView?.canvasDelegate?.apply {
                        engraveLoadingAsync({
                            getCanvasDataBean(
                                "save-${nowTimeString()}",
                                HawkEngraveKeys.projectOutSize
                            ).let {
                                val json = it.toJson()
                                json.writeTo(
                                    CanvasDataHandleOperate._defaultProjectOutputFile(
                                        "LP-${fileNameTime()}"
                                    ),
                                    false
                                )
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

                //more
                itemHolder.click(R.id.more_button) {
                    moreDialog(itemHolder)
                }//end more

                //canvas
                bindCanvasRecyclerView(itemHolder, adapterItem)

                //product
                engraveProductLayoutHelper.bindCanvasView(
                    itemHolder,
                    _vh.itemView as ViewGroup,
                    itemHolder.v<CanvasView>(R.id.canvas_view)
                )

                //test
                //canvasView?.canvasDelegate?.engraveMode()
            }
        }

        //设备状态监听和恢复
        /*engraveFlowLayoutHelper.laserPeckerModel.initializeData.observeOnce() { init ->
            if (init == true) {
                //监听第一次初始化
                val stateParser = engraveFlowLayoutHelper.laserPeckerModel.deviceStateData.value
                if (vmApp<EngraveModel>().isRestore()) {
                    //恢复状态
                    if (stateParser?.isModeEngravePreview() == true) {
                        //设备已经在雕刻预览中
                        engraveFlowLayoutHelper.engraveFlow =
                            BaseEngraveLayoutHelper.ENGRAVE_FLOW_PREVIEW
                        engraveFlowLayoutHelper.showIn(this@CanvasDemo)
                    } else if (stateParser?.isModeEngrave() == true) {
                        //设备已经在雕刻中
                        engraveFlowLayoutHelper.engraveFlow =
                            BaseEngraveLayoutHelper.ENGRAVE_FLOW_ENGRAVING
                        engraveFlowLayoutHelper.showIn(this@CanvasDemo)
                    }
                }
            }
            init == true
        }*/

        //有需要打开的数据
        vmApp<CanvasOpenModel>().openPendingData.observe { bean ->
            bean?.let {
                _delay {
                    if (bean is CanvasProjectBean) {
                        canvasDelegate?.openCanvasFile(this, bean)
                    } else if (bean is CanvasProjectItemBean) {
                        GraphicsHelper.addRenderItemDataBean(canvasDelegate, bean)
                    }
                }
            }
        }

        //首次进来, 检查是否要恢复雕刻进度
        engraveFlowLayoutHelper.laserPeckerModel.initializeData.observe {
            if (it == true) {
                engraveFlowLayoutHelper.checkRestoreEngrave(this)
                engraveFlowLayoutHelper.engraveModel._listenerEngraveState =
                    engraveFlowLayoutHelper.laserPeckerModel.deviceStateData.value?.isModeEngrave() == true
                engraveFlowLayoutHelper.checkLoopQueryDeviceState()
            }
        }
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
        //restore
        _vh.postDelay(0) {
            canvasDelegate?.restoreInstanceState()
        }
    }

    override fun onDestroyView() {
        //save
        canvasDelegate?.saveInstanceState()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        engraveFlowLayoutHelper.loopCheckDeviceState = false
        GraphicsHelper.restoreLocation()
    }

    /**预处理数据*/
    fun preProcessData(itemHolder: DslViewHolder, canvasDelegate: CanvasDelegate?) {
        canvasDelegate ?: return
        engraveLoadingAsync({
            val entityList = EngraveTransitionManager().transitionTransferData(
                canvasDelegate,
                TransferConfigEntity(
                    name = "Test",
                    dpi = LaserPeckerHelper.DPI_254
                )
            )
            L.w(entityList)
            val string = entityList.connect { it.index.toStr() }
            toastQQ(string)
            itemHolder.tv(R.id.result_text_view)?.text = string
        })
    }

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

    /**更多的对话框*/
    fun moreDialog(itemHolder: DslViewHolder) {
        val selectedRenderer = canvasDelegate?.getSelectedRenderer()
        val rendererBounds = selectedRenderer?.getBounds()
        val rendererRotate = selectedRenderer?.rotate
        var pathList: List<Path>? = null
        if (selectedRenderer is DataItemRenderer) {
            val renderItem = selectedRenderer.getRendererRenderItem()
            if (renderItem is DataPathItem) {
                pathList = renderItem.drawPathList
            }
        }
        fContext().itemsDialog {
            addDialogItem {
                itemText = "toStrokeGCode"
                itemClick = {
                    pathList?.let {
                        this@CanvasDemo.engraveLoadingAsync({
                            L.i(
                                CanvasDataHandleOperate.pathStrokeToGCode(
                                    pathList,
                                    rendererBounds!!,
                                    rendererRotate!!
                                )
                            )
                        })
                    }
                }
            }
            addDialogItem {
                itemText = "toFillGCode"
                itemClick = {
                    pathList?.let {
                        this@CanvasDemo.engraveLoadingAsync({
                            L.i(
                                CanvasDataHandleOperate.pathFillToGCode(
                                    pathList,
                                    rendererBounds!!,
                                    rendererRotate!!
                                )
                            )
                        })
                    }
                }
            }
            addDialogItem {
                itemText = "toStrokeSvg"
                itemClick = {
                    pathList?.let {
                        this@CanvasDemo.engraveLoadingAsync({
                            L.i(
                                CanvasDataHandleOperate.pathStrokeToSvg(
                                    pathList,
                                    rendererBounds!!,
                                    rendererRotate!!
                                )
                            )
                        })
                    }
                }
            }
            addDialogItem {
                itemText = "toFillSvg"
                itemClick = {
                    pathList?.let {
                        this@CanvasDemo.engraveLoadingAsync({
                            L.i(
                                CanvasDataHandleOperate.pathFillToSvg(
                                    pathList,
                                    rendererBounds!!,
                                    rendererRotate!!
                                )
                            )
                        })
                    }
                }
            }
            //
            addDialogItem {
                itemText = "查询机器日志"
                itemClick = {
                    QueryCmd.log.enqueue { bean, error ->
                        if (error == null) {
                            bean?.parse<QueryLogParser>()?.let {
                                L.i(it)
                                val log = it.log ?: "no log!"
                                toast(log)
                                itemHolder.tv(R.id.result_text_view)?.text = log
                            }
                        }
                    }
                }
            }
            //
            addDialogItem {
                itemText = "预处理数据"
                itemClick = {
                    preProcessData(itemHolder, canvasDelegate)
                }
            }
            //
            addDialogItem {
                itemText = "显示雕刻通知"
                itemClick = {
                    if (!isNotificationsEnabled()) {
                        openNotificationSetting()
                    } else if (!EngraveNotifyHelper.isChannelEnable()) {
                        EngraveNotifyHelper.openChannelSetting()
                    } else {
                        anim(0, 100) {
                            onAnimatorConfig = {
                                it.duration = 3_000
                            }
                            onAnimatorUpdateValue = { value, _ ->
                                EngraveNotifyHelper.showEngraveNotify(value as Int)
                            }
                        }
                    }
                }
            }
            addDialogItem {
                itemText = "隐藏雕刻通知"
                itemClick = {
                    EngraveNotifyHelper.hideEngraveNotify()
                }
            }
        }
    }

    @TestOnly
    fun test(viewHolder: DslViewHolder, canvasView: CanvasView?) {
        /*History::class.java.updateOrCreateEntity(query = {

        }) {

        }*/

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
        /*fContext().canvasRegulateWindow2(it) {

        }*/
        //toast("toast-${nowTimeString()}")
        /*TransferMonitorEntity::class.box(LPBox.PACKAGE_NAME) {
            val count = count()
            val result = query().build().find(count - 1, 1)
            val list = query().build().find()
            L.i()
        }*/
        /*val monitorEntity =
            EngraveFlowDataHelper.getTransferMonitor("180438d6-9b7a-4c33-af76-ded8c5ead288")
        L.i()*/

        /*engraveLoadingAsyncTimeout({
            val time = 2000L
            sleep(time)
            time
        }) {
            toast("执行完成...$it")
        }*/

        /*val list = listOf(appFolderPath())
        val zip = list.zip()
        L.i("压缩->${zip}")
        L.i("解压->${zip?.unzipFile()}")*/

        /*val list = listOf(
            LogFile.http.toLogFilePath(),
            DslCrashHandler.currentCrashFile().absolutePath,
            logPath()
        )
        L.i(list.zip())*/
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

    /**Canvas布局*/
    val canvasLayoutHelper = CanvasLayoutHelper(this)

    /**产品布局*/
    val engraveProductLayoutHelper = EngraveProductLayoutHelper(this)

    /**雕刻布局*/
    val _engraveFlowLayoutHelper = EngraveFlowLayoutHelper().apply {
        backPressedDispatcherOwner = this@CanvasDemo

        onEngraveFlowChangedAction = { from, to ->
            //禁止手势
            _vh.v<CanvasView>(R.id.canvas_view)?.canvasDelegate?.engraveMode(to.isEngraveFlow())

            if (to == BaseFlowLayoutHelper.ENGRAVE_FLOW_PREVIEW) {
                //预览中, 偏移画板界面
                val productInfoData = laserPeckerModel.productInfoData
                productInfoData.value?.bounds?.let {
                    canvasView?.canvasDelegate?.showRectBounds(it, offsetRectTop = true)
                }
            } else if (to == BaseFlowLayoutHelper.ENGRAVE_FLOW_BEFORE_CONFIG) {
                canvasDelegate?.saveInstanceState()
            }
        }

        onIViewEvent = { iView, event ->
            val start = event == Lifecycle.Event.ON_START
            val destroy = event == Lifecycle.Event.ON_DESTROY

            if (destroy) {
                _vh.enable(R.id.lib_title_wrap_layout, null)
                _vh.enable(R.id.device_tip_wrap_layout, null)
            } else {
                _vh.enable(R.id.lib_title_wrap_layout, destroy)
                _vh.enable(R.id.device_tip_wrap_layout, destroy)
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

    //<editor-fold desc="IEngraveCanvasFragment">

    override val fragment: AbsLifecycleFragment
        get() = this

    override val engraveFlowLayoutHelper: EngraveFlowLayoutHelper
        get() = _engraveFlowLayoutHelper.apply {
            engraveCanvasFragment = this@CanvasDemo
        }

    override val canvasDelegate: CanvasDelegate?
        get() = canvasView?.canvasDelegate

    override val flowLayoutContainer: ViewGroup?
        get() = fragment._vh.group(R.id.engrave_flow_wrap_layout)

    //</editor-fold desc="IEngraveCanvasFragment">
}