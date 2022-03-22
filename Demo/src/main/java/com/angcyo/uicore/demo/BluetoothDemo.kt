package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.bluetooth.BluetoothModel
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.app
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppBluetoothDeviceItem
import com.angcyo.widget.progress.ArcLoadingView

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/12
 */
class BluetoothDemo : AppDslFragment() {

    val bluetoothModel = vmApp<BluetoothModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BluetoothModel.init(app())
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        bluetoothModel.bluetoothStateData.observe {
            it?.let {
                _adapter[0]?.updateAdapterItem()
            }
        }

        bluetoothModel.bluetoothDeviceData.observe { device ->
            device?.let {
                _adapter.render {

                    //移除旧的item
                    dataItems.removeAll {
                        it is AppBluetoothDeviceItem && it.bleDevice?.mac == device.mac
                    }

                    //添加新的item
                    AppBluetoothDeviceItem()() {
                        bleDevice = device
                        time = nowTimeString()
                    }
                }
            }
        }

        renderDslAdapter {
            DslAdapterItem()(headerItems) {
                itemLayoutId = R.layout.item_bluetooth_layout
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.tv(R.id.lib_text_view)?.text =
                        "蓝牙设备:${BluetoothModel.isSupportBle()}  蓝牙开关:${BluetoothModel.isBlueEnable()}"

                    if (bluetoothModel.bluetoothStateData.value == BluetoothModel.BLUETOOTH_STATE_SCANNING) {
                        itemHolder.v<ArcLoadingView>(R.id.arc_load_view)?.startLoading()
                    } else {
                        itemHolder.v<ArcLoadingView>(R.id.arc_load_view)?.endLoading()
                        itemHolder.v<ArcLoadingView>(R.id.arc_load_view)?.progress = 0
                    }

                    itemHolder.tv(R.id.start_scan_button)?.text =
                        if (bluetoothModel.bluetoothStateData.value == BluetoothModel.BLUETOOTH_STATE_SCANNING) "停止扫描" else "开始扫描"

                    itemHolder.click(R.id.start_scan_button) {
                        if (bluetoothModel.bluetoothStateData.value == BluetoothModel.BLUETOOTH_STATE_SCANNING) {
                            bluetoothModel.stopScan()
                        } else {
                            dataItems.clear()
                            bluetoothModel.startScan(requireActivity())
                        }
                    }
                }
            }
        }
    }

    override fun onFragmentNotFirstShow(bundle: Bundle?) {
        super.onFragmentNotFirstShow(bundle)
        _adapter[0]?.updateAdapterItem()
    }

}