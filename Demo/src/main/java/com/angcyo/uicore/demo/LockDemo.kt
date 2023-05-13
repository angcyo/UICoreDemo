package com.angcyo.uicore.demo

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.angcyo.activity.lockNotify
import com.angcyo.base.dslAHelper
import com.angcyo.base.fullscreen
import com.angcyo.core.component.model.BatteryModel
import com.angcyo.core.component.model.isCharging
import com.angcyo.core.component.model.toBatteryHealthStr
import com.angcyo.core.component.model.toBatteryPluggedStr
import com.angcyo.core.component.model.toBatteryStatusStr
import com.angcyo.core.vmCore
import com.angcyo.dialog2.dslitem.DslLabelWheelItem
import com.angcyo.dialog2.dslitem.itemWheelBean
import com.angcyo.dsladapter.findItemByTag
import com.angcyo.item.DslButtonItem
import com.angcyo.item.DslLabelEditItem
import com.angcyo.item.DslLabelTextItem
import com.angcyo.item.DslTextItem
import com.angcyo.item.itemEditText
import com.angcyo.item.style.itemButtonText
import com.angcyo.item.style.itemEditText
import com.angcyo.item.style.itemLabelText
import com.angcyo.item.style.itemText
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.nowTime
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.postDelay
import com.angcyo.library.utils.RUtils
import com.angcyo.putData
import com.angcyo.speech.TTS
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.tx
import com.angcyo.uicore.test.TestActivity
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/17
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class LockDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            DslButtonItem()() {
                itemButtonText = "启动锁屏通知"
                itemClick = {
                    dslAHelper {
                        lockNotify {
                            notifyTitle = "锁屏通知测试"
                            notifyContent = tx()

                            notifyActivityIntent = Intent(
                                fContext(),
                                TestActivity::class.java
                            ).apply { putData("data:${nowTime()}") }
                        }
                    }
                }
            }

            DslButtonItem()() {
                itemButtonText = "启动锁屏通知(延迟)"
                itemClick = {
                    it.postDelay(2_000) {
                        dslAHelper {
                            lockNotify {
                                notifyTitle = "锁屏通知测试"
                                notifyContent = tx()

                                notifyActivityIntent = Intent(
                                    fContext(),
                                    TestActivity::class.java
                                ).apply { putData("data:${nowTime()}") }
                            }
                        }
                    }
                }
            }

            DslButtonItem()() {
                itemButtonText = "全屏测试"

                itemClick = {
                    if (itemIsSelected) {
                        it.fullscreen(false)
                    } else {
                        it.fullscreen(true)
                    }
                    itemIsSelected = !itemIsSelected
                }
            }

            DslLabelWheelItem()() {
                itemSelectedIndex = 0
                itemLabelText = "选择音色"
                itemWheelList = mutableListOf(
                    "0-云小宁，亲和女声(默认)",
                    "1-云小奇，亲和男声",
                    "2-云小晚，成熟男声",
                    "4-云小叶，温暖女声",
                    "5-云小欣，情感女声",
                    "6-云小龙，情感男声",
                    "7-云小曼，客服女声",
                    "1000-智侠，情感男声",
                    "1001-智瑜，情感女声",
                    "1002-智聆，通用女声",
                    "1003-智美，客服女声",
                    "1050-WeJack，英文男声",
                    "1051-WeRose，英文女声"
                )
                itemChangeListener = {
                    it.onItemChangeListener(it)
                    it.itemWheelBean<String>()?.let {
                        TTS.configParams {
                            setVoiceType(it.split("-").getOrNull(0)?.toIntOrNull() ?: 0)
                        }
                    }
                }
            }

            DslLabelEditItem()() {
                itemTag = "TTS"
                itemLabelText = "TTS文本"
                configEditTextStyle {
                    hint = "请输入要转换成语音的文本"
                }
                itemEditText = "您有一个新的任务,请注意查收!"
            }

            DslButtonItem()() {
                itemButtonText = "TTS(文本转语音)"

                itemClick = {
                    TTS.startSpeaking(findItemByTag("TTS")?.itemEditText()?.toString())
                }
            }

            DslLabelEditItem()() {
                itemLabelText = "Label"
                itemEditText = "Edit"
                itemEditTipIcon = -1
                itemRightText = span {
                    append("m")
                    append("2") {
                        isSuperscript = true
                        fontSize = 9 * dpi
                    }
                }
            }

            DslLabelTextItem()() {
                itemLabelText = "Label"
                itemText = "Edit"
                itemRightText = span {
                    append("m")
                    text("2") {
                        isSuperscript = true
                        textSize = 9 * dp
                    }
                }
            }

            DslTextItem()() {
                itemTag = "battery"
                itemText = vmCore<BatteryModel>().batteryData.value?.toString() ?: "电量提示"
            }
        }
    }

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)
        vmCore<BatteryModel>().load(fContext())
    }

    override fun onDestroy() {
        super.onDestroy()
        vmCore<BatteryModel>().remove()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        vmCore<BatteryModel>().apply {
            batteryData.observe {
                it?.let {
                    _adapter.findItemByTag("battery")?.apply {
                        if (this is DslTextItem) {
                            itemText = buildString {
                                appendln(it)
                                appendln(it.health.toBatteryHealthStr())
                                appendln(it.plugged.toBatteryPluggedStr())
                                appendln(it.status.toBatteryStatusStr())
                                appendln(nowTimeString())
                                if (it.isCharging()) {
                                    appendln("正在充电...")
                                }
                                appendln("移动信号:${RUtils.getMobileDbm(fContext())}")
                                appendln("WIFI信号:${RUtils.getWifiRssi(fContext())}")
                            }
                            updateAdapterItem()
                        }
                    }
                }
            }

            observe(fContext())
        }
    }
}