package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.addToAfter
import com.angcyo.dsladapter.filter.FilterChain
import com.angcyo.dsladapter.filter.IFilterInterceptor
import com.angcyo.dsladapter.removeFromAfter
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppBluetoothDeviceItem
import com.angcyo.uicore.dslitem.AppFscDeviceItem
import com.angcyo.widget.progress.ArcLoadingView

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/21
 */
class FscBleApiDemo : AppDslFragment() {

    val fscModel = vmApp<FscBleApiModel>()

    val emptyDeviceFilter = EmptyDeviceFilter()

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
                        itemData = device
                        fscDevice = device
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

                    //start
                    itemHolder.click(R.id.start_scan_button) {
                        fscModel.useSppModel = itemHolder.isChecked(R.id.spp_check_box)
                        if (fscModel.bleStateData.value == FscBleApiModel.BLUETOOTH_STATE_SCANNING) {
                            fscModel.stopScan()
                        } else {
                            dataItems.clear()
                            fscModel.startScan(requireActivity())
                        }
                    }

                    //filter
                    itemHolder.check(
                        R.id.filter_switch,
                        emptyDeviceFilter.isEnable
                    ) { buttonView, isChecked ->
                        emptyDeviceFilter.isEnable = isChecked
                        if (isChecked) {
                            emptyDeviceFilter.addToAfter(_adapter)
                        } else {
                            emptyDeviceFilter.removeFromAfter(_adapter)
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

/**过滤空名字的设备*/
class EmptyDeviceFilter : IFilterInterceptor {

    override var isEnable: Boolean = false

    override fun intercept(chain: FilterChain): List<DslAdapterItem> {
        val requestList = chain.requestList
        val result = mutableListOf<DslAdapterItem>()
        requestList.forEach {
            if (it is AppFscDeviceItem) {
                if (!it.fscDevice?.name.isNullOrEmpty()) {
                    result.add(it)
                }
            } else if (it is AppBluetoothDeviceItem) {
                if (!it.bleDevice?.name.isNullOrEmpty()) {
                    result.add(it)
                }
            } else {
                result.add(it)
            }
        }
        return result
    }

}