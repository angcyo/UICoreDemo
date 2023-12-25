package com.angcyo.uicore.demo

import android.net.nsd.NsdServiceInfo
import android.os.Bundle
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.core.component.model.NsdModel
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.renderItem
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemText
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

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        nsdModel.serviceFoundOnceData.observe {
            it?.let { serviceInfo ->
                _adapter.render {
                    DslTextItem()() {
                        itemText = span {
                            append("发现服务:↓") {
                                foregroundColor = _color(R.color.colorAccent)
                            }
                            append(serviceInfo.toStr())
                        }
                    }
                }
            }
        }

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