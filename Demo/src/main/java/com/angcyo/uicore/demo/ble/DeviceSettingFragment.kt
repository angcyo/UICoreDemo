package com.angcyo.uicore.demo.ble

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.command.sendCommand
import com.angcyo.bluetooth.fsc.laserpacker.parse.QuerySettingParser
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.drawBottom
import com.angcyo.item.DslPropertySwitchItem
import com.angcyo.item.style.itemDes
import com.angcyo.item.style.itemLabel
import com.angcyo.item.style.itemSwitchChangedAction
import com.angcyo.item.style.itemSwitchChecked
import com.angcyo.library.ex._string
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.R

/**
 * 设备设置界面
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/06/08
 */
class DeviceSettingFragment : AppDslFragment() {

    init {
        fragmentTitle = _string(R.string.ui_slip_menu_model)
    }

    override fun onInitFragment(savedInstanceState: Bundle?) {
        fragmentConfig.fragmentBackgroundDrawable = ColorDrawable(Color.WHITE)
        super.onInitFragment(savedInstanceState)
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        val settingParser = vmApp<LaserPeckerModel>().deviceSettingStateData.value
        settingParser?.functionSetting()

        renderDslAdapter {
            DslPropertySwitchItem()() {
                itemLabel = _string(R.string.device_setting_act_model_warning_tone)
                itemDes = _string(R.string.device_setting_act_des_sound)
                initItem()

                itemSwitchChecked = settingParser?.buzzer == 1
                itemSwitchChangedAction = {
                    settingParser?.buzzer = if (it) 1 else 0
                    settingParser?.sendCommand()
                }
            }
            DslPropertySwitchItem()() {
                itemLabel = _string(R.string.device_setting_act_model_security)
                itemDes = _string(R.string.device_setting_act_des_security)
                initItem()

                itemSwitchChecked = settingParser?.safe == 1
                itemSwitchChangedAction = {
                    settingParser?.safe = if (it) 1 else 0
                    settingParser?.sendCommand()
                }
            }
            DslPropertySwitchItem()() {
                itemLabel = _string(R.string.device_setting_act_model_free)
                itemDes = _string(R.string.device_setting_act_des_free)
                initItem()

                itemSwitchChecked = settingParser?.free == 1
                itemSwitchChangedAction = {
                    settingParser?.free = if (it) 1 else 0
                    settingParser?.sendCommand()
                }
            }
            DslPropertySwitchItem()() {
                itemLabel = _string(R.string.device_setting_act_model_preview_g_code)
                itemDes = _string(R.string.device_setting_act_des_preview_g_code)
                initItem()

                itemSwitchChecked = settingParser?.gcodeView == 1
                itemSwitchChangedAction = {
                    settingParser?.gcodeView = if (it) 1 else 0
                    settingParser?.sendCommand()
                }
            }
            DslPropertySwitchItem()() {
                itemLabel = _string(R.string.device_setting_tips_fourteen_2)
                itemDes = _string(R.string.device_setting_tips_fourteen_3)
                initItem()

                itemSwitchChecked = settingParser?.zFlag == 1
                itemSwitchChangedAction = {
                    settingParser?.zFlag = if (it) 1 else 0
                    settingParser?.sendCommand()
                }
            }
            DslPropertySwitchItem()() {
                itemLabel = _string(R.string.device_setting_txt_3)
                itemDes = _string(R.string.device_setting_txt_4)
                initItem()

                itemSwitchChecked = settingParser?.keyView == 1
                itemSwitchChangedAction = {
                    settingParser?.keyView = if (it) 1 else 0
                    settingParser?.sendCommand()
                }
            }
            DslPropertySwitchItem()() {
                itemLabel = _string(R.string.device_setting_txt_5)
                itemDes = _string(R.string.device_setting_txt_6)
                initItem()

                itemSwitchChecked = settingParser?.keyPrint == 1
                itemSwitchChangedAction = {
                    settingParser?.keyPrint = if (it) 1 else 0
                    settingParser?.sendCommand()
                }
            }
            DslPropertySwitchItem()() {
                itemLabel = _string(R.string.button_infra_red_title)
                itemDes = _string(R.string.button_infra_red_content)
                initItem()

                itemSwitchChecked = settingParser?.irDst == 1
                itemSwitchChangedAction = {
                    settingParser?.irDst = if (it) 1 else 0
                    settingParser?.sendCommand()
                }
            }
            DslPropertySwitchItem()() {
                itemLabel = _string(R.string.device_setting_blue_connect_auto)
                initItem()

                itemSwitchChecked = QuerySettingParser.AUTO_CONNECT_DEVICE
                itemSwitchChangedAction = {
                    QuerySettingParser.AUTO_CONNECT_DEVICE = it
                }
            }
        }
    }

    fun DslAdapterItem.initItem() {
        drawBottom()
    }

}