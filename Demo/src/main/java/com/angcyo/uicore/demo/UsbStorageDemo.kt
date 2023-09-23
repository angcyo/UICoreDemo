package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterStatusItem
import com.angcyo.dsladapter.select
import com.angcyo.dsladapter.singleModel
import com.angcyo.library.ex.dpi
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.usb.storage.UsbFileSelectorItem
import com.angcyo.usb.storage.UsbStorageModel
import com.angcyo.usb.storage.usbFolderSelector
import com.angcyo.widget.span.span
import me.jahnen.libaums.core.fs.UsbFile

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/09/22
 */
class UsbStorageDemo : AppDslFragment() {

    val usbStorageModel = vmApp<UsbStorageModel>()

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        _adapter.render {
            setAdapterStatus(DslAdapterStatusItem.ADAPTER_STATUS_LOADING)
            singleModel()
        }

        usbStorageModel.usbStorageDevicesOnceData.observe { device ->
            device?.let {
                val usbDevice = it.usbDevice
                fragmentTitle = span {
                    append("${usbDevice.productName}/${usbDevice.manufacturerName} ${usbDevice.version}")
                    appendln()
                    append("${usbDevice.deviceName} | ${usbDevice.deviceId}/${usbDevice.productId}/${usbDevice.vendorId}") {
                        fontSize = 12 * dpi
                    }
                }
                _adapter.renderUsbFile(it.partitions.firstOrNull()?.fileSystem?.rootDirectory)
            }
        }

        usbStorageModel.startReceiveUsb()

        appendRightItem("选择") {
            usbFolderSelector(usbStorageModel.selectedDevice?.partitions?.get(0)?.fileSystem?.rootDirectory) {
                it?.let {
                    fragmentTitle = it.absolutePath
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        usbStorageModel.release()
    }

    /**渲染Usb文件*/
    fun DslAdapter.renderUsbFile(usbFile: UsbFile?) {
        render {
            clearAllItems()
            if (usbFile == null) {
            } else if (usbFile.isDirectory) {
                for (file in usbFile.listFiles()) {
                    usbFileSelectorItem(file)
                }
            } else {
                usbFileSelectorItem(usbFile)
            }
        }
    }

    fun DslAdapter.usbFileSelectorItem(file: UsbFile) {
        UsbFileSelectorItem()() {
            itemData = file
            itemClick = {
                if (itemIsFolder(null)) {
                    renderUsbFile(file)
                } else {
                    this@usbFileSelectorItem.select { it == this }
                }
            }
        }
    }

}