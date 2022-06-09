package com.angcyo.uicore.demo.canvas

import android.graphics.Color
import android.graphics.Paint
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.command.*
import com.angcyo.bluetooth.fsc.laserpacker.parse.FileTransferParser
import com.angcyo.bluetooth.fsc.laserpacker.parse.MiniReceiveParser
import com.angcyo.bluetooth.fsc.parse
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.core.CanvasEntryPoint
import com.angcyo.canvas.items.PictureShapeItem
import com.angcyo.canvas.items.renderer.BaseItemRenderer
import com.angcyo.canvas.utils.EngraveHelper
import com.angcyo.core.vmApp
import com.angcyo.coroutine.launchLifecycle
import com.angcyo.coroutine.withBlock
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.http.rx.doMain
import com.angcyo.item.style.itemLabelText
import com.angcyo.library.L
import com.angcyo.library.ex.*
import com.angcyo.library.utils.fileName
import com.angcyo.library.utils.filePath
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.demo.ble.*
import com.angcyo.widget.recycler.renderDslAdapter

/**
 * 雕刻布局相关操作
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/30
 */
class EngraveLayoutHelper(val lifecycleOwner: LifecycleOwner) : BaseEngraveLayoutHelper() {

    /**雕刻对象*/
    var renderer: BaseItemRenderer<*>? = null

    /**雕刻参数*/
    var engraveOptionInfo: EngraveOptionInfo = EngraveOptionInfo("自定义", 100, 10, 1)

    /**进度item*/
    val engraveProgressItem = EngraveProgressItem()

    var dslAdapter: DslAdapter? = null

    //雕刻数据
    var _engraveDataInfo: EngraveDataInfo? = null

    init {
        layoutId = R.layout.canvas_engrave_layout
    }

    /**绑定布局*/
    @CanvasEntryPoint
    fun bindCanvasView(canvasView: CanvasView) {
        vmApp<LaserPeckerModel>().productInfoData.observe(lifecycleOwner) { productInfo ->
            if (productInfo == null) {
                canvasView.canvasDelegate.limitRenderer.clear()
            } else {
                if (productInfo.isOriginCenter) {
                    canvasView.canvasDelegate.moveOriginToCenter()
                } else {
                    canvasView.canvasDelegate.moveOriginToLT()
                }
                canvasView.canvasDelegate.showAndLimitBounds(productInfo.limitPath)
            }
        }
    }

    override fun showLayout(viewGroup: ViewGroup, canvasDelegate: CanvasDelegate?) {
        super.showLayout(viewGroup, canvasDelegate)
        initLayout()
    }

    /**初始化布局*/
    fun initLayout() {
        //init
        viewHolder?.click(R.id.close_layout_view) {
            hideLayout()
        }

        viewHolder?.rv(R.id.lib_recycler_view)?.renderDslAdapter {
            dslAdapter = this
        }

        //engrave
        handleEngrave()
    }

    override fun hideLayout() {
        super.hideLayout()
    }

    fun percentList(): List<Int> {
        return (1..100).toList()
    }

    /**显示错误提示*/
    fun showEngraveError(error: String?) {
        dslAdapter?.render {
            this + engraveProgressItem.apply {
                itemUpdateFlag = true
                itemProgress = 0
                itemTip = error
            }
        }
        doMain {
            viewHolder?.visible(R.id.close_layout_view)
        }
    }

    /**显示进度提示*/
    fun showEngraveProgress(progress: Int, tip: CharSequence? = null, time: Long? = null) {
        dslAdapter?.render {
            insertItem(0, engraveProgressItem.apply {
                itemUpdateFlag = true
                itemProgress = progress
                itemTip = tip ?: itemTip
                itemTime = time ?: itemTime
            })
        }
        //_dialogViewHolder?.visible(R.id.close_layout_view)
    }

    //region ---handle---

    /**处理雕刻数据*/
    fun handleEngrave() {
        val renderer = renderer
        val item = renderer?.getRendererItem()
        if (item == null) {
            showEngraveError("数据传输失败")
            return
        }
        if (item is PictureShapeItem && item.paint.style == Paint.Style.STROKE) {
            //描边时, 才处理成GCode
            val path = item.shapePath
            if (path != null) {
                lifecycleOwner.launchLifecycle {
                    showEngraveProgress(100, _string(R.string.v4_bmp_edit_tips))
                    val dataInfo = withBlock {
                        val file = EngraveHelper.pathStrokeToGCode(
                            path,
                            renderer.getRotateBounds(),
                            renderer.rotate,
                            filePath(
                                "GCode",
                                fileName(suffix = ".gcode")
                            ).file()
                        )
                        EngraveDataInfo(EngraveDataInfo.TYPE_GCODE, file.readBytes())
                    }
                    sendEngraveData(dataInfo)
                }
            }
        } else {
            //其他方式, 使用图片雕刻
            lifecycleOwner.launchLifecycle {
                showEngraveProgress(100, _string(R.string.v4_bmp_edit_tips))

                val dataInfo = withBlock {

                    val bounds = renderer.getRotateBounds()
                    var bitmap = renderer.preview()?.toBitmap()

                    if (bitmap == null) {
                        null
                    } else {
                        val width = bitmap.width
                        val height = bitmap.height

                        val px: Byte = engraveOptionInfo.px

                        bitmap = LaserPeckerHelper.bitmapScale(bitmap, px)

                        val x = bounds.left.toInt()
                        val y = bounds.top.toInt()

                        val data = bitmap.colorChannel { color, channel ->
                            if (color == Color.TRANSPARENT) {
                                255
                            } else {
                                channel
                            }
                        }

                        //根据px, 修正坐标
                        val rect = EngravePreviewCmd.adjustBitmapRange(x, y, width, height, px)

                        EngraveDataInfo(
                            EngraveDataInfo.TYPE_BITMAP,
                            data,
                            rect.width(),
                            rect.height(),
                            rect.left,
                            rect.top,
                            px
                        )
                    }
                }
                if (dataInfo == null) {
                    showEngraveError("数据处理失败")
                } else {
                    sendEngraveData(dataInfo)
                }
            }
        }
    }

    /**发送雕刻数据*/
    fun sendEngraveData(data: EngraveDataInfo) {
        _engraveDataInfo = data
        showEngraveProgress(0, _string(R.string.print_v2_package_transfer))
        val cmd = FileModeCmd(data.data.size)
        LaserPeckerHelper.sendCommand(cmd) { bean, error ->
            bean?.parse<FileTransferParser>()?.let {
                if (it.isIntoFileMode()) {
                    //成功进入大数据模式

                    //数据类型封装
                    val dataCommand: DataCommand? = when (data.dataType) {
                        EngraveDataInfo.TYPE_BITMAP -> {
                            engraveOptionInfo.x = data.x
                            engraveOptionInfo.y = data.y
                            DataCommand.bitmapData(
                                data.name,
                                data.data,
                                data.width,
                                data.height,
                                data.x,
                                data.y,
                                data.px
                            )
                        }
                        EngraveDataInfo.TYPE_GCODE -> DataCommand.gcodeData(data.name, data.data)
                        else -> null
                    }

                    //开始发送数据
                    if (dataCommand != null) {
                        LaserPeckerHelper.sendCommand(dataCommand, {
                            //进度
                            showEngraveProgress(it.sendPacketPercentage, null, it.remainingTime)
                        }) { bean, error ->
                            bean?.parse<FileTransferParser>()?.let {
                                if (it.isFileTransferSuccess()) {
                                    //文件传输完成
                                    showEngraveItem()
                                } else {
                                    showEngraveError("数据传输失败")
                                }
                            }
                        }
                    } else {
                        showEngraveError("数据有误")
                    }
                } else {
                    showEngraveError("数据传输失败")
                }
            }.elseNull {
                showEngraveError(error?.message ?: "数据传输失败")
            }
        }
    }

    /**显示开始雕刻相关的item*/
    fun showEngraveItem() {
        doMain {
            viewHolder?.visible(R.id.close_layout_view)
        }

        dslAdapter?.render {
            removeItem(engraveProgressItem, false)

            EngraveOptionItem()() {
                itemLabelText = "材质:"
                itemWheelList = _stringArray(R.array.sourceMaterial).toList()
                itemSelectedIndex = itemWheelList?.indexOf(engraveOptionInfo.material) ?: 0
                itemTag = EngraveOptionInfo::material.name
                itemEngraveOptionInfo = engraveOptionInfo
            }
            EngraveOptionItem()() {
                itemLabelText = "功率:"
                itemWheelList = percentList()
                itemSelectedIndex = findIndex(itemWheelList, engraveOptionInfo.power)
                itemTag = EngraveOptionInfo::power.name
                itemEngraveOptionInfo = engraveOptionInfo
            }
            EngraveOptionItem()() {
                itemLabelText = "深度:"
                itemWheelList = percentList()
                itemSelectedIndex = findIndex(itemWheelList, engraveOptionInfo.depth)
                itemTag = EngraveOptionInfo::depth.name
                itemEngraveOptionInfo = engraveOptionInfo
            }
            EngraveOptionItem()() {
                itemLabelText = "次数:"
                itemWheelList = percentList()
                itemSelectedIndex = findIndex(itemWheelList, engraveOptionInfo.time)
                itemTag = EngraveOptionInfo::time.name
                itemEngraveOptionInfo = engraveOptionInfo
            }
            EngraveConfirmItem()() {
                engraveAction = {
                    //开始雕刻
                    _engraveDataInfo?.let {
                        EngraveCmd(
                            it.name,
                            engraveOptionInfo.power,
                            engraveOptionInfo.depth,
                            engraveOptionInfo.state,
                            engraveOptionInfo.x,
                            engraveOptionInfo.y,
                            engraveOptionInfo.time,
                            engraveOptionInfo.type,
                        ).sendCommand { bean, error ->
                            L.w("开始雕刻:${bean?.parse<MiniReceiveParser>()}")

                            if (error == null) {
                                showEngraveProgress(
                                    100,
                                    _string(R.string.print_v2_package_printing)
                                )
                            }
                        }
                    }
                }
            }
        }

        //进入空闲模式
        ExitCmd().sendCommand { _, _ ->
            vmApp<LaserPeckerModel>().queryDeviceState()
        }
    }

    fun findIndex(list: List<Any>?, value: Byte): Int {
        return list?.indexOfFirst { it.toString().toInt() == value.toInt() } ?: -1
    }

    //endregion

}