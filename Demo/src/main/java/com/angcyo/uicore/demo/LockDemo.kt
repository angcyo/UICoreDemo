package com.angcyo.uicore.demo

import android.content.Intent
import android.os.Bundle
import com.angcyo.activity.lockNotify
import com.angcyo.base.dslAHelper
import com.angcyo.base.fullscreen
import com.angcyo.item.DslButtonItem
import com.angcyo.library.ex.nowTime
import com.angcyo.putData
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
        }
    }
}