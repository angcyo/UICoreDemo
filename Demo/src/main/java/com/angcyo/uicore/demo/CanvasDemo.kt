package com.angcyo.uicore.demo

import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bluetooth.fsc.IReceiveBeanAction
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.command.*
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryEngraveFileParser
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryStateParser
import com.angcyo.bluetooth.fsc.parse
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.Strategy
import com.angcyo.canvas.core.InchValueUnit
import com.angcyo.canvas.core.MmValueUnit
import com.angcyo.canvas.core.PixelValueUnit
import com.angcyo.canvas.items.renderer.ShapeItemRenderer
import com.angcyo.canvas.utils.addDrawableRenderer
import com.angcyo.canvas.utils.addPictureTextRender
import com.angcyo.canvas.utils.addPictureTextRenderer
import com.angcyo.canvas.utils.addTextRenderer
import com.angcyo.core.component.dslPermissions
import com.angcyo.core.vmApp
import com.angcyo.dialog.itemsDialog
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.bindItem
import com.angcyo.gcode.GCodeHelper
import com.angcyo.http.rx.doMain
import com.angcyo.library.ex.*
import com.angcyo.library.toast
import com.angcyo.uicore.MainFragment.Companion.CLICK_COUNT
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.SvgDemo.Companion.gCodeNameList
import com.angcyo.uicore.demo.SvgDemo.Companion.svgResList
import com.angcyo.uicore.demo.ble.bluetoothSearchListDialog
import com.angcyo.uicore.demo.ble.engraveDialog
import com.angcyo.uicore.demo.canvas.CanvasLayoutHelper
import com.angcyo.uicore.demo.canvas.EngraveLayoutHelper
import com.angcyo.uicore.demo.canvas.EngravePreviewLayoutHelper
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.recycler.initDslAdapter
import com.angcyo.widget.span.span
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
            bindItem(R.layout.demo_canvas) { itemHolder, itemPosition, adapterItem, payloads ->
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
                                bitmap = it.getBitmap(left, top, width, height)
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

                                onDismissListener = {
                                    vmApp<FscBleApiModel>().connectDeviceListData.value?.firstOrNull()
                                        ?.let { deviceState ->
                                            fragmentTitle = span {
                                                appendLine(deviceState.device.name)
                                                append(deviceState.device.address) {
                                                    fontSize = 12 * dpi
                                                }
                                            }
                                        }
                                }
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
                    engravePreviewLayoutHelper.showPreviewLayout(
                        itemHolder.itemView as ViewGroup,
                        canvasView?.canvasDelegate
                    )
                }

                //结束预览
                itemHolder.click(R.id.preview_stop_button) {
                    val cmd = EngravePreviewCmd.previewStop()
                    cmdString = cmd.toHexCommandString()
                    LaserPeckerHelper.sendCommand(cmd, action = receiveAction)
                }

                //退出指令
                itemHolder.click(R.id.exit_button) {
                    val cmd = ExitCmd()
                    cmdString = cmd.toHexCommandString()
                    LaserPeckerHelper.sendCommand(cmd, action = receiveAction)
                }

                //雕刻
                itemHolder.click(R.id.engrave_button) {
                    canvasView?.canvasDelegate?.getSelectedRenderer()?.let { renderer ->
                        //engraveLayoutHelper.engrave(renderer)
                        fContext().engraveDialog(renderer) {

                        }
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

                //历史
                itemHolder.click(R.id.file_button) {
                    QueryCmd(0x01).send { bean, error ->
                        doMain {
                            bean?.parse<QueryEngraveFileParser>()?.let {
                                fContext().itemsDialog {
                                    dialogTitle = "打印历史"
                                    it.nameList?.forEach { name ->
                                        addDialogItem {
                                            itemText = "$name"
                                            itemClick = {
                                                //开始雕刻
                                                EngraveCmd(name).send { bean, error ->
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
                    }
                }

                //canvas
                bindCanvasRecyclerView(itemHolder, adapterItem)

                //engrave
                engraveLayoutHelper.bindLayout(itemHolder.v<CanvasView>(R.id.canvas_view)!!)
            }
        }
    }

    fun getRandomText() =
        "angcyo${randomString(Random.nextInt(0, 3))}\n${randomString(Random.nextInt(0, 3))}"

    fun loadSvgDrawable(): Drawable =
        Sharp.loadResource(resources, svgResList.randomGetOnce()!!).drawable

    fun loadGCodeDrawable(): Drawable = GCodeHelper.parseGCode(
        fContext(), fContext().readAssets(gCodeNameList.randomGetOnce()!!)
    )!!

    //<editor-fold desc="bindCanvasRecyclerView">

    val canvasLayoutHelper = CanvasLayoutHelper(this)
    val engraveLayoutHelper = EngraveLayoutHelper(this)
    val engravePreviewLayoutHelper = EngravePreviewLayoutHelper(this)

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