package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.loadTextItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/29
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class TitleBarBehaviorDemo : AppDslFragment() {
    init {
        fragmentLayoutId = R.layout.demo_title_bar_behavior
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            loadTextItem()
        }
    }
}