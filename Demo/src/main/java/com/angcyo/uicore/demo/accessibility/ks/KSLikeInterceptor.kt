package com.angcyo.uicore.demo.accessibility.ks

import com.angcyo.core.component.accessibility.AccessibilityHelper
import com.angcyo.core.component.accessibility.intervalMode
import com.angcyo.core.component.file.DslFileHelper
import com.angcyo.core.component.file.wrapData
import com.angcyo.core.vmCore

/**
 * 快手点赞拦截器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/02
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class KSLikeInterceptor : BaseKSInterceptor() {
    companion object {

        const val KS_PACKAGE_NAME = "com.smile.gifmaker"

        val ksUserName: CharSequence get() = vmCore<KSModel>().userNameData.value ?: "未登录"

        fun log(data: String) {
            DslFileHelper.write(
                AccessibilityHelper.logFolderName,
                "ks.log",
                "$ksUserName:$data".wrapData()
            )
        }
    }

    init {
        intervalMode()

        actionList.add(KSShareAction())
        actionList.add(KSLikeAction())
    }
}