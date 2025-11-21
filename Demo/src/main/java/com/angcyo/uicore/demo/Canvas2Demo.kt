package com.angcyo.uicore.demo

import android.graphics.Path
import android.graphics.PointF
import android.graphics.RectF
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bluetooth.fsc.HttpApiModel
import com.angcyo.bluetooth.fsc.IReceiveBeanAction
import com.angcyo.bluetooth.fsc.WifiApiModel
import com.angcyo.bluetooth.fsc.enqueue
import com.angcyo.bluetooth.fsc.laserpacker.DeviceStateModel
import com.angcyo.bluetooth.fsc.laserpacker.HawkEngraveKeys
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker._productName
import com.angcyo.bluetooth.fsc.laserpacker.alg.HxAlg
import com.angcyo.bluetooth.fsc.laserpacker.command.ExitCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.WifiUpdateCmd
import com.angcyo.canvas.CanvasRenderView
import com.angcyo.canvas.render.core.CanvasRenderDelegate
import com.angcyo.canvas.render.element.rendererBounds
import com.angcyo.canvas2.laser.pecker.IEngraveRenderFragment
import com.angcyo.canvas2.laser.pecker.RenderLayoutHelper
import com.angcyo.canvas2.laser.pecker.dialog.fontLibraryHandleDialogConfig
import com.angcyo.canvas2.laser.pecker.dialog.speedConvertDialogConfig
import com.angcyo.canvas2.laser.pecker.engrave.BaseFlowLayoutHelper
import com.angcyo.canvas2.laser.pecker.engrave.EngraveFlowLayoutHelper
import com.angcyo.canvas2.laser.pecker.engrave.LPEngraveHelper
import com.angcyo.canvas2.laser.pecker.engrave.dslitem.transfer.TransferDataPxItem
import com.angcyo.canvas2.laser.pecker.engrave.isEngraveFlow
import com.angcyo.canvas2.laser.pecker.engrave.lowFireWarning2DialogConfig
import com.angcyo.canvas2.laser.pecker.engrave.newflow.NewFlowConfigFragment
import com.angcyo.canvas2.laser.pecker.history.EngraveHistoryFragment
import com.angcyo.canvas2.laser.pecker.manager.AppGuideManager
import com.angcyo.canvas2.laser.pecker.manager.FileManagerFragment
import com.angcyo.canvas2.laser.pecker.manager.LPProjectManager
import com.angcyo.canvas2.laser.pecker.manager.restoreProjectStateV2
import com.angcyo.canvas2.laser.pecker.manager.saveProjectStateV2
import com.angcyo.canvas2.laser.pecker.toGcode
import com.angcyo.canvas2.laser.pecker.toSvgXml
import com.angcyo.canvas2.laser.pecker.util.LPElementHelper
import com.angcyo.canvas2.laser.pecker.util.lpElement
import com.angcyo.core.component.file.writeTo
import com.angcyo.core.component.file.writeToLog
import com.angcyo.core.component.fileSelector
import com.angcyo.core.component.model.LanguageModel
import com.angcyo.core.showIn
import com.angcyo.core.vmApp
import com.angcyo.dialog.gridMenuDialog
import com.angcyo.dialog.inputDialog
import com.angcyo.dialog.itemsDialog
import com.angcyo.dsladapter.bindItem
import com.angcyo.engrave2.data.TransitionParam
import com.angcyo.engrave2.transition.EngraveTransitionHelper
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.http.base.toJson
import com.angcyo.http.rx.doMain
import com.angcyo.item.component.DebugFragment
import com.angcyo.item.style.itemCurrentIndex
import com.angcyo.laserpacker.LPDataConstant
import com.angcyo.laserpacker.bean.LPElementBean
import com.angcyo.laserpacker.bean.LPProjectBean
import com.angcyo.laserpacker.device.AddCameraFragment
import com.angcyo.laserpacker.device.AddDeviceFragment
import com.angcyo.laserpacker.device.DeviceHelper
import com.angcyo.laserpacker.device.DeviceHelper._defaultProjectOutputFile
import com.angcyo.laserpacker.device.DeviceHelper._defaultProjectOutputFileV2
import com.angcyo.laserpacker.device.EngraveHelper
import com.angcyo.laserpacker.device.EngraveNotifyHelper
import com.angcyo.laserpacker.device.ble.DeviceConnectTipActivity
import com.angcyo.laserpacker.device.ble.EngraveExperimentalFragment
import com.angcyo.laserpacker.device.engraveLoadingAsync
import com.angcyo.laserpacker.device.machineParamsViewDialog
import com.angcyo.laserpacker.device.wifi.AddWifiDeviceFragment
import com.angcyo.laserpacker.open.CanvasOpenModel
import com.angcyo.laserpacker.project.ProjectListFragment
import com.angcyo.library.L
import com.angcyo.library.LTime
import com.angcyo.library.Library.CLICK_COUNT
import com.angcyo.library.canvas.core.Reason
import com.angcyo.library.component.MultiFingeredHelper
import com.angcyo.library.component.RBackground
import com.angcyo.library.component._delay
import com.angcyo.library.component.pad.isInPadMode
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex._string
import com.angcyo.library.ex.base64Encode
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.isDebugType
import com.angcyo.library.ex.isNil
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.randomColor
import com.angcyo.library.ex.shareFile
import com.angcyo.library.ex.toHexString
import com.angcyo.library.ex.toListOf
import com.angcyo.library.ex.wrapLog
import com.angcyo.library.libCacheFile
import com.angcyo.library.libFolderPath
import com.angcyo.library.toastQQ
import com.angcyo.library.unit.toRectMm
import com.angcyo.library.utils.Constant
import com.angcyo.library.utils.fileNameTime
import com.angcyo.library.utils.writeToFile
import com.angcyo.objectbox.laser.pecker.entity.EntitySync
import com.angcyo.objectbox.laser.pecker.entity.TransferConfigEntity
import com.angcyo.server.startFileServer
import com.angcyo.server.stopFileServer
import com.angcyo.uicore.AppDebugHelper
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.getRandomText
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023-2-11
 */
class Canvas2Demo : AppDslFragment(), IEngraveRenderFragment {

    companion object {
        /**写入到同步日志*/
        fun CharSequence.writeToSync(
            logLevel: Int = L.DEBUG, name: String = "sync.log"
        ): CharSequence {
            wrapLog().writeTo(Constant.LOG_FOLDER_NAME, name)
            L.log(logLevel, this)
            return this
        }
    }

    init {
        enableSoftInput = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val deviceStateModel = vmApp<DeviceStateModel>()
        vmApp<FscBleApiModel>().connectDeviceListData.observe {
            if (it != null) {
                if (!deviceStateModel.isDeviceConnect()) {
                    fragmentTitle = "未连接设备"
                } else {
                    it.firstOrNull()?.let { deviceState ->
                        fragmentTitle = span {
                            appendLine(DeviceConnectTipActivity.formatDeviceName(deviceState.device.name))
                            append(deviceState.device.address) {
                                fontSize = 12 * dpi
                            }
                        }
                    }
                }
            }
        }
        vmApp<WifiApiModel>().tcpStateData.observe { tcpState ->
            if (tcpState != null) {
                if (!deviceStateModel.isDeviceConnect()) {
                    fragmentTitle = "未连接设备"
                } else {
                    fragmentTitle = span {
                        appendLine(DeviceConnectTipActivity.formatDeviceName(tcpState.tcpDevice.deviceName))
                        append(tcpState.tcpDevice.address) {
                            fontSize = 12 * dpi
                        }
                    }
                }
            }
        }/*vmApp<LaserPeckerModel>().initializeOnceData.observeOnce(false) {
            if (it != null && it) {
                //设备初始化成功后, 检查是否要双红光校准
                if (_deviceSettingBean?.supportFocalDistanceAdjustRange.matchesProductVersion() && !DeviceSettingFragment.focalDistanceAdjustPromptKey.hawkGetBoolean()) {
                    //DeviceSettingFragment.focalDistanceAdjustPromptKey.hawkPut(true)
                    fContext().messageDialog {
                        dialogTitle =
                            _string(com.angcyo.laserpacker.device.R.string.focal_distance_adjust_label)
                        dialogMessage =
                            _string(com.angcyo.laserpacker.device.R.string.focal_distance_adjust_des)

                        negativeButtonText =
                            _string(com.angcyo.laserpacker.device.R.string.dialog_negative)

                        positiveButton(_string(com.angcyo.laserpacker.device.R.string.start_calibration)) { dialog, dialogViewHolder ->
                            dialog.dismiss()
                            this@Canvas2Demo.dslFHelper {
                                show(FocalDistanceAdjustFragment::class)
                            }
                        }
                    }
                }
                true
            } else {
                false
            }
        }*/
        /*vmApp<DeviceStateModel>().flameAlarmData.observe {
            if (it != null) {
                toastQQ("火焰报警$it")
            }
        }*/
        /*CoverFlameAlarmDialogConfig.observer(this)*/
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(if (isInPadMode()) R.layout.canvas2_pad_layout else R.layout.canvas2_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_view)

                //demo
                canvasRenderView?.delegate?.renderManager?.apply {
                    var width = 22 * dp
                    var height = width
                    var bounds = RectF(0f, -height, width, 0f)
                    addBeforeRendererList(bounds, _drawable(R.drawable.lib_notify_icon))

                    width = 600f
                    height = 426f
                    val left = -1000f
                    val top = -1000f - height
                    bounds = RectF(left, top, left + width, top + height)
                    addBeforeRendererList(bounds, _drawable(R.drawable.all_in2))
                }

                //1.
                renderLayoutHelper.bindRenderLayout(itemHolder)

                //雕刻预览
                itemHolder.click(R.id.engrave_preview_button) {
                    flowLayoutHelper.startPreview(this@Canvas2Demo)
                }

                //长按预览
                if (isDebugType()) {
                    itemHolder.longClick(R.id.engrave_preview_button) {
                        flowLayoutHelper._startPreview(this@Canvas2Demo)
                    }
                }

                //雕刻
                itemHolder.click(R.id.engrave_button) {
                    if (flowLayoutHelper.isAttach()) {
                        return@click
                    }
                    val list = LPEngraveHelper.getAllValidRendererList(renderDelegate)
                    if (list.isNullOrEmpty()) {
                        toastQQ("无元素需要雕刻")
                    } else {
                        renderDelegate?.selectorManager?.resetSelectorRenderer(list, Reason.user)

                        flowLayoutHelper.engraveFlow =
                            BaseFlowLayoutHelper.ENGRAVE_FLOW_TRANSFER_BEFORE_CONFIG
                        flowLayoutHelper.showIn(this@Canvas2Demo)
                    }
                }

                //雕刻2-新流程
                itemHolder.click(R.id.engrave_button2) {
                    dslFHelper {
                        anim(R.anim.lib_y_show_enter_holder, 0)
                        show(NewFlowConfigFragment::class) {
                            (this as NewFlowConfigFragment).canvasRenderDelegate = renderDelegate
                        }
                    }
                }

                //长按雕刻
                if (isDebugType()) {
                    itemHolder.longClick(R.id.engrave_button) {
                        flowLayoutHelper.engraveFlow =
                            BaseFlowLayoutHelper.ENGRAVE_FLOW_TRANSFER_BEFORE_CONFIG
                        flowLayoutHelper.showIn(this@Canvas2Demo)
                    }
                }

                //---

                itemHolder.click(R.id.canvas_view) {
                    canvasRenderView?.invalidate()
                }

                //demo
                bindTestDemo(itemHolder)

                //testCanvasRenderView(itemHolder)
                testDemo(itemHolder)
            }
        }

        //有需要打开的数据
        vmApp<CanvasOpenModel>().openPendingData.observe { bean ->
            bean?.let {
                _delay {
                    renderDelegate?.let { delegate ->
                        when (bean) {
                            is LPProjectBean -> LPProjectManager().apply {
                                flowLayoutHelper.projectBean = bean
                                openProjectBean(delegate, bean)
                            }

                            is LPElementBean -> LPProjectManager().openElementBean(
                                delegate, bean, true
                            )

                            is List<*> -> LPProjectManager().openElementBeanList(
                                delegate, bean as List<LPElementBean>?, true
                            )
                        }
                    }
                }
            }
        }

        //首次进来, 检查是否要恢复雕刻进度
        flowLayoutHelper.laserPeckerModel.initializeData.observe {
            if (it == true) {
                flowLayoutHelper.checkRestoreEngrave(this)
                val deviceState = flowLayoutHelper.deviceStateModel.deviceStateData.value
                if (deviceState != null) {
                    if (deviceState.error == 0) {
                        //无错误
                        flowLayoutHelper.deviceStateModel.startLoopCheckState(
                            !deviceState.isModeIdle(), reason = "恢复雕刻进度"
                        )
                    }
                }
            }
        }
    }

    //region---test---

    /**测试布局*/
    private fun bindTestDemo(itemHolder: DslViewHolder) {

        //---
        var cmdString = ""
        val receiveAction: IReceiveBeanAction = { bean, error ->
            val text = span {
                append(Thread.currentThread().name)
                append(" $_productName")
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
            vmApp<DeviceStateModel>().queryDeviceState()
        }

        //---

        //添加设备
        itemHolder.click(R.id.add_device_button) {
            dslFHelper {
                show(AddDeviceFragment::class)
            }
        }

        //添加摄像头
        itemHolder.click(R.id.add_camera_button) {
            dslFHelper {
                show(AddCameraFragment::class)
            }
        }

        //配网
        itemHolder.click(R.id.wifi_button) {
            dslFHelper {
                show(AddWifiDeviceFragment::class)
            }
        }

        //文件管理
        itemHolder.click(R.id.file_manager_button) {
            dslFHelper {
                show(FileManagerFragment::class)
            }
        }

        //设备指令
        itemHolder.click(R.id.device_command_button) {
            AppDebugHelper.showDeviceCommand(itemHolder, fContext())
        }

        //测试算法
        itemHolder.click(R.id.arithmetic_button) {
            showArithmeticHandle(itemHolder)
        }

        //退出指令
        itemHolder.click(R.id.exit_button) {
            val cmd = ExitCmd()
            cmdString = cmd.toHexCommandString()
            //LaserPeckerHelper.sendCommand(cmd, action = receiveAction)
            cmd.enqueue(action = receiveAction)
            EngraveNotifyHelper.hideEngraveNotify()//隐藏通知
        }

        //当前设备版本
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

        //雕刻历史
        itemHolder.click(R.id.file_button) {
            dslFHelper {
                show(EngraveHistoryFragment::class)
            }
        }

        //分享最后一次的雕刻日志
        itemHolder.click(R.id.share_log_button) {
            DeviceHelper.shareEngraveLog()
        }

        //---

        //添加随机文本
        itemHolder.click(R.id.add_picture_text) {
            LPElementHelper.addTextElement(renderDelegate, getRandomText())
        }
        itemHolder.click(R.id.random_add_svg) {
            SvgDemo.loadSvgPathDrawable().apply {

            }
        }
        itemHolder.click(R.id.random_add_gcode) {
            /*SvgDemo.loadGCodeDrawable().apply {
                LPElementHelper.addPathElement(
                    renderDelegate,
                    LPDataConstant.DATA_TYPE_GCODE,
                    first,
                    second.gCodePath.toListOf()
                )
            }*/
            SvgDemo.loadGCodePathJni().apply {
                LPElementHelper.addPathElement(
                    renderDelegate, LPDataConstant.DATA_TYPE_GCODE, first, second.toListOf()
                )
            }
        }

        //截图
        itemHolder.click(R.id.preview_button) { view ->
            _adapter.render {
                PreviewBitmapItem()() {
                    renderDelegate?.let {
                        bitmap = it.preview()
                    }
                }
            }
        }
        itemHolder.click(R.id.preview_override_button) { view ->
            _adapter.render {
                PreviewBitmapItem()() {
                    renderDelegate?.let {
                        bitmap = it.preview(overrideSize = HawkEngraveKeys.projectOutSize.toFloat())
                    }
                }
            }
        }
        itemHolder.click(R.id.preview_rect_button) {
            _adapter.render {
                PreviewBitmapItem()() {
                    renderDelegate?.let {
                        val unit = it.axisManager.renderUnit
                        val left = unit.convertValueToPixel(0f)
                        val top = unit.convertValueToPixel(0f)
                        val width = unit.convertValueToPixel(10f)
                        val height = unit.convertValueToPixel(10f)
                        bitmap = it.preview(RectF(left, top, width, height))
                    }
                }
            }
        }

        //tip
        itemHolder.click(R.id.tip_button) {
            dslAHelper {
                start(DeviceConnectTipActivity::class)
            }
        }

        //字库
        itemHolder.click(R.id.font_library_button) {
            it.context.fontLibraryHandleDialogConfig()
        }

        //实验性功能界面
        itemHolder.click(R.id.experimental_button) {
            dslFHelper {
                show(EngraveExperimentalFragment::class)
            }
        }

        //save
        itemHolder.click(R.id.save1_button) {
            renderDelegate?.apply {
                engraveLoadingAsync({
                    LPProjectManager().apply {
                        projectName = "save-v1-${nowTimeString()}"
                        val file = saveProjectV1To(
                            flowLayoutHelper.flowTaskId,
                            _defaultProjectOutputFile("LP-${fileNameTime()}"),
                            renderDelegate!!
                        )
                        L.i(file)
                        EntitySync.saveProjectSyncEntity(projectName, file?.absolutePath)
                    }
                }) {
                    it?.let { toastQQ("save success!") }
                }
            }
        }
        itemHolder.click(R.id.save2_button) {
            renderDelegate?.apply {
                engraveLoadingAsync({
                    LPProjectManager().apply {
                        projectName = "save-v2-${nowTimeString()}"
                        val file = saveProjectV2To(
                            flowLayoutHelper.lastFlowTaskId,
                            _defaultProjectOutputFileV2("LP-${fileNameTime()}"),
                            renderDelegate!!
                        )
                        L.i(file)
                        EntitySync.saveProjectSyncEntity(projectName, file?.absolutePath)
                    }
                }) {
                    it?.let { toastQQ("save success!") }
                }
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
                        renderDelegate?.apply {
                            engraveLoadingAsync({
                                LPProjectManager().openProjectUri(renderDelegate!!, it.fileUri)
                            })
                        }
                    }
                }
            }
        }
    }

    /**算法测试*/
    private fun showArithmeticHandle(itemHolder: DslViewHolder) {
        val list = renderDelegate?.selectorManager?.getSelectorRendererList(true, false)
        val renderer = list?.firstOrNull() ?: return
        val element = renderer.lpElement() ?: return

        val transferConfigEntity = TransferConfigEntity().apply {
            taskId = "Test-${nowTimeString()}"
            name = EngraveHelper.generateEngraveName()
            layerJson = HawkEngraveKeys.lastDpiLayerJson
        }

        fContext().itemsDialog {
            //分辨率dpi
            addItem(TransferDataPxItem().apply {
                itemPxList =
                    LaserPeckerHelper.findProductLayerSupportPxList(HawkEngraveKeys.lastLayerId)
                selectorCurrentDpi(
                    transferConfigEntity.getLayerConfigDpi(
                        HawkEngraveKeys.lastLayerId,
                        HawkEngraveKeys.getLastLayerDpi(HawkEngraveKeys.lastLayerId)
                    )
                )
                itemHidden = itemPxList.isNullOrEmpty() //自动隐藏
                observeItemChange {
                    //保存最后一次选择的dpi
                    val dpi = itemPxList?.get(itemCurrentIndex)?.dpi ?: LaserPeckerHelper.DPI_254
                    HawkEngraveKeys.lastDpiLayerJson =
                        HawkEngraveKeys.getLayerConfigJson(HawkEngraveKeys.lastLayerId, dpi)
                    transferConfigEntity.layerJson = HawkEngraveKeys.lastDpiLayerJson
                }
            })
            addDialogItem {
                itemText = "转普通图片"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToBitmap(
                                element, transferConfigEntity
                            )
                        )
                    }
                }
            }
            addDialogItem {
                itemText = "转抖动图片"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToBitmapDithering(
                                element, transferConfigEntity, TransitionParam(
                                    isBitmapInvert = false,
                                    invert = CLICK_COUNT++ % 2 == 1,
                                    useNewDithering = false
                                )
                            )
                        )
                    }
                }
            }
            addDialogItem {
                itemText = "转抖动图片-new"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToBitmapDithering(
                                element, transferConfigEntity, TransitionParam(
                                    isBitmapInvert = false,
                                    invert = CLICK_COUNT++ % 2 == 1,
                                    useNewDithering = true
                                )
                            )
                        )
                    }
                }
            }
            addDialogItem {
                itemText = "转图片线段"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToBitmapPath(
                                element, transferConfigEntity
                            )
                        )
                    }
                }
            }
            addDialogItem {
                itemText = "转GCode"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToGCode(
                                element, transferConfigEntity, TransitionParam()
                            )
                        )
                        EngraveTransitionHelper.tempTransitionFilePath?.shareFile()
                    }
                }
            }
            addDialogItem {
                itemText = "转GCode(OpenCV)"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToGCode(
                                element, transferConfigEntity, TransitionParam(
                                    onlyUseBitmapToGCode = true, useOpenCvHandleGCode = true
                                )
                            )
                        )
                        EngraveTransitionHelper.tempTransitionFilePath?.shareFile()
                    }
                }
            }
            addDialogItem {
                itemText = "转GCode(Pixel)"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToGCode(
                                element, transferConfigEntity, TransitionParam(
                                    onlyUseBitmapToGCode = true,
                                    useOpenCvHandleGCode = false,
                                    isSingleLine = element.elementBean.isLineShape
                                )
                            )
                        )
                        EngraveTransitionHelper.tempTransitionFilePath?.shareFile()
                    }
                }
            }
        }
    }

    /**Lx2测试*/
    private fun showLx2TestHandle(itemHolder: DslViewHolder) {
        val list = renderDelegate?.getSelectorOrAllElementRendererList(true, false)
        /*if(isNil(list)){
            return
        }*/
        fContext().gridMenuDialog {
            addGridItem(false) {
                itemText = "预览"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    vmApp<HttpApiModel>().preview(
                        LPDataConstant.kLpBaseFeed * 60f,
                        list?.mapNotNull { it.rendererBounds?.toRectMm() }
                    )
                }
            }
            vmApp<HttpApiModel>().websocketGcodeResponseData.observe {
            }
            addGridItem(false) {
                itemText = "测距"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    vmApp<HttpApiModel>().detectDistance(
                        30f, 30f
                    ) { z, error ->
                        toastQQ(error?.toString() ?: "$z")
                    }
                }
            }
            addGridItem(false) {
                itemText = "重启MCU"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    vmApp<HttpApiModel>().restartMcu { data, error ->
                        toastQQ(error?.toString() ?: "$data")
                    }
                }
            }
            addGridItem(false) {
                itemText = "重启Klipper"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    vmApp<HttpApiModel>().restartKlipper { data, error ->
                        toastQQ(error?.toString() ?: "$data")
                    }
                }
            }
            addGridItem(false) {
                itemText = "SAVE_VARIABLE"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    val cameraCalibration = listOf(
                        PointF(1744f, 1029f),
                        PointF(2498f, 1031f),
                        PointF(2495f, 1786f),
                        PointF(1738f, 1782f)
                    )
                    vmApp<HttpApiModel>().saveVariable(
                        "camera_calibration",
                        cameraCalibration.toJson()?.base64Encode()
                    ) { data, error ->
                        toastQQ(error?.toString() ?: "$data")
                    }
                    val cameraRectify = mapOf(
                        "intrinsic_matrix" to listOf(
                            listOf(
                                39389.034491756116,
                                0.0,
                                1986.3704946841995
                            ),
                            listOf(
                                0.0, 36882.84609273625, 1505.8006987027738
                            ),
                            listOf(
                                0.0, 0.0, 1.0
                            ),
                        ),
                        "distortion_coeff" to listOf(
                            listOf(
                                6.150073312790283,
                                2787.7942716389457,
                                0.21965962720085916,
                                0.08806865699064705,
                                0.9399111131795392,
                                0.64957580346988,
                                4243.834350883954,
                                -0.8678932943770115,
                                -0.0906487255207549,
                                -2.1630892856201553,
                                -0.22360973877996665,
                                2.5916286535080686
                            )
                        )
                    )
                    vmApp<HttpApiModel>().saveVariable(
                        "camera_rectify",
                        cameraRectify.toJson()?.base64Encode()
                    ) { data, error ->
                        toastQQ(error?.toString() ?: "$data")
                    }
                }
            }
            addGridItem(false) {
                itemText = "获取相机畸变数据"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    vmApp<HttpApiModel>().getCameraData { data, error ->
                        toastQQ(error?.toString() ?: "$data")
                    }
                }
            }
            addGridItem(false) {
                itemText = "升级固件"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    vmApp<HttpApiModel>().startUpgrade("https://oss.laserpecker.net/apply/firmware/LX2_V90031.zip") { data, error ->
                        toastQQ(error?.toString() ?: "$data")
                    }
                }
            }
            addGridItem(false) {
                itemText = "查询升级进度"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    vmApp<HttpApiModel>().queryUpgradeProgress { data, error ->
                        toastQQ(error?.toString() ?: "$data")
                    }
                }
            }
            addGridItem(false) {
                itemText = "获取探针状态"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    vmApp<HttpApiModel>().getProbeState { data, error ->
                        toastQQ(error?.toString() ?: "$data")
                    }
                }
            }
            addGridItem(false) {
                itemText = "弹窗测试"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    /*fContext().inclineDialogConfig {

                    }*/
                    //MaterialHelper.saveLx2CustomMaterial(MaterialHelper.lx2MaterialList)
                    //val list = MaterialHelper.loadLx2CustomMaterial()
                    fContext().machineParamsViewDialog {

                    }
                }
            }
            addGridItem(false) {
                itemText = "Start"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    fContext().startFileServer()
                }
            }
            addGridItem(false) {
                itemText = "Stop"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    fContext().stopFileServer()
                }
            }
            addGridItem(false) {
                itemText = "HxAlg Test"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    HxAlg.initAlg()
                }
            }

            addGridItem(false) {
                itemText = "火焰误检提示"
                itemGridIcon = R.drawable.lib_ic_info
                itemClick = {
                    fContext().lowFireWarning2DialogConfig {}
                }
            }
        }
    }

    private fun wrapDuration(itemHolder: DslViewHolder, action: () -> Unit) {
        engraveLoadingAsync({
            LTime.tick()
            action()
        }) {
            itemHolder.tv(R.id.result_text_view)?.text = span {
                append(nowTimeString()) {
                    foregroundColor = randomColor()
                }
                appendLine()
                append("耗时:${LTime.time()}")
            }
        }
    }

    private fun testCanvasRenderView(itemHolder: DslViewHolder) {
        val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_view)
        canvasRenderView?.delegate?.apply {
            renderViewBox.originGravity = Gravity.CENTER
        }
    }

    private fun testDemo(itemHolder: DslViewHolder) {
        //test
        itemHolder.click(R.id.test_button) {
            val str = _string(R.string.label_move_path)
            L.i("->$str")
            L.i("systemLocale->${LanguageModel.systemLocale}")
            L.i("appLocale->${LanguageModel.appLocale}")

            /*dslFHelper {
                show(FocalDistanceAdjustFragment::class)
            }*/

            /*val originBounds = CanvasGroupRenderer.getRendererListRenderProperty(
                renderDelegate!!.getSelectorOrAllElementRendererList()
            ).getRenderBounds(RectF())
            L.w("边界:$originBounds 宽:${originBounds.width()} 高:${originBounds.height()}")*/

            //fContext().checkShowEjectSmokeDialog()

            /*GuideManager.checkOrShowGuide(
                activity?.window?.contentView(),
                it,
                1
            )*//*GuideManager.checkOrShowGuide(
                activity?.window?.contentView(),
                itemHolder.view(R.id.engrave_preview_button),
                2
            )

            fContext().numberKeyboardDialog {
                //dialogTitle = "输入测试..."
                dialogMessage = "80.8"
                numberInputHint = "数字"
                //removeDecimal()
                //removePlusMinus()
                onNumberResultAction = {
                    toast(it?.toString())
                    false
                }
            }*/

            //"测试写入".writeToSync()
            /*LPTransferHelper.startCreateTransferData(
                vmApp(),
                "test-${uuid()}",
                renderDelegate
            )
            toastQQ("test")*/
            //EngraveTransitionHelper.saveTaskAerialView("e2bebf35ff624b9bba2f9bce86633524")

            /* "M12.981 6.677c.006.122.008.243.008.366a7.991 7.991 0 0 1-8.046 8.047 8.01 8.01 0 0 1-4.335-1.271 5.805 5.805 0 0 0 .675.04 5.68 5.68 0 0 0 3.513-1.21 2.832 2.832 0 0 1-2.642-1.965 2.813 2.813 0 0 0 .531.051 2.865 2.865 0 0 0 .746-.1 2.831 2.831 0 0 1-2.269-2.772v-.037a2.805 2.805 0 0 0 1.281.354 2.828 2.828 0 0 1-.874-3.775 8.024 8.024 0 0 0 5.828 2.954 2.83 2.83 0 0 1 2.755-3.472 2.826 2.826 0 0 1 2.064.893 5.654 5.654 0 0 0 1.796-.686 2.835 2.835 0 0 1-1.244 1.564 5.642 5.642 0 0 0 1.624-.445 5.735 5.735 0 0 1-1.41 1.464z".toPath()
                 .apply {
                     val matrix = Matrix()
                     matrix.setScale(10f, 10f)
                     transform(matrix)
                     testPath(this)
                 }*/

            /*val sx = 100f
            val sy = 100f
            val cx = 150f
            val cy = 0f
            val ex = 200f
            val ey = 100f
            val svg = "M${sx},${sy} Q${cx},${cy},${ex},${ey}"
            val svgPath = svg.toPath()
            testPath(svgPath)*/

            /*val o = PointF(0f, 0f)
            val p1 = PointF(45f, -45f)
            val p2 = PointF(-45f, -45f)
            val p3 = PointF(-45f, 45f)
            val p4 = PointF(45f, 45f)
            L.i(buildString {
                appendLine()
                appendLine("P1: $p1 ${VectorHelper.angle(o, p1)}")
                appendLine("P2: $p2 ${VectorHelper.angle(o, p2)}")
                appendLine("P3: $p3 ${VectorHelper.angle(o, p3)}")
                appendLine("P4: $p4 ${VectorHelper.angle(o, p4)}")
            })

            Path().apply {
                //addCircle(100f, 100f, 100f, Path.Direction.CW)
                addRoundRect(0f, 0f, 200f, 200f, 40f, 40f, Path.Direction.CW)

                *//*transform(Matrix().apply {
                    setRotate(30f, 100f, 100f)
                    postSkew(0.3f, 0f)
                })*//*

                testPath(this)
            }

            Path().apply {
                addCircle(100f, 100f, 100f, Path.Direction.CW)
                testPath(this)
            }*/
        }

        itemHolder.click(R.id.speed_convert_button) {
            it.context.speedConvertDialogConfig()
        }

        itemHolder.click(R.id.wifi_upgrade_button) {
            it.context.inputDialog {
                dialogTitle = "Wifi固件地址"
                defaultInputString =
                    "https://gitcode.net/angcyo/file/-/raw/master/firmware/wifi/V8.01.02.bin"

                onInputResult = { dialog, inputText ->
                    val url = inputText.toString()
                    if (url.isEmpty()) {
                        toastQQ("地址为空")
                    } else {
                        WifiUpdateCmd(url).enqueue { bean, error ->
                            if (error == null) {
                                dialog.dismiss()
                            } else {
                                toastQQ(error.message)
                            }
                        }
                    }
                    false
                }
            }
        }

        itemHolder.click(R.id.camera_upgrade_button) {
            AppDebugHelper.showCameraUpgrade()
        }

        itemHolder.click(R.id.export_svg_button) {
            val rendererList = renderDelegate?.getSelectorOrAllElementRendererList(true)
            if (rendererList != null && !isNil(rendererList)) {
                val svgXml = rendererList.toSvgXml(byEngrave = true)
                svgXml?.writeToFile(libCacheFile("export_svg_test.svg"))?.shareFile()
            }

            /*renderDelegate?.selectorManager?.getSelectorRendererList(true)?.forEach {
                if (it is CanvasElementRenderer) {
                    val element = it.lpPathElement()*//*element?.getEngravePathData()?.toSvgPathContent(
                        libCacheFile("${element.elementBean.name ?: element.elementBean.uuid}.svg")
                    )?.shareFile()*//*
                    itemHolder.context.tgStrokeLoading { isCancel, loadEnd ->
                        runOnBackground {
                            element?.getEngravePathData()?.toSVGStrokeContentVectorStr(
                                libCacheFile("${element.elementBean.name ?: element.elementBean.uuid}.svg"),
                                wrapSvgXml = true
                            )?.shareFile()
                            loadEnd(true, null)
                        }
                    }
                }
            }*/
        }

        itemHolder.click(R.id.export_gcode_button) {
            val rendererList = renderDelegate?.getSelectorOrAllElementRendererList(true)
            if (rendererList != null && !isNil(rendererList)) {
                val gcode = rendererList.toGcode(byEngrave = true)
                gcode?.writeToFile(libCacheFile("export_gcode_test.gcode"))?.shareFile()
            }
        }

        //lx2
        itemHolder.click(R.id.lx2_test_button) {
            showLx2TestHandle(itemHolder)
        }
    }

    private fun testPath(path: Path) {/*val bool = LibLpHawkKeys.enableVectorArc
        LibLpHawkKeys.enableVectorArc = true
        val svgFile = path.toListOf().toSVGContent(libCacheFile("svg.txt"), Paint.Style.STROKE)
        val svgSysFile = path.toListOf().toSvgPathContent(libCacheFile("svg-sys.txt"))
        val gcodeFile =
            path.toListOf().toGCodeContent(libCacheFile("gcode.txt"), Paint.Style.STROKE)
        val gcodeSysFile = path.toListOf().toGCodePathContent(libCacheFile("gcode-sys.txt"))
        LibLpHawkKeys.enableVectorArc = bool
        val text = svgFile.readText()
        L.i(text)*/
    }

    //endregion---test---

    //region---core---

    override fun canSwipeBack(): Boolean = false

    override fun onBackPressed(): Boolean {
        return !AppGuideManager.backGuid() && super.onBackPressed()
    }

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)
        AppGuideManager.pauseGuideIndex = -1
    }

    override fun onFragmentHide() {
        super.onFragmentHide()
        AppGuideManager.pauseGuideIndex = 1
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
        //restore
        _vh.postDelay {
            renderLayoutHelper.delegate?.restoreProjectStateV2()
        }
    }

    override fun onDestroyView() {
        //save
        "CanvasDemo:onDestroyView".writeToLog()
        vmApp<DeviceStateModel>().exitIfNeed()
        renderLayoutHelper.delegate?.saveProjectStateV2(
            flowLayoutHelper.flowTaskId, !RBackground.isBackground()
        )
        super.onDestroyView()
    }

    override fun onDestroy() {
        "CanvasDemo:onDestroy".writeToLog()
        super.onDestroy()
        flowLayoutHelper.deviceStateModel.startLoopCheckState(false, reason = "界面销毁")
        LPElementHelper.restoreLocation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        "CanvasDemo:onSaveInstanceState:$outState".writeToLog()
        renderDelegate?.saveProjectStateV2(flowLayoutHelper.flowTaskId, true)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        "CanvasDemo:onViewStateRestored:$savedInstanceState".writeToLog()
    }

    //endregion---core---

    //<editor-fold desc="touch">

    private val pinchGestureDetector = MultiFingeredHelper.PinchGestureDetector().apply {
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

    //<editor-fold desc="init">

    /**Canvas2布局*/
    private val _renderLayoutHelper = RenderLayoutHelper(this)

    /**雕刻布局*/
    private val _engraveFlowLayoutHelper = EngraveFlowLayoutHelper().apply {
        backPressedDispatcherOwner = this@Canvas2Demo

        onEngraveFlowChangedAction = { from, to ->
            //禁止手势
            val isEngraveFlow = to.isEngraveFlow()
            renderLayoutHelper.delegate?.disableEditTouchGesture(isEngraveFlow)
            if (isInPadMode()) {
                renderLayoutHelper.disableEditItem(isEngraveFlow)
            }

            if (to == BaseFlowLayoutHelper.ENGRAVE_FLOW_PREVIEW) {
                //预览中, 偏移画板界面
                val productInfoData = laserPeckerModel.productInfoData
                productInfoData.value?.bounds?.let {
                    renderLayoutHelper.delegate?.showRectBounds(it, offsetRectTop = true)
                }
            } else if (to == BaseFlowLayoutHelper.ENGRAVE_FLOW_BEFORE_CONFIG) {
                //2024-6-26 移除雕刻时的自动保存, 漂移错误的问题?
                if (HawkEngraveKeys.enableAutoSaveProject) {
                    renderLayoutHelper.delegate?.saveProjectStateV2(flowLayoutHelper.flowTaskId)
                }
            }
        }

        onIViewEvent = { iView, event ->
            val start = event == Lifecycle.Event.ON_START
            val destroy = event == Lifecycle.Event.ON_DESTROY

            if (destroy) {
                _vh.enable(R.id.lib_title_wrap_layout, null)
                _vh.enable(R.id.device_tip_wrap_layout, null)
            } else {
                _vh.enable(R.id.lib_title_wrap_layout, false)
                _vh.enable(R.id.device_tip_wrap_layout, false)
            }
        }

        onEngraveParamsChangeAction = {
            renderLayoutHelper.updateLayerListLayout()
        }
    }

    //</editor-fold desc="init">

    //<editor-fold desc="IEngraveCanvasFragment">

    override val fragment: AbsLifecycleFragment
        get() = this

    override val flowLayoutHelper: EngraveFlowLayoutHelper
        get() = _engraveFlowLayoutHelper.apply {
            engraveCanvasFragment = this@Canvas2Demo
        }

    override val renderDelegate: CanvasRenderDelegate?
        get() = renderLayoutHelper.delegate

    override val flowLayoutContainer: ViewGroup?
        get() = fragment._vh.group(R.id.engrave_flow_wrap_layout)

    override val dangerLayoutContainer: ViewGroup?
        get() = fragment._vh.group(R.id.canvas_wrap_layout) ?: _vh.itemView as ViewGroup

    override val renderLayoutHelper: RenderLayoutHelper
        get() = _renderLayoutHelper

    //</editor-fold desc="IEngraveCanvasFragment">
}