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
import com.angcyo.uicore.demo.UsbHidDemo.Companion.ACTION_USB_PERMISSION
import com.angcyo.uicore.demo.dslitem.AppUsbDeviceItem
import java.util.concurrent.atomic.AtomicBoolean


/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2024/12/27
 *
 * [UsbHidDemo]
 */
class UsbSerialDemo : AppDslFragment() {

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
                    itemUseSerial = true
                }
            }
        }
    }

}