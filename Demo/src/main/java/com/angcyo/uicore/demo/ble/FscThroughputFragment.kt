package com.angcyo.uicore.demo.ble

import android.os.Bundle
import android.text.method.TextKeyListener
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bluetooth.fsc.ReceivePacket
import com.angcyo.bluetooth.fsc.core.DevicePacketState
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker.command.EngraveCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.EngravePreviewCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.QueryCmd
import com.angcyo.bluetooth.fsc.laserpacker.parse.*
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.dslItem
import com.angcyo.dsladapter.isUpdatePart
import com.angcyo.engrave.EngraveHelper
import com.angcyo.getData
import com.angcyo.http.rx.doMain
import com.angcyo.library.ex._dimen
import com.angcyo.library.ex.fileSizeString
import com.angcyo.library.ex.toHexByteArray
import com.angcyo.library.ex.toHexString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget._ev
import com.angcyo.widget.base.onTextChange
import com.angcyo.widget.base.setInputText
import com.angcyo.widget.edit.NumKeyListener
import com.angcyo.widget.progress.DslProgressBar
import com.angcyo.widget.span.span
import com.feasycom.common.bean.FscDevice
import okhttp3.internal.toHexString
import java.util.zip.CRC32
import kotlin.math.max

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/22
 */
class FscThroughputFragment : AppDslFragment() {

    var fscDevice: FscDevice? = null

    val fscModel = vmApp<FscBleApiModel>()

    //需要发送的数据
    var byteBuffer = byteArrayOf()

    //接收到的数据
    var receiveBuffer = byteArrayOf()

    val sendCRC = CRC32() // 发送crc
    val receiveCRC = CRC32() // 接收crc

    val defaultName = 63780

    init {
        enableSoftInput = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fscDevice = getData()
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        updateTitle()
        fscModel.connectStateData.observe {
            if (it?.device == fscDevice) {
                updateTitle()
            }
        }

        fscModel.devicePacketStateData.observe {
            if (it?.address == fscDevice?.address) {
                if (it?.state == DevicePacketState.PACKET_STATE_START) {
                    //发送数据
                    sendCRC.update(it.bytes)
                } else if (it?.state == DevicePacketState.PACKET_STATE_RECEIVED) {
                    //接收数据
                    receiveCRC.update(it.bytes)
                    receiveBuffer = it.bytes.copyOf()
                }
                _adapter[0]?.updateAdapterItem()
            }
        }

        renderDslAdapter {
            dslItem(R.layout.item_fsc_throughput_layout) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    val hexSwitch = itemHolder.cb(R.id.hex_switch)
                    val autoSendSwitch = itemHolder.cb(R.id.auto_send_switch)
                    val sendEditView = itemHolder._ev(R.id.send_edit_view)
                    val packetProgressView = itemHolder.v<DslProgressBar>(R.id.packet_progress_view)

                    itemHolder.tv(R.id.send_crc_view)?.text =
                        "CRC32:${sendCRC.value.toHexString().uppercase()}"

                    itemHolder.tv(R.id.receive_crc_view)?.text =
                        "CRC32:${receiveCRC.value.toHexString().uppercase()}"

                    //接收到的数据
                    itemHolder.tv(R.id.receive_text_view)?.text = receiveBuffer.toHexString(true)

                    if (payloads.isUpdatePart()) {
                        //进度更新
                        fscModel.findProgressCache(fscDevice?.address)?.let {
                            val time =
                                max(1, ((System.currentTimeMillis() - it.startTime) / 1000))
                            val rate = "${it.sendBytesSize / time} bytes/s"
                            if (it.percentage >= 0) {
                                packetProgressView?.setProgress(it.percentage)
                                itemHolder.tv(R.id.rate_view)?.text = rate
                            } else {
                                packetProgressView?.setProgress(0)
                            }
                            if (it.percentage == 100) {
                                itemHolder.tv(R.id.rate_view)?.text =
                                    "耗时:${(it.finishTime - it.startTime) / 1000} s 共:${it.sendBytesSize.fileSizeString()} $rate "
                            }
                            if (it.percentage >= 0 && it.percentage != 100) {
                                itemHolder.visible(R.id.stop_button)
                                itemHolder.visible(R.id.pause_button)
                            } else {
                                itemHolder.gone(R.id.stop_button)
                                itemHolder.gone(R.id.pause_button)
                            }

                            itemHolder.tv(R.id.send_tip_view)?.text =
                                "发送${it.sendBytesSize}字节${it.sendPacketCount}包"

                            itemHolder.tv(R.id.pause_button)?.text = if (it.isPause) {
                                "继续发送"
                            } else {
                                "暂停发送"
                            }
                        }
                        fscModel.findReceiveCache(fscDevice?.address)?.let {
                            itemHolder.tv(R.id.receive_tip_view)?.text =
                                "接收${it.receiveBytesSize}字节${it.receivePacketCount}包"
                        }
                    } else {
                        hexSwitch?.setOnCheckedChangeListener { buttonView, isChecked ->
                            if (isChecked) {
                                sendEditView?.keyListener = NumKeyListener()
                                sendEditView?.setInputText(byteBuffer.toHexString(true))
                                sendEditView?.editDelegate?.patternEnable = isChecked
                            } else {
                                sendEditView?.editDelegate?.patternEnable = false
                                sendEditView?.keyListener = TextKeyListener.getInstance()
                                sendEditView?.setInputText(byteBuffer.toString(Charsets.UTF_8))
                            }
                        }

                        sendEditView?.apply {
                            onTextChange {
                                val text = it.toString()
                                byteBuffer = if (hexSwitch?.isChecked == true) {
                                    //输入的是16进制
                                    text.toHexByteArray()
                                } else {
                                    text.toByteArray()
                                }

                                itemHolder.tv(R.id.send_count_view)?.text =
                                    "${byteBuffer.size} 字节(bytes)"
                            }
                        }

                        //send

                        itemHolder.click(R.id.send_button) {
                            fscDevice?.let { device ->
                                //fscModel.send(device.address, byteBuffer)

                                LaserPeckerHelper.waitCmdReturn(
                                    fscModel,
                                    device.address,
                                    byteBuffer, progress = {
                                        doMain {
                                            itemHolder.tv(R.id.result_text_view)?.text =
                                                "发送中:${it.sendPacketPercentage}%"
                                        }
                                    }
                                ) { bean, error ->
                                    doMain {
                                        bean?.let {
                                            receiveBuffer = it.receivePacket

                                            //接收到的数据
                                            itemHolder.tv(R.id.receive_text_view)?.text =
                                                receiveBuffer.toHexString(true)

                                            handleResult(itemHolder, it)
                                        }
                                        error?.let {
                                            itemHolder.tv(R.id.result_text_view)?.text = it.message
                                        }
                                    }
                                }
                            }
                        }

                        itemHolder.click(R.id.send_file_button) {
                            fscDevice?.let { device ->
                                cmdClass = null
                                fscModel.sendFile(device.address, 5 * 1024 * 1024)
                            }
                        }

                        itemHolder.click(R.id.stop_button) {
                            fscDevice?.let { device ->
                                fscModel.stopSend(device.address)
                            }
                        }

                        itemHolder.click(R.id.pause_button) {
                            fscDevice?.let { device ->
                                if (fscModel.findProgressCache(device.address)?.isPause == true) {
                                    fscModel.continueSend(device.address)
                                } else {
                                    fscModel.pauseSend(device.address)
                                }
                            }
                        }

                        fun setCommand(cmd: String, cls: Class<*>? = null) {
                            hexSwitch?.isChecked = true
                            sendEditView?.setInputText(cmd)
                            cmdClass = cls
                            if (autoSendSwitch?.isChecked == true) {
                                itemHolder.clickCallView(R.id.send_button)
                            }
                        }

                        //control

                        //指令-工作状态
                        itemHolder.click(R.id.state_command0) {
                            setCommand(
                                QueryCmd.workState.toHexCommandString(),
                                QueryStateParser::class.java
                            )
                        }
                        //指令-文件列表
                        itemHolder.click(R.id.state_command1) {
                            setCommand(
                                QueryCmd.fileList.toHexCommandString(),
                                QueryEngraveFileParser::class.java
                            )
                        }
                        //指令-设置状态
                        itemHolder.click(R.id.state_command2) {
                            setCommand(
                                QueryCmd.settingState.toHexCommandString(),
                                QuerySettingParser::class.java
                            )
                        }
                        //指令-查询版本
                        itemHolder.click(R.id.state_command3) {
                            setCommand(
                                QueryCmd.version.toHexCommandString(),
                                QueryVersionParser::class.java
                            )
                        }
                        //查询安全码与用户帐号
                        itemHolder.click(R.id.state_command4) {
                            setCommand(
                                QueryCmd.safeCode.toHexCommandString(),
                                QuerySafeCodeParser::class.java
                            )
                        }

                        //指令-打印
                        itemHolder.click(R.id.print_command0) {
                            setCommand(
                                EngraveCmd(defaultName).toHexCommandString(),
                                EngraveReceiveParser::class.java
                            )
                        }
                        //指令-预览图片
                        itemHolder.click(R.id.preview_command0) {
                            setCommand(
                                EngravePreviewCmd.previewFlashBitmap(defaultName)
                                    .toHexCommandString(),
                                EngravePreviewParser::class.java
                            )
                        }
                        //指令-预览范围
                        itemHolder.click(R.id.preview_command1) {
                            setCommand(
                                EngravePreviewCmd.previewRange(
                                    0,
                                    0,
                                    60,
                                    20,
                                    EngraveHelper.lastPwrProgress,
                                    0
                                ).toHexCommandString(),
                                EngravePreviewParser::class.java
                            )
                        }
                        //指令-结束预览
                        itemHolder.click(R.id.preview_command2) {
                            setCommand(
                                EngravePreviewCmd(0x03).toHexCommandString(),
                                EngravePreviewParser::class.java
                            )
                        }
                        //升支架
                        itemHolder.click(R.id.preview_command3) {
                            setCommand(
                                EngravePreviewCmd.previewBracketUp().toHexCommandString(),
                                EngravePreviewParser::class.java
                            )
                        }
                        //降支架
                        itemHolder.click(R.id.preview_command4) {
                            setCommand(
                                EngravePreviewCmd.previewBracketDown().toHexCommandString(),
                                EngravePreviewParser::class.java
                            )
                        }
                    }
                }
            }
        }
    }

    fun updateTitle() {
        fscDevice?.let { device ->
            fragmentTitle = span {
                append(device.name ?: "unknown")
                appendln()
                append(if (fscModel.isConnected(fscDevice)) "已连接" else "未连接") {
                    fontSize = _dimen(R.dimen.text_min_size)
                }
                append(" ${device.mode}") {
                    fontSize = _dimen(R.dimen.text_min_size)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fscModel.disconnect(fscDevice)
    }

    var cmdClass: Class<*>? = null

    fun handleResult(holder: DslViewHolder, bean: ReceivePacket) {
        when (cmdClass) {
            QueryStateParser::class.java -> QueryStateParser().parse(bean.receivePacket)
            QueryEngraveFileParser::class.java -> QueryEngraveFileParser().parse(bean.receivePacket)
            QuerySettingParser::class.java -> QuerySettingParser().parse(bean.receivePacket)
            QueryVersionParser::class.java -> QueryVersionParser().parse(bean.receivePacket)
            QuerySafeCodeParser::class.java -> QuerySafeCodeParser().parse(bean.receivePacket)
            EngraveReceiveParser::class.java -> EngraveReceiveParser().parse(bean.receivePacket)
            EngravePreviewParser::class.java -> EngravePreviewParser().parse(bean.receivePacket)
            else -> null
        }?.let {
            holder.tv(R.id.result_text_view)?.text = it.toString()
        }

        cmdClass = null
    }

}