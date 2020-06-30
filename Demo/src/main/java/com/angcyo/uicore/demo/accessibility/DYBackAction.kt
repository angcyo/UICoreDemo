package com.angcyo.uicore.demo.accessibility

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.core.vmCore
import com.angcyo.library.ex.isListEmpty

/**
 * 抖音回退[Action], 执行[back]操作
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/30
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DYBackAction : BaseAccessibilityAction() {
    override fun doActionWidth(
        action: BaseAccessibilityAction,
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {
        val back = !service.rootNodeInfo(event)?.findNode {
            if (it.haveText("请输入手机号") ||
                it.haveText("请输入验证码") ||
                it.haveText("一键登录")
            ) {
                vmCore<DYModel>().loginData.postValue(false)
                0
            } else if (it.haveText("通讯录好友")) {
                vmCore<DYModel>().loginData.postValue(true)
                //跳过通讯录好友界面
                0
            } else {
                -1
            }
        }.isListEmpty()

        if (back) {
            service.back()
        }

        return back
    }
}