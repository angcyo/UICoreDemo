package com.angcyo.uicore.demo

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.os.Bundle
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemText
import com.angcyo.library.L
import com.angcyo.library.ex.toHexString
import com.angcyo.library.toastQQ
import com.angcyo.uicore.base.AppDslFragment
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread


/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2024/12/27
 *
 * https://developer.android.com/develop/connectivity/usb?hl=zh-cn
 * https://developer.android.com/develop/connectivity/usb/host?hl=zh-cn
 *
 *
 * [UsbManager.openDevice]需要权限
 * ```
 * User has not given 10053/com.angcyo.uicore.demo permission to access device /dev/bus/usb/002/002
 * ```
 * https://blog.csdn.net/gd6321374/article/details/78014255
 * https://blog.csdn.net/zhoupuxian/article/details/125343811
 */
class UsbDemo : AppDslFragment() {

    var usbManager: UsbManager? = null

    val readBytes = ByteArray(64) { 0x00 }
    val sendBytes = ByteArray(64 * 2) { 0x00 }

    init {
        enableRefresh = true
        enableAdapterRefresh = false
    }

    private val ACTION_USB_PERMISSION = "com.android.angcyo.USB_PERMISSION"

    private val usbReceiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            if (ACTION_USB_PERMISSION == intent.action) {
                synchronized(this) {
                    val device: UsbDevice? = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE)

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        device?.apply {
                            // call method to set up device communication
                        }
                    } else {
                        L.d("permission denied for device $device")
                    }
                }
            }
        }
    }

    var permissionIntent: PendingIntent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        usbManager = fContext().getSystemService(UsbManager::class.java)
        permissionIntent = PendingIntent.getBroadcast(
            fContext(), 0, Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_IMMUTABLE
        )
        val filter = IntentFilter(ACTION_USB_PERMISSION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                fContext().registerReceiver(usbReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
            } else {
                fContext().registerReceiver(usbReceiver, filter)
            }
        } else {
            fContext().registerReceiver(usbReceiver, filter)
        }
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        onLoadData();
    }

    var isDestroy = AtomicBoolean(false)

    override fun onDestroy() {
        super.onDestroy()
        isDestroy.set(true)
    }

    override fun onLoadData() {
        super.onLoadData()
        renderDslAdapter(clear = true) {
            val deviceList = usbManager?.deviceList
            deviceList?.onEachIndexed { index, entry ->
                val device = entry.value
                DslTextItem()() {
                    itemText = "$index->${entry.key}\n$device"
                    itemClick = {
                        val connection = usbManager?.openDevice(device)
                        if (connection != null) {
                            toastQQ("打开成功")
                            thread {
                                val intf = device.getInterface(0)
                                connection.claimInterface(intf, true)
                                while (!isDestroy.get()) {
                                    _adapter.addLastItem(DslTextItem().apply {
                                        itemText = "开始监听数据..."
                                    })
                                    _adapter.updateItemDepend()

                                    val length = connection.bulkTransfer(
                                        intf.getEndpoint(0), readBytes, readBytes.size, 0
                                    ) //do in another thread
                                    if (length > 0) {
                                        toastQQ("读取成功:$length")
                                        _adapter.addLastItem(DslTextItem().apply {
                                            itemText = readBytes.toHexString()
                                        })
                                        _adapter.updateItemDepend()

                                        // 控制传输参数
                                        val requestType = 0x21
                                        //UsbConstants.USB_TYPE_VENDOR // 或者 USB_TYPE_CLASS, 根据你的设备类型
                                        val request = 0x09 // HID 命令
                                        val value = 0x301 // 对于 HID 通常是 0
                                        val index = 0 // 通常是 0
                                        val length = connection.controlTransfer(
                                            /*UsbConstants.USB_TYPE_VENDOR, 0,0,0,*/
                                            requestType,
                                            request,
                                            value,
                                            index,
                                            sendBytes,
                                            sendBytes.size,
                                            5000
                                        )

                                        if (length > 0) {
                                            toastQQ("写入成功:$length")
                                            _adapter.addLastItem(DslTextItem().apply {
                                                itemText = "写入成功:$length"
                                            })
                                            _adapter.updateItemDepend()
                                        } else {
                                            toastQQ("写入失败")
                                            _adapter.addLastItem(DslTextItem().apply {
                                                itemText = "写入失败:${length}"
                                            })
                                            _adapter.updateItemDepend()
                                        }

                                    } else {
                                        toastQQ("读取失败")
                                        _adapter.addLastItem(DslTextItem().apply {
                                            itemText = "读取失败:${length}"
                                        })
                                        _adapter.updateItemDepend()
                                        break
                                    }
                                }
                            }
                        } else {
                            usbManager?.requestPermission(device, permissionIntent)
                            toastQQ("打开失败")
                        }
                    }
                }
            }
        }
    }

}