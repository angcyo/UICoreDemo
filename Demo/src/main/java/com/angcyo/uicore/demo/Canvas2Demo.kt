package com.angcyo.uicore.demo

import android.graphics.RectF
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bluetooth.fsc.IReceiveBeanAction
import com.angcyo.bluetooth.fsc.enqueue
import com.angcyo.bluetooth.fsc.laserpacker.DeviceStateModel
import com.angcyo.bluetooth.fsc.laserpacker.HawkEngraveKeys
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.command.ExitCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.FileModeCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.QueryCmd
import com.angcyo.bluetooth.fsc.laserpacker.parse.FileTransferParser
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryLogParser
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryStateParser
import com.angcyo.bluetooth.fsc.parse
import com.angcyo.canvas.CanvasRenderView
import com.angcyo.canvas.render.core.CanvasRenderDelegate
import com.angcyo.canvas.render.core.Reason
import com.angcyo.canvas2.laser.pecker.IEngraveRenderFragment
import com.angcyo.canvas2.laser.pecker.RenderLayoutHelper
import com.angcyo.canvas2.laser.pecker.engrave.BaseFlowLayoutHelper
import com.angcyo.canvas2.laser.pecker.engrave.EngraveFlowLayoutHelper
import com.angcyo.canvas2.laser.pecker.engrave.LPEngraveHelper
import com.angcyo.canvas2.laser.pecker.engrave.dslitem.transfer.TransferDataPxItem
import com.angcyo.canvas2.laser.pecker.engrave.isEngraveFlow
import com.angcyo.canvas2.laser.pecker.history.EngraveHistoryFragment
import com.angcyo.canvas2.laser.pecker.manager.LPProjectManager
import com.angcyo.canvas2.laser.pecker.manager.restoreProjectStateV2
import com.angcyo.canvas2.laser.pecker.manager.saveProjectStateV2
import com.angcyo.canvas2.laser.pecker.util.LPElementHelper
import com.angcyo.canvas2.laser.pecker.util.lpElement
import com.angcyo.core.component.file.writeToLog
import com.angcyo.core.component.fileSelector
import com.angcyo.core.showIn
import com.angcyo.core.vmApp
import com.angcyo.dialog.itemsDialog
import com.angcyo.dsladapter.bindItem
import com.angcyo.engrave2.data.TransitionParam
import com.angcyo.engrave2.transition.EngraveTransitionHelper
import com.angcyo.fragment.AbsLifecycleFragment
import com.angcyo.http.rx.doMain
import com.angcyo.item.component.DebugFragment
import com.angcyo.item.style.itemCurrentIndex
import com.angcyo.laserpacker.LPDataConstant
import com.angcyo.laserpacker.bean.LPElementBean
import com.angcyo.laserpacker.bean.LPProjectBean
import com.angcyo.laserpacker.device.DeviceHelper
import com.angcyo.laserpacker.device.DeviceHelper._defaultProjectOutputFile
import com.angcyo.laserpacker.device.DeviceHelper._defaultProjectOutputFileV2
import com.angcyo.laserpacker.device.EngraveHelper
import com.angcyo.laserpacker.device.ble.DeviceConnectTipActivity
import com.angcyo.laserpacker.device.engraveLoadingAsync
import com.angcyo.laserpacker.open.CanvasOpenModel
import com.angcyo.laserpacker.project.ProjectListFragment
import com.angcyo.library.L
import com.angcyo.library.LTime
import com.angcyo.library.Library.CLICK_COUNT
import com.angcyo.library.component.MultiFingeredHelper
import com.angcyo.library.component._delay
import com.angcyo.library.component.pad.isInPadMode
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.randomColor
import com.angcyo.library.ex.toHexString
import com.angcyo.library.ex.toListOf
import com.angcyo.library.libFolderPath
import com.angcyo.library.toast
import com.angcyo.library.toastQQ
import com.angcyo.library.utils.fileNameTime
import com.angcyo.objectbox.laser.pecker.entity.TransferConfigEntity
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.getRandomText
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023-2-11
 */
class Canvas2Demo : AppDslFragment(), IEngraveRenderFragment {

    init {
        enableSoftInput = false
    }

    override fun canSwipeBack(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vmApp<FscBleApiModel>().connectDeviceListData.observe {
            if (it.isNullOrEmpty()) {
                fragmentTitle = "未连接设备"
            } else {
                it.first().let { deviceState ->
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
                    engraveFlowLayoutHelper.startPreview(this@Canvas2Demo)
                }

                //雕刻
                itemHolder.click(R.id.engrave_button) {
                    if (engraveFlowLayoutHelper.isAttach()) {
                        return@click
                    }
                    val list = LPEngraveHelper.getAllValidRendererList(renderDelegate)
                    if (list.isNullOrEmpty()) {
                        toastQQ("无元素需要雕刻")
                    } else {
                        renderDelegate?.selectorManager?.resetSelectorRenderer(list, Reason.user)

                        engraveFlowLayoutHelper.engraveFlow =
                            com.angcyo.engrave.BaseFlowLayoutHelper.ENGRAVE_FLOW_TRANSFER_BEFORE_CONFIG
                        engraveFlowLayoutHelper.showIn(this@Canvas2Demo)
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
                        if (bean is LPProjectBean) {
                            LPProjectManager().openProjectBean(delegate, bean)
                        } else if (bean is LPElementBean) {
                            LPProjectManager().openElementBean(delegate, bean, true)
                        }
                    }
                }
            }
        }

        //首次进来, 检查是否要恢复雕刻进度
        engraveFlowLayoutHelper.laserPeckerModel.initializeData.observe {
            if (it == true) {
                engraveFlowLayoutHelper.checkRestoreEngrave(this)
                val deviceState = engraveFlowLayoutHelper.deviceStateModel.deviceStateData.value
                if (deviceState != null) {
                    engraveFlowLayoutHelper.deviceStateModel.startLoopCheckState(!deviceState.isModeIdle())
                }
            }
        }
    }

    //region---core---

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
        //restore
        _vh.postDelay(0) {
            renderLayoutHelper.delegate?.restoreProjectStateV2()
        }
    }

    override fun onDestroyView() {
        //save
        renderLayoutHelper.delegate?.saveProjectStateV2()
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        engraveFlowLayoutHelper.deviceStateModel.startLoopCheckState(false)
        LPElementHelper.restoreLocation()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        "CanvasDemo:onSaveInstanceState:$outState".writeToLog()
        renderDelegate?.saveProjectStateV2()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        "CanvasDemo:onViewStateRestored:$savedInstanceState".writeToLog()
    }

    /**测试布局*/
    private fun bindTestDemo(itemHolder: DslViewHolder) {

        //---
        var cmdString = ""
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
            vmApp<DeviceStateModel>().queryDeviceState()
        }

        //---

        //设备指令
        itemHolder.click(R.id.device_command_button) {
            showDeviceCommand(itemHolder)
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
                    renderDelegate,
                    LPDataConstant.DATA_TYPE_GCODE,
                    first,
                    second.toListOf()
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

        //test
        itemHolder.click(R.id.test_button) {

        }

        //save
        itemHolder.click(R.id.save1_button) {
            renderDelegate?.apply {
                engraveLoadingAsync({
                    LPProjectManager().apply {
                        projectName = "save-v1-${nowTimeString()}"
                        L.i(
                            saveProjectV1To(
                                _defaultProjectOutputFile("LP-${fileNameTime()}"),
                                renderDelegate!!
                            )
                        )
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
                        L.i(
                            saveProjectV2To(
                                _defaultProjectOutputFileV2("LP-${fileNameTime()}"),
                                renderDelegate!!
                            )
                        )
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

    /**设备指令*/
    private fun showDeviceCommand(itemHolder: DslViewHolder) {
        fContext().itemsDialog {
            addDialogItem {
                itemText = "查询设备状态"
                itemClick = {
                    vmApp<DeviceStateModel>().queryDeviceState { bean, error ->
                        bean?.parse<QueryStateParser>()?.let {
                            doMain {
                                itemHolder.tv(R.id.result_text_view)?.text = "$it"
                            }
                        }
                    }
                }
            }
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
            addDialogItem {
                itemText = "清除设备所有历史"
                itemClick = {
                    FileModeCmd.deleteAllHistory().enqueue { bean, error ->
                        if (bean?.parse<FileTransferParser>()?.isFileDeleteSuccess() == true) {
                            toast("清除成功")
                        }
                        error?.let { toast(it.message) }
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
            dpi = LaserPeckerHelper.DPI_254
        }

        fContext().itemsDialog {
            //分辨率dpi
            addItem(TransferDataPxItem().apply {
                itemPxList = LaserPeckerHelper.findProductSupportPxList()
                selectorCurrentDpi(transferConfigEntity.dpi)
                itemHidden = itemPxList.isNullOrEmpty() //自动隐藏
                observeItemChange {
                    //保存最后一次选择的dpi
                    val dpi = itemPxList?.get(itemCurrentIndex)?.dpi ?: LaserPeckerHelper.DPI_254
                    transferConfigEntity.dpi = dpi
                }
            })
            addDialogItem {
                itemText = "转普通图片"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToBitmap(
                                element,
                                transferConfigEntity
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
                                element,
                                transferConfigEntity,
                                TransitionParam(
                                    isBitmapInvert = false,
                                    invert = CLICK_COUNT++ % 2 == 1
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
                                element,
                                transferConfigEntity
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
                                element,
                                transferConfigEntity,
                                TransitionParam()
                            )
                        )
                    }
                }
            }
            addDialogItem {
                itemText = "转GCode(OpenCV)"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToGCode(
                                element,
                                transferConfigEntity,
                                TransitionParam(
                                    onlyUseBitmapToGCode = true,
                                    useOpenCvHandleGCode = true
                                )
                            )
                        )
                    }
                }
            }
            addDialogItem {
                itemText = "转GCode(Pixel)"
                itemClick = {
                    wrapDuration(itemHolder) {
                        L.i(
                            EngraveTransitionHelper.transitionToGCode(
                                element,
                                transferConfigEntity,
                                TransitionParam(
                                    onlyUseBitmapToGCode = true,
                                    useOpenCvHandleGCode = false,
                                    isSingleLine = element.elementBean.isLineShape
                                )
                            )
                        )
                    }
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

    //endregion---core---

    //region---test---

    private fun testCanvasRenderView(itemHolder: DslViewHolder) {
        val canvasRenderView = itemHolder.v<CanvasRenderView>(R.id.canvas_view)
        canvasRenderView?.delegate?.apply {
            renderViewBox.originGravity = Gravity.CENTER
        }
    }

    private fun testDemo(itemHolder: DslViewHolder) {

    }

    //endregion---test---

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
    private val renderLayoutHelper = RenderLayoutHelper(this)

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
                    renderLayoutHelper.delegate?.showRectBounds(
                        it,
                        offsetRectTop = true
                    )
                }
            } else if (to == BaseFlowLayoutHelper.ENGRAVE_FLOW_BEFORE_CONFIG) {
                renderLayoutHelper.delegate?.saveProjectStateV2()
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

        onEngraveParamsChangeAction = {
            renderLayoutHelper.updateLayerListLayout()
        }
    }

    //</editor-fold desc="init">

    //<editor-fold desc="IEngraveCanvasFragment">

    override val fragment: AbsLifecycleFragment
        get() = this

    override val engraveFlowLayoutHelper: EngraveFlowLayoutHelper
        get() = _engraveFlowLayoutHelper.apply {
            engraveCanvasFragment = this@Canvas2Demo
        }

    override val renderDelegate: CanvasRenderDelegate?
        get() = renderLayoutHelper.delegate

    override val flowLayoutContainer: ViewGroup?
        get() = fragment._vh.group(R.id.engrave_flow_wrap_layout)

    override val dangerLayoutContainer: ViewGroup?
        get() = fragment._vh.group(R.id.canvas_wrap_layout) ?: _vh.itemView as ViewGroup

    //</editor-fold desc="IEngraveCanvasFragment">
}