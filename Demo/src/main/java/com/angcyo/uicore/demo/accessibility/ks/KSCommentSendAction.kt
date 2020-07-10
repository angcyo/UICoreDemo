package com.angcyo.uicore.demo.accessibility.ks

import android.view.accessibility.AccessibilityEvent
import com.angcyo.core.component.accessibility.*
import com.angcyo.uicore.dslitem.tx

/**
 * 快手发送评论
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class KSCommentSendAction : BaseAccessibilityAction() {

    override fun checkEvent(
        service: BaseAccessibilityService,
        event: AccessibilityEvent?
    ): Boolean {

        var result = false

        service.findNode {
            if (it.isEditText() && it.haveText("说点什么")) {
                result = true
            }
        }

        return result
    }

    override fun doAction(service: BaseAccessibilityService, event: AccessibilityEvent?) {
        super.doAction(service, event)

        service.findNode {
            if (it.isEditText() && it.haveText("说点什么")) {

                val comment = tx()

                val result = it.setNodeText(comment)
                KSLikeInterceptor.log("快手评论页, 输入评论[$comment] :$result")

                if (result) {
                    service.findNode {
                        if (it.isButton() && it.haveText("发送")) {
                            val send = it.click()
                            KSLikeInterceptor.log("快手评论页, 点击评论 :$send")

                            if (send) {
                                doActionFinish()
                            }
                        }
                    }
                }
            }
        }

    }
}