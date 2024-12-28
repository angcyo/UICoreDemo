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
import com.angcyo.library.L
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.dslitem.AppUsbDeviceItem
import java.util.concurrent.atomic.AtomicBoolean


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

    companion object {
        val ACTION_USB_PERMISSION = "com.android.angcyo.USB_PERMISSION"
    }

    var usbManager: UsbManager? = null

    init {
        enableRefresh = true
        enableAdapterRefresh = false
    }


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
        onLoadData()
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
                AppUsbDeviceItem()() {
                    itemUsbManager = usbManager
                    itemUsbDevice = device
                    itemDeviceIndex = index
                }
            }
        }
    }

}