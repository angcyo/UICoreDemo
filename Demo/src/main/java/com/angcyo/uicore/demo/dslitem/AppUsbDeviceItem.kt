package com.angcyo.uicore.demo.dslitem

import android.app.PendingIntent
import android.content.Intent
import android.hardware.usb.UsbConstants
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbEndpoint
import android.hardware.usb.UsbInterface
import android.hardware.usb.UsbManager
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.http.rx.doMain
import com.angcyo.library.L
import com.angcyo.library.component.Speed
import com.angcyo.library.component.onMain
import com.angcyo.library.ex.fileSizeString
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.sleep
import com.angcyo.library.ex.toHexString
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.demo.UsbHidDemo.Companion.ACTION_USB_PERMISSION
import com.angcyo.widget.DslViewHolder
import com.felhr.usbserial.UsbSerialDevice
import com.felhr.usbserial.UsbSerialInterface
import com.felhr.usbserial.UsbSerialInterface.UsbReadCallback
import kotlin.concurrent.thread
import kotlin.math.min

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2024/12/28
 */
class AppUsbDeviceItem : DslAdapterItem() {

    var itemDeviceIndex = 0
    var itemUsbManager: UsbManager? = null
    var itemUsbDevice: UsbDevice? = null

    //--

    /**是否要使用串口设备通信
     * https://github.com/felHR85/UsbSerial?
     * */
    var itemUseSerial = false
    var serialDevice: UsbSerialDevice? = null

    init {
        itemLayoutId = R.layout.app_item_usb_device
    }

    val readBytes = ByteArray(64) { 0x00 }
    val sendBytes = ByteArray(64) { 0xF0.toByte() }
    var deviceConnection: UsbDeviceConnection? = null
    var permissionIntent: PendingIntent? = null

    //--

    var readCallback: UsbReadCallback? = null

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        if (permissionIntent == null) {
            permissionIntent = PendingIntent.getBroadcast(
                itemHolder.context, 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
            )
        }

        itemHolder.tv(R.id.lib_text_view)?.text = "$itemDeviceIndex->${itemUsbDevice}"
        itemHolder.tv(R.id.lib_des_view)?.text = logUsbDevice(itemUsbDevice).apply {
            L.d(this)
        }

        //--
        itemHolder.click(R.id.connect_button) {
            if (deviceConnection == null) {
                val connection = itemUsbManager?.openDevice(itemUsbDevice)
                if (connection != null) {
                    deviceConnection = connection
                    itemHolder.tv(R.id.lib_des_view)?.text = "连接成功${nowTimeString()}"

                    if (itemUseSerial) {
                        serialDevice =
                            UsbSerialDevice.createUsbSerialDevice(itemUsbDevice!!, connection)

                        serialDevice!!.open()
                        //serialDevice!!.setBaudRate(115_200)
                        serialDevice!!.setBaudRate(2_000_000)
                        serialDevice!!.setDataBits(UsbSerialInterface.DATA_BITS_8)
                        serialDevice!!.setParity(UsbSerialInterface.PARITY_ODD)
                        serialDevice!!.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF)

                        /*serialDevice!!.setRTS(true) // Raised
                        serialDevice!!.setRTS(false) // Not Raised
                        serialDevice!!.setDTR(true) // Raised
                        serialDevice!!.setDTR(false) // Not Raised*/
                    }
                } else {
                    itemHolder.tv(R.id.lib_des_view)?.text = "连接失败${nowTimeString()}"
                    itemUsbManager?.requestPermission(itemUsbDevice!!, permissionIntent!!)
                }
            } else {
                itemHolder.tv(R.id.lib_des_view)?.text = "设备已连接${nowTimeString()}"
            }
        }
        itemHolder.click(R.id.close_button) {
            if (deviceConnection != null) {
                closeDevice()
                itemHolder.tv(R.id.lib_des_view)?.text = "连接已断开${nowTimeString()}"
            } else {
                itemHolder.tv(R.id.lib_des_view)?.text = "设备未连接${nowTimeString()}"
            }
        }
        //--
        itemHolder.click(R.id.read_button) {
            raedData(itemHolder)
        }
        itemHolder.click(R.id.write_bulk_button) {
            if (deviceConnection != null) {
                sendData(itemHolder, null)
            }
        }
        itemHolder.click(R.id.write_bulk_100k_button) {
            sendData(itemHolder, 100 * 1024)
        }
        itemHolder.click(R.id.write_bulk_1_button) {
            sendData(itemHolder, 1 * 1024 * 1024)
        }
        itemHolder.click(R.id.write_bulk_10_button) {
            sendData(itemHolder, 10 * 1024 * 1024)
        }
        itemHolder.click(R.id.write_control_button) {
            if (deviceConnection != null) {
                // 控制传输参数
                val requestType = 0x21
                //UsbConstants.USB_TYPE_VENDOR // 或者 USB_TYPE_CLASS, 根据你的设备类型
                val request = 0x09 // HID 命令
                val value = 0x301 // 对于 HID 通常是 0
                val index = 0 // 通常是 0
                val length =
                    deviceConnection!!.controlTransfer(/*UsbConstants.USB_TYPE_VENDOR, 0,0,0,*/
                        requestType, request, value, index, sendBytes, sendBytes.size, 5000
                    )
                if (length > 0) {
                    onMain {
                        itemHolder.tv(R.id.lib_des_view)?.text =
                            "写入成功:$length ${nowTimeString()}"
                    }
                } else {
                    onMain {
                        itemHolder.tv(R.id.lib_des_view)?.text = "写入失败:${nowTimeString()}"
                    }
                }
            }
        }
    }

    override fun onItemViewDetachedToWindow(itemHolder: DslViewHolder, itemPosition: Int) {
        super.onItemViewDetachedToWindow(itemHolder, itemPosition)
        closeDevice()
    }

    fun closeDevice() {
        serialDevice?.close()
        serialDevice = null
        deviceConnection?.close()
        deviceConnection = null
    }

    /**读取数据*/
    fun raedData(itemHolder: DslViewHolder) {
        if (deviceConnection != null) {/*val intf = itemUsbDevice!!.getInterface(0)
                deviceConnection!!.claimInterface(intf, true)*/

            thread {
                onMain {
                    itemHolder.tv(R.id.lib_des_view)?.text = "等待读取数据...${nowTimeString()}"
                }

                if (itemUseSerial) {
                    if (readCallback == null) {
                        val callback = UsbReadCallback { bytes ->
                            onMain {
                                itemHolder.tv(R.id.lib_des_view)?.text =
                                    "读取成功:${bytes.toHexString()}"
                            }
                        }
                        readCallback = callback
                        serialDevice?.read(callback)
                    }
                } else {
                    val pari = getReadEndpoint(itemUsbDevice!!)
                    if (deviceConnection!!.claimInterface(pari.first, true)) {
                        val length = deviceConnection!!.bulkTransfer(
                            pari.second, readBytes, readBytes.size, 5000
                        ) //do in another thread
                        if (length > 0) {
                            onMain {
                                itemHolder.tv(R.id.lib_des_view)?.text =
                                    "读取成功:${readBytes.toHexString()}"
                            }
                        } else {
                            onMain {
                                itemHolder.tv(R.id.lib_des_view)?.text =
                                    "读取失败:${nowTimeString()}"
                            }
                        }
                    } else {
                        onMain {
                            itemHolder.tv(R.id.lib_des_view)?.text =
                                "读取失败.claimInterface:${nowTimeString()}"
                        }
                    }
                }
            }
        }
    }

    /**发送数据
     * [sendByteCount]需要发送的数据字节大小*/
    fun sendData(itemHolder: DslViewHolder, sendByteCount: Long?) {
        if (deviceConnection != null) {
            thread {
                val speed = Speed()
                val pari = getWriteEndpoint(itemUsbDevice!!)
                val packetSize = pari.second?.maxPacketSize ?: 8192
                val byteCount = sendByteCount ?: packetSize.toLong()

                var sendCount = 0/*val bytes = ByteArray(packetSize) { 0x00 }*/
                while (sendCount < byteCount) {
                    val count = min(/*16384*/ packetSize, (byteCount - sendCount).toInt())
                    val bytes = ByteArray(count) { if (sendCount == 0) 0xF0.toByte() else 0x00 }

                    val length = if (itemUseSerial) {
                        serialDevice?.write(bytes)
                        bytes.size
                    } else {
                        deviceConnection!!.bulkTransfer(
                            pari.second, bytes, bytes.size, 5000
                        ) //do in another thread
                    }

                    if (packetSize < 1024) {
                        sleep(1)
                    }

                    doMain {
                        L.d("sendData: $length $sendCount/$byteCount")
                    }
                    if (length > 0) {
                        speed.update(count.toLong(), byteCount)
                        sendCount += count

                        onMain {
                            itemHolder.tv(R.id.lib_des_view)?.text =
                                "写入成功:$length $sendCount/${byteCount} ${nowTimeString()}"
                        }
                    } else {
                        onMain {
                            itemHolder.tv(R.id.lib_des_view)?.text = "写入失败:${nowTimeString()}"
                        }
                        break
                    }
                }
                if (sendCount >= byteCount) {
                    onMain {
                        itemHolder.tv(R.id.lib_des_view)?.text =
                            "写入成功:$sendCount ${nowTimeString()} 耗时:${speed.duration()}ms 速率:${speed.speed.fileSizeString()}/s"
                    }
                }
            }
        }
    }

    /**
     * [UsbConstants.USB_ENDPOINT_XFER_INT]
     * [UsbConstants.USB_DIR_IN]  0x80 128
     * [UsbConstants.USB_DIR_OUT] 0x00 0
     * */
    fun logUsbDevice(device: UsbDevice?): String = buildString {
        if (device == null) {
            append("null\n")
        } else {
            for (i in 0 until device.interfaceCount) {
                val inter = device.getInterface(i)
                for (j in 0 until inter.endpointCount) {
                    val endpoint = inter.getEndpoint(j)/*if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_INT && endpoint.direction == UsbConstants.USB_DIR_IN) {
                    }*/
                    //0:0->type:3,direction:128,address:129,maxPacketSize:64,interval:10
                    //0:1->type:3,direction:0,address:1,maxPacketSize:64,interval:10
                    append("$i:$j->type:${endpoint.type},direction:${endpoint.direction},address:${endpoint.address},maxPacketSize:${endpoint.maxPacketSize},interval:${endpoint.interval}\n")
                }
            }
        }
    }

    fun getReadEndpoint(device: UsbDevice?): Pair<UsbInterface?, UsbEndpoint?> {
        if (device != null) {
            for (i in 0 until device.interfaceCount) {
                val inter = device.getInterface(i)
                for (j in 0 until inter.endpointCount) {
                    val endpoint = inter.getEndpoint(j)
                    if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_INT && endpoint.direction == UsbConstants.USB_DIR_IN) {
                        return inter to endpoint
                    }
                }
            }
        }
        return null to null
    }

    fun getWriteEndpoint(device: UsbDevice?): Pair<UsbInterface?, UsbEndpoint?> {
        if (device != null) {
            for (i in 0 until device.interfaceCount) {
                val inter = device.getInterface(i)
                for (j in 0 until inter.endpointCount) {
                    val endpoint = inter.getEndpoint(j)
                    if (endpoint.type == UsbConstants.USB_ENDPOINT_XFER_INT && endpoint.direction == UsbConstants.USB_DIR_OUT) {
                        return inter to endpoint
                    }
                }
            }
        }
        return null to null
    }
}