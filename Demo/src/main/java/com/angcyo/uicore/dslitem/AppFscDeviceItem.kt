package com.angcyo.uicore.dslitem

import androidx.fragment.app.Fragment
import com.angcyo.base.dslFHelper
import com.angcyo.bluetooth.DeviceConnectState
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.item.IFragmentItem
import com.angcyo.library.ex.readAssetsBytes
import com.angcyo.library.toastQQ
import com.angcyo.putParcelable
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
                    putParcelable(fscDevice)
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

        //connect_at_button AT指令
        itemHolder.click(R.id.connect_at_button) {
            fscDevice?.let { device ->
                if (fscBleApiModel.isConnected(device)) {
                    //断开
                    fscBleApiModel.disconnect(device)
                } else {
                    //连接
                    fscBleApiModel.connect(device, connectToModify = true)
                }
            }
        }

        //connect_factory_button 蓝牙模块固件升级
        itemHolder.click(R.id.connect_factory_button) {
            fscDevice?.let { device ->
                if (fscBleApiModel.isConnected(device)) {
                    //断开
                    fscBleApiModel.disconnect(device)
                    fscDevice = null
                } else {
                    //模块升级
                    //val byteArray = it.context.readAssetsBytes("fsc/HAIXING_BT986916.dfu")
                    val byteArray = it.context.readAssetsBytes("fsc/BT986916.dfu")
                    fscBleApiModel.connectToOTAWithFactory(
                        device.address,
                        byteArray!!
                    ) {
                        toastQQ("更新进度:$it")
                    }

                    /*val url = "https://dfu.feasycom.com/HAIXING%2FBT986916.dfu"
                    url.download() { task: DownloadTask, error: Throwable? ->
                        if (task.isFinish) {
                            doMain {
                                val byteArray = task.savePath.file().readBytes()
                                fscBleApiModel.connectToOTAWithFactory(device.address, byteArray) {
                                    toastQQ("更新进度:$it")
                                }
                            }
                        }
                    }*/
                }
            }
        }
    }
}