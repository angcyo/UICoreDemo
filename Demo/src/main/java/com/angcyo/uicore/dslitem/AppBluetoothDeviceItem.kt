package com.angcyo.uicore.dslitem

import com.angcyo.bluetooth.BluetoothModel
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.clj.fastble.data.BleDevice


/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/12
 */
class AppBluetoothDeviceItem : DslAdapterItem() {

    var bleDevice: BleDevice? = null

    var time: CharSequence? = null

    init {
        itemLayoutId = R.layout.item_bluetooth_device_layout
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        itemHolder.tv(R.id.lib_text_view)?.text = buildString {
            bleDevice?.let {
                if (it.name.isNullOrEmpty()) {
                    append(it.mac)
                } else {
                    appendLine(it.name)
                    append(it.mac)
                }
            }
        }

        itemHolder.tv(R.id.lib_des_view)?.text = buildString {
            bleDevice?.let {
                appendLine(time)
                append(it.rssi)
                append(" ${it.device.type}")
                append(" ${it.device.alias}")
                append(" ${it.device.bluetoothClass.deviceClass}/${it.device.bluetoothClass.majorDeviceClass}")
                //HexUtil.formatHexString(it.scanRecord)
            }
        }

        //https://www.jianshu.com/p/12b69fe68246
        bleDevice?.let {
            itemHolder.img(R.id.lib_image_view)
                ?.setImageResource(BtUtil.getDeviceTypeResource(it.device.bluetoothClass))
        }

        itemHolder.tv(R.id.connect_button)?.text = if (BluetoothModel.isConnected(bleDevice)) {
            "断开"
        } else {
            "连接"
        }

        itemHolder.click(R.id.connect_button) {
            if (BluetoothModel.isConnected(bleDevice)) {
                //断开
                
            } else {
                //连接
            }
        }
    }

}