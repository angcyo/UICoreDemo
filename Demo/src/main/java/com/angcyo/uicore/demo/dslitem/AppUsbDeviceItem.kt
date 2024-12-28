package com.angcyo.uicore.demo.dslitem

import android.app.PendingIntent
import android.content.Intent
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbDeviceConnection
import android.hardware.usb.UsbManager
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.component.onMain
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.toHexString
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.demo.UsbDemo.Companion.ACTION_USB_PERMISSION
import com.angcyo.widget.DslViewHolder
import kotlin.concurrent.thread

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2024/12/28
 */
class AppUsbDeviceItem : DslAdapterItem() {

    var itemDeviceIndex = 0
    var itemUsbManager: UsbManager? = null
    var itemUsbDevice: UsbDevice? = null

    init {
        itemLayoutId = R.layout.app_item_usb_device
    }

    val readBytes = ByteArray(64) { 0x00 }
    val sendBytes = ByteArray(64 * 2) { 0xF0.toByte() }
    var deviceConnection: UsbDeviceConnection? = null
    var permissionIntent: PendingIntent? = null

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

        //--
        itemHolder.click(R.id.connect_button) {
            if (deviceConnection == null) {
                val connection = itemUsbManager?.openDevice(itemUsbDevice)
                if (connection != null) {
                    deviceConnection = connection
                    itemHolder.tv(R.id.lib_des_view)?.text = "连接成功${nowTimeString()}"
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
                deviceConnection?.close()
                deviceConnection = null
                itemHolder.tv(R.id.lib_des_view)?.text = "连接已断开${nowTimeString()}"
            } else {
                itemHolder.tv(R.id.lib_des_view)?.text = "设备未连接${nowTimeString()}"
            }
        }
        //--
        itemHolder.click(R.id.read_button) {
            if (deviceConnection != null) {
                val intf = itemUsbDevice!!.getInterface(0)
                deviceConnection!!.claimInterface(intf, true)

                thread {
                    onMain {
                        itemHolder.tv(R.id.lib_des_view)?.text = "等待读取数据...${nowTimeString()}"
                    }
                    val length = deviceConnection!!.bulkTransfer(
                        intf.getEndpoint(0), readBytes, readBytes.size, 5000
                    ) //do in another thread
                    if (length > 0) {
                        onMain {
                            itemHolder.tv(R.id.lib_des_view)?.text =
                                "读取成功:${readBytes.toHexString()}"
                        }
                    } else {
                        onMain {
                            itemHolder.tv(R.id.lib_des_view)?.text = "读取失败:${nowTimeString()}"
                        }
                    }
                }
            }
        }
        itemHolder.click(R.id.write_bulk_button) {
            if (deviceConnection != null) {
                val intf = itemUsbDevice!!.getInterface(0)
                val length = deviceConnection!!.bulkTransfer(
                    intf.getEndpoint(1), sendBytes, sendBytes.size, 5000
                ) //do in another thread
                if (length > 0) {
                    onMain {
                        itemHolder.tv(R.id.lib_des_view)?.text = "写入成功:$length ${nowTimeString()}"
                    }
                } else {
                    onMain {
                        itemHolder.tv(R.id.lib_des_view)?.text = "写入失败:${nowTimeString()}"
                    }
                }
            }
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
                        itemHolder.tv(R.id.lib_des_view)?.text = "写入成功:$length ${nowTimeString()}"
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
        deviceConnection?.close()
    }
}