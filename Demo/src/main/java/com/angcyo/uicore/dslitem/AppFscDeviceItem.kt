package com.angcyo.uicore.dslitem

import androidx.fragment.app.Fragment
import com.angcyo.base.dslFHelper
import com.angcyo.bluetooth.DeviceConnectState
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.core.dslitem.IFragmentItem
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.putData
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.demo.ble.FscThroughputFragment
import com.angcyo.widget.DslViewHolder
import com.feasycom.common.bean.FscDevice

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/21
 */
class AppFscDeviceItem : DslAdapterItem(), IFragmentItem {

    var fscDevice: FscDevice? = null

    var time: CharSequence? = null

    val fscBleApiModel = vmApp<FscBleApiModel>()

    override var itemFragment: Fragment? = null

    init {
        itemLayoutId = R.layout.item_bluetooth_device_layout

        thisAreItemsTheSame

        fscBleApiModel.connectStateData.observe(this) {
            if (it != null) {
                if (it.device == fscDevice) {
                    updateAdapterItem()
                }
            }
        }

        itemClick = {
            itemFragment?.dslFHelper {
                show(FscThroughputFragment::class) {
                    putData(fscDevice)
                }
            }
        }
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.lib_text_view)?.text = buildString {
            fscDevice?.let {
                if (it.name.isNullOrEmpty()) {
                    append(it.address)
                } else {
                    appendLine(it.name)
                    append(it.address)
                }
            }
        }

        itemHolder.tv(R.id.lib_des_view)?.text = buildString {
            fscDevice?.let {
                appendLine(time)
                append(it.rssi)
                append(" ${it.mode}")
                append(" ${it.device.type}")
                append(" ${it.device.alias}")
                append(" ${it.device.bluetoothClass.deviceClass}/${it.device.bluetoothClass.majorDeviceClass}")
                //HexUtil.formatHexString(it.scanRecord)
            }
        }

        //https://www.jianshu.com/p/12b69fe68246
        fscDevice?.let {
            itemHolder.img(R.id.lib_image_view)
                ?.setImageResource(BtUtil.getDeviceTypeResource(it.device.bluetoothClass))
        }

        itemHolder.tv(R.id.connect_button)?.text = when (fscBleApiModel.connectState(fscDevice)) {
            DeviceConnectState.CONNECT_STATE_START -> "..."
            DeviceConnectState.CONNECT_STATE_SUCCESS -> "断开"
            else -> "连接"
        }

        itemHolder.click(R.id.connect_button) {
            fscDevice?.let { device ->
                if (fscBleApiModel.isConnected(device)) {
                    //断开
                    fscBleApiModel.disconnect(device)
                } else {
                    //连接
                    fscBleApiModel.connect(device)
                }
            }
        }
    }
}