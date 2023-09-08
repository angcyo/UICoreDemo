package com.angcyo.uicore.demo.p2p

import android.net.wifi.p2p.WifiP2pDevice
import androidx.fragment.app.Fragment
import com.angcyo.core.component.file.file
import com.angcyo.core.component.fileSelector
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.item.IFragmentItem
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemDes
import com.angcyo.item.style.itemText
import com.angcyo.library.ex.fileSizeString
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.toMsTime
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span
import com.angcyo.wifip2p.WifiP2p
import com.angcyo.wifip2p.WifiP2pModel
import com.angcyo.wifip2p.data.ConnectStateWrap.Companion.STATE_CONNECT_START
import com.angcyo.wifip2p.data.ConnectStateWrap.Companion.STATE_CONNECT_SUCCESS
import com.angcyo.wifip2p.data.ProgressInfo
import com.angcyo.wifip2p.data.WifiP2pDeviceWrap
import com.angcyo.wifip2p.data.servicePort
import com.angcyo.wifip2p.task.FileSendDataStream
import com.angcyo.wifip2p.task.WifiP2pSendRunnable

/**
 * [WifiP2pDevice]
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/09
 */
class AppWifiP2pItem : DslTextItem(), IFragmentItem {

    override var itemFragment: Fragment? = null

    var wifiP2pModel = vmApp<WifiP2pModel>()

    var itemProgressInfo: ProgressInfo? = null

    init {
        itemLayoutId = R.layout.app_wifi_p2p_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        val deviceWrap = itemData as? WifiP2pDeviceWrap

        deviceWrap?.let {
            val connectState = wifiP2pModel.connectState(deviceWrap)
            itemHolder.tv(R.id.connect_button)?.text = when (connectState.state) {
                STATE_CONNECT_START -> "连接中..."
                STATE_CONNECT_SUCCESS -> "连接完成"
                else -> "连接设备"
            }

            itemHolder.visible(R.id.send_button, connectState.state == STATE_CONNECT_SUCCESS)

            //发送进度
            val progressInfo = itemProgressInfo
            itemHolder.tv(R.id.send_button)?.text =
                if (connectState.state == STATE_CONNECT_SUCCESS && progressInfo != null) {
                    val progress = progressInfo.progress
                    val speed = progressInfo.speed.speed
                    if (progress == 100) {
                        "${speed.fileSizeString()}/s 耗时:${
                            progressInfo.speed.duration().toMsTime()
                        } 共:${progressInfo.speed.targetTotal.fileSizeString()}"
                    } else {
                        "${speed.fileSizeString()}/s"
                    }
                } else {
                    "发送数据"
                }
        }

        itemHolder.click(R.id.connect_button) {
            //连接设备
            deviceWrap?.let {
                WifiP2p.connectWifiP2pService(it)
            }
        }

        itemHolder.click(R.id.send_button) {
            //发送数据
            deviceWrap?.let { deviceWrap ->
                val connectState = wifiP2pModel.connectState(deviceWrap)
                connectState.deviceWrap.wifiP2pInfo?.let { wifiP2pInfo ->
                    itemFragment?.fileSelector {
                        it?.file()?.let { file ->
                            wifiP2pInfo.groupOwnerAddress?.hostAddress?.let { ip ->
                                WifiP2pSendRunnable(
                                    deviceWrap.sourceDevice.deviceAddress,
                                    ip,
                                    deviceWrap.servicePort,
                                    FileSendDataStream(file)
                                ).start()
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onSelfSetItemData(data: Any?) {
        super.onSelfSetItemData(data)

        (data as? WifiP2pDeviceWrap)?.apply {
            itemText = span {
                append(nowTimeString())
                instanceName?.let {
                    appendLine()
                    append("$it/$registrationType")
                }
            }
            itemDes = sourceDevice.toString()
        }
    }

}