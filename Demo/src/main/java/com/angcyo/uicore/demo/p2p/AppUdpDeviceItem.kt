package com.angcyo.uicore.demo.p2p

import com.angcyo.device.bean.DeviceBean
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/12/02
 */
class AppUdpDeviceItem : DslAdapterItem() {

    /**数据*/
    var itemDeviceBean: DeviceBean? = null

    init {
        itemLayoutId = R.layout.app_udp_device_item
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.lib_text_view)?.text = itemDeviceBean?.name
        itemHolder.tv(R.id.lib_des_view)?.text = span {
            itemDeviceBean?.apply {
                deviceId?.let {
                    append("id:${it}")
                }
                appendln()
                append("${address}:$port")
                appendln()
                token?.let {
                    append("token:$it")
                }
            }
        }
    }

}