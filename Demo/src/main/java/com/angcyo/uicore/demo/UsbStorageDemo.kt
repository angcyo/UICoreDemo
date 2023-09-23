package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.base.dslFHelper
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapterStatusItem
import com.angcyo.dsladapter.singleModel
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.isDebugType
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.usb.storage.UsbStorageFolderSelectorHelper
import com.angcyo.usb.storage.UsbStorageModel
import com.angcyo.usb.storage.usbFolderSelector
import com.angcyo.widget.span.span

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/09/22
 */
class UsbStorageDemo : AppDslFragment() {

    val usbStorageModel = vmApp<UsbStorageModel>()

    val usbStorageFolderSelectorHelper = UsbStorageFolderSelectorHelper().apply {
        usbSelectorConfig.isSelectFile = true
        usbSelectorConfig.isSelectFolder = true
        usbSelectorConfig.showFileMenu = true
        removeThisAction = {
            removeFragment()
        }
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        usbStorageFolderSelectorHelper.init(_vh)

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
                //_adapter.renderUsbFile(it.partitions.firstOrNull()?.fileSystem?.rootDirectory)
                usbStorageFolderSelectorHelper.firstLoad(usbStorageModel.selectedFileSystem)
            }
        }

        usbStorageModel.startReceiveUsb()

        appendRightItem("选择") {
            usbFolderSelector(usbStorageModel.selectedFileSystem) {
                it?.let {
                    fragmentTitle = it.absolutePath
                }
            }
        }
    }

    override fun onBackPressed(): Boolean {
        usbStorageFolderSelectorHelper.onBackPressed()
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!BuildConfig.BUILD_TYPE.isDebugType()) {
            usbStorageModel.release()
        }
    }

    /**移除界面*/
    private fun removeFragment() {
        dslFHelper {
            //noAnim()
            remove(this@UsbStorageDemo)
        }
    }

}