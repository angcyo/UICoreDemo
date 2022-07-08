package com.angcyo.uicore.activity.firmware

import android.content.Intent
import android.os.Bundle
import com.angcyo.base.back
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.core.fragment.BaseDslFragment
import com.angcyo.core.vmApp
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.file
import com.angcyo.library.ex.getPathFromUri
import com.angcyo.library.toast
import com.angcyo.widget.span.span

/**
 * 固件升级界面
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/07/08
 */
class FirmwareUpdateFragment : BaseDslFragment() {

    companion object {
        /**固件的扩展名*/
        const val FIRMWARE_EXT = ".lpbin"
    }

    init {
        fragmentTitle = "固件升级"
        enableAdapterRefresh = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        vmApp<FscBleApiModel>().connectDeviceListData.observe {
            if (it.isNullOrEmpty()) {
                fragmentTitle = "固件升级"
            } else {
                it.first().let { deviceState ->
                    fragmentTitle = span {
                        appendLine(deviceState.device.name)
                        append(deviceState.device.address) {
                            fontSize = 12 * dpi
                        }
                    }
                }
            }
        }
    }

    override fun onLoadData() {
        super.onLoadData()
        var support = false
        activity?.intent?.let { intent ->
            val action = intent.action
            if (action == Intent.ACTION_VIEW) {
                val data = intent.data
                val path = data?.getPathFromUri()

                if (path?.endsWith(FIRMWARE_EXT) == true && path.file().canRead()) {
                    support = true

                    renderDslAdapter {
                        FirmwareUpdateItem()() {
                            itemFirmwareInfo = path.toFirmwareInfo()
                        }
                    }
                }
            }
        }

        if (!support) {
            toast("not support!")
            back()
        }
    }

}