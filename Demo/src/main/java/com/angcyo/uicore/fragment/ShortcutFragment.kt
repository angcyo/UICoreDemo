package com.angcyo.uicore.fragment

import android.os.Bundle
import android.view.View
import com.angcyo.item.DslTextInfoItem
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/26
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class ShortcutFragment : AppDslFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderDslAdapter {
            DslTextInfoItem()() {
                itemInfoText = "快捷方式测试\n$arguments"
                itemDarkText = nowTimeString()
            }
        }
    }
}