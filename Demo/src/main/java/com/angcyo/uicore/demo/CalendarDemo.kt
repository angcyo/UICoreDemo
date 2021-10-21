package com.angcyo.uicore.demo

import com.angcyo.uicore.base.BaseDemoDslFragment

/**
 * 日历相关demo
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/20
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class CalendarDemo : BaseDemoDslFragment() {

    init {
        baseClassPackage = "com.angcyo.uicore.demo.calendar"
    }

    override fun onInitFragment() {
        super.onInitFragment()
        renderDslAdapter {
            renderDemoListItem("CalendarLayoutDemo")
            renderDemoListItem("SingleCalendarLayoutDemo")
            renderDemoListItem("VerticalCalendarDemo")
        }
    }
}