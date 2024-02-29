package com.angcyo.uicore.demo

import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.os.Bundle
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.core.component.model.DataShareModel
import com.angcyo.core.component.model.NsdModel
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.renderItem
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemText
import com.angcyo.library.L
import com.angcyo.library.component.Port
import com.angcyo.library.ex._color
import com.angcyo.library.ex.toStr
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.string
import com.angcyo.widget.span.span

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/12/25
 */
class NsdDemo : AppDslFragment() {

    val nsdModel = vmApp<NsdModel>()
    val shareModel = vmApp<DataShareModel>()

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        nsdModel.serviceFoundOnceData.observe {
            L.i("...test...$it")
            it?.let { serviceInfo ->
                _adapter.render {
                    DslTextItem()() {
                        itemText = span {
                            append("发现服务:↓") {
                                foregroundColor = _color(R.color.colorAccent)
                            }
                            append(serviceInfo.toStr())
                            appendLine()

                            append("服务名:${serviceInfo.serviceName}")
                            append(" 服务类型:${serviceInfo.serviceType}")
                            append(" 端口:${serviceInfo.port}")
                            appendLine()
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                append("network:${serviceInfo.network}")
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                append("hostAddresses:${serviceInfo.hostAddresses}")
                            } else {
                                append("host:${serviceInfo.host}")
                            }
                            append(" describeContents:${serviceInfo.describeContents()}")
                            append(" attributes:${serviceInfo.attributes}")
                        }
                    }
                }
            }
        }

        nsdModel.discoveryInfoData.observe(allowBackward = false) { info ->
            info?.let {
                _adapter.render {
                    DslTextItem()() {
                        itemText = span {
                            if (it.start) {
                                append("正在发现服务[${info.serviceType}]...")
                            } else {
                                append("停止发现服务[${info.serviceType}]!")
                            }
                        }
                    }
                }
            }
        }

        /*shareModel.shareMessageOnceData.observe {
            it?.let {
                _adapter.render {
                    DslTextItem()() {
                        itemText = span {
                            *//*append("收到消息:↓") {
                                foregroundColor = _color(R.color.colorAccent)
                            }*//*
                            append(it)
                        }
                    }
                }
            }
        }*/

        renderDslAdapter {
            renderItem {
                itemLayoutId = R.layout.demo_nsd_item
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.hawkInstallAndRestore("nsd_")
                    itemHolder.click(R.id.register_service_button) {
                        nsdModel.registerService(NsdServiceInfo().apply {
                            serviceName = itemHolder.tv(R.id.service_name_view).string()
                            serviceType = itemHolder.tv(R.id.service_type_view).string()
                            port = Port.assignPort()
                        })
                    }
                    itemHolder.click(R.id.discover_service_button) {
                        nsdModel.startDiscovery(itemHolder.tv(R.id.discover_type_view).string())
                    }
                    itemHolder.click(R.id.unregister_service_button) {
                        nsdModel.unregisterService()
                    }
                    itemHolder.click(R.id.stop_discover_service_button) {
                        nsdModel.stopDiscovery()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        nsdModel.stopDiscovery()
        nsdModel.unregisterService()
    }

}