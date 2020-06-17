package com.angcyo.uicore.demo

import android.content.Intent
import android.os.Bundle
import com.angcyo.activity.lockNotify
import com.angcyo.base.dslAHelper
import com.angcyo.base.fullscreen
import com.angcyo.dsladapter.findItemByTag
import com.angcyo.item.DslButtonItem
import com.angcyo.item.DslLabelEditItem
import com.angcyo.item.itemEditText
import com.angcyo.library.ex.nowTime
import com.angcyo.putData
import com.angcyo.speech.TTS
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.tx
import com.angcyo.uicore.test.TestActivity
import com.angcyo.widget.base.postDelay

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

            DslLabelEditItem()() {
                itemTag = "TTS"
                itemLabelText = "TTS文本"
                configEditTextStyle {
                    hint = "请输入要转换成语音的文本"
                }
                itemEditText = "您有一个新的任务."
            }

            DslButtonItem()() {
                itemButtonText = "TTS(文本转语音)"

                itemClick = {
                    TTS.startSpeaking(findItemByTag("TTS")?.itemEditText()?.toString())
                }
            }
        }
    }
}