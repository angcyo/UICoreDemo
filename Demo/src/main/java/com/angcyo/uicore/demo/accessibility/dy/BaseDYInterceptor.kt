package com.angcyo.uicore.demo.accessibility.dy

import com.angcyo.core.component.accessibility.BaseAccessibilityInterceptor
import com.angcyo.core.component.accessibility.intervalMode
import com.angcyo.uicore.demo.accessibility.*

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/01
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
abstract class BaseDYInterceptor : BaseAccessibilityInterceptor() {
    init {
        filterPackageNameList.add(DYLikeInterceptor.DY_PACKAGE_NAME)

        actionOtherList.add(DYPrivacyAction())
        actionOtherList.add(DYPermissionsAction())
        actionOtherList.add(DYProtectAction())
        actionOtherList.add(DYGuideAction())
        actionOtherList.add(DYUpdateAction())
        actionOtherList.add(DYContactsAction())
        actionOtherList.add(DYShareAction())
        actionOtherList.add(DYBackAction())

        intervalMode()
    }
}