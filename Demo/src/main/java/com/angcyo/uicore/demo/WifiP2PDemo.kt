package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.updateItem
import com.angcyo.library.ex.visible
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.p2p.AppWifiP2pItem
import com.angcyo.widget.loading.RadarScanLoadingView
import com.angcyo.wifip2p.WifiP2p
import com.angcyo.wifip2p.WifiP2pModel
import com.angcyo.wifip2p.data.ServiceData
import com.angcyo.wifip2p.data.WifiP2pDeviceWrap
import com.angcyo.wifip2p.data.wrap
import java.net.InetSocketAddress
import java.net.ServerSocket

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/08
 */
class WifiP2PDemo : AppDslFragment() {

    var wifiP2pModel = vmApp<WifiP2pModel>()

    init {
        contentLayoutId = R.layout.layout_wifi_p2p
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            headerItems.add(DslAdapterItem().apply {
                itemLayoutId = R.layout.item_wifi_p2p_layout
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    //
                    itemHolder.tv(R.id.lib_text_view)?.text =
                        wifiP2pModel.selfWifiP2pDeviceData.value?.toString()

                    //
                    itemHolder.click(R.id.start_service_button) {
                        //启动服务
                        val data = ServiceData("WifiP2pDemo", "default-type")
                        WifiP2p.startWifiP2pService(data)
                    }
                    itemHolder.click(R.id.stop_service_button) {
                        //停止服务
                        WifiP2p.stopWifiP2pService()
                    }
                    itemHolder.click(R.id.discover_service_button) {
                        //发现服务
                        WifiP2p.discoverWifiP2pService()
                    }
                }
            })
        }

        wifiP2pModel.apply {

            //自身设备信息
            selfWifiP2pDeviceData.observe {
                _adapter.updateAllHeaderItem()
            }

            //可以匹配的设备
            peerDeviceListData.observe { list ->
                if (list != null) {
                    _adapter.changeFooterItems {
                        it.clear()
                        list.forEach { device ->
                            it.add(AppWifiP2pItem().apply {
                                itemData = device.wrap()
                            })
                        }
                    }
                }
            }

            //指定服务的设备
            foundServiceDeviceData.observe { device ->
                if (device != null) {
                    _adapter.changeDataItems {
                        it.add(AppWifiP2pItem().apply {
                            itemData = device
                        })
                    }
                }
            }

            //扫描
            isDiscoverData.observe {
                //radar
                _vh.v<RadarScanLoadingView>(R.id.radar_scan_loading_view)?.apply {
                    visible(it == true)
                    loading(it == true)
                }
            }

            //连接状态
            connectStateData.observe {
                it?.let {
                    _adapter.updateItem { item ->
                        (item.itemData as? WifiP2pDeviceWrap)?.sourceDevice?.deviceAddress ==
                                it.deviceWrap.sourceDevice.deviceAddress
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        WifiP2p.stopWifiP2pService()
    }

    fun testSocket() {
        try {
            val socket = ServerSocket()
            socket.bind(InetSocketAddress(2900))

            val socket2 = ServerSocket()
            socket2.bind(InetSocketAddress(2900))
        } catch (e: Exception) {
            //java.net.BindException: bind failed: EADDRINUSE (Address already in use)
            e.printStackTrace()
        }
    }

}