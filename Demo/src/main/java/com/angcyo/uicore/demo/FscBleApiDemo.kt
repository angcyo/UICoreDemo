package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppFscDeviceItem
import com.angcyo.widget.progress.ArcLoadingView

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/21
 */
class FscBleApiDemo : AppDslFragment() {

    val fscModel = vmApp<FscBleApiModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        fscModel.bleStateData.observe {
            it?.let {
                _adapter[0]?.updateAdapterItem()
            }
        }

        fscModel.bleDeviceData.observe { device ->
            device?.let {
                _adapter.render {

                    //移除旧的item
                    dataItems.removeAll {
                        it is AppFscDeviceItem && it.fscDevice == device
                    }

                    //添加新的item
                    AppFscDeviceItem()() {
                        fscDevice = device
                        time = nowTimeString()
                    }
                }
            }
        }

        renderDslAdapter {
            headerItems.add(DslAdapterItem().apply {
                itemLayoutId = R.layout.item_bluetooth_layout
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.tv(R.id.lib_text_view)?.text =
                        "蓝牙设备:${FscBleApiModel.isSupportBle()}  蓝牙开关:${FscBleApiModel.isBlueEnable()}"

                    itemHolder.visible(R.id.spp_check_box)

                    if (fscModel.bleStateData.value == FscBleApiModel.BLUETOOTH_STATE_SCANNING) {
                        itemHolder.v<ArcLoadingView>(R.id.arc_load_view)?.startLoading()
                    } else {
                        itemHolder.v<ArcLoadingView>(R.id.arc_load_view)?.endLoading()
                        itemHolder.v<ArcLoadingView>(R.id.arc_load_view)?.progress = 0
                    }

                    itemHolder.tv(R.id.start_scan_button)?.text =
                        if (fscModel.bleStateData.value == FscBleApiModel.BLUETOOTH_STATE_SCANNING) "停止扫描" else "开始扫描"

                    itemHolder.click(R.id.start_scan_button) {
                        fscModel.useSppScan = itemHolder.isChecked(R.id.spp_check_box)
                        if (fscModel.bleStateData.value == FscBleApiModel.BLUETOOTH_STATE_SCANNING) {
                            fscModel.stopScan()
                        } else {
                            fscModel.startScan(requireActivity())
                        }
                    }
                }
            })
        }
    }

    override fun onFragmentNotFirstShow(bundle: Bundle?) {
        super.onFragmentNotFirstShow(bundle)
        _adapter[0]?.updateAdapterItem()
    }

}