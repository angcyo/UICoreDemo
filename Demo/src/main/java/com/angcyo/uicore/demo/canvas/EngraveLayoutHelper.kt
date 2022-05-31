package com.angcyo.uicore.demo.canvas

import androidx.fragment.app.Fragment
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.command.*
import com.angcyo.bluetooth.fsc.laserpacker.parse.FileTransferParser
import com.angcyo.bluetooth.fsc.laserpacker.parse.MiniReceiveParser
import com.angcyo.bluetooth.fsc.parse
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.core.CanvasEntryPoint
import com.angcyo.canvas.items.PictureShapeItem
import com.angcyo.canvas.items.renderer.BaseItemRenderer
import com.angcyo.canvas.utils.EngraveHelper
import com.angcyo.core.vmApp
import com.angcyo.library.L
import com.angcyo.library.ex.file
import com.angcyo.library.toast
import com.angcyo.library.utils.fileName
import com.angcyo.library.utils.filePath

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/30
 */
class EngraveLayoutHelper(val fragment: Fragment) {

    /**绑定布局*/
    @CanvasEntryPoint
    fun bindLayout(canvasView: CanvasView) {
        vmApp<LaserPeckerModel>().productInfoData.observe(fragment) { productInfo ->
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

    /**雕刻*/
    fun engrave(renderer: BaseItemRenderer<*>) {
        val item = renderer.getRendererItem() ?: return

        if (item is PictureShapeItem) {
            val path = item.shapePath
            if (path != null) {
                fragment.loadingAsync({
                    val file = EngraveHelper.pathStrokeToGCode(
                        path,
                        renderer.getRotateBounds(),
                        renderer.rotate,
                        filePath(
                            "GCode",
                            fileName(suffix = ".gcode")
                        ).file()
                    )
                    file
                }) {
                    //no op
                    it?.let { file ->
                        val data = file.readBytes()

                        val cmd = FileModeCmd(data.size)
                        LaserPeckerHelper.sendCommand(cmd) { bean, error ->
                            bean?.parse<FileTransferParser>()?.let {
                                if (it.isIntoFileMode()) {
                                    //成功进入大数据模式

                                    val name = (System.currentTimeMillis() / 1000).toInt()
                                    LaserPeckerHelper.sendCommand(
                                        DataCommand.gcodeData(name, data)
                                    ) { bean, error ->

                                        bean?.parse<FileTransferParser>()?.let {
                                            if (it.isFileTransferSuccess()) {
                                                //文件传输完成
                                                L.w("文件传输完成:$name")

                                                //进入空闲模式
                                                ExitCmd().send { _, _ ->
                                                    //开始雕刻
                                                    EngraveCmd(name).send { bean, error ->
                                                        L.w("开始雕刻:${bean?.parse<MiniReceiveParser>()}")
                                                    }
                                                }
                                            } else {
                                                L.w("文件传输失败:$name:$it")
                                            }
                                        }
                                    }
                                } else {
                                    toast("file cmd error.")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}