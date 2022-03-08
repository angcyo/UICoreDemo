package com.angcyo.uicore.demo.calendar

import android.os.Bundle
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.uicore.base.BaseDemoDslFragment
import com.angcyo.uicore.demo.R
import com.angcyo.widget.layout.RCoordinatorLayout
import com.angcyo.widget.layout.isEnableCoordinator

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/20
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
open class CalendarLayoutDemo : BaseDemoDslFragment() {

    init {
        contentLayoutId = R.layout.demo_calendar_layout
    }

    override fun onCreateBehavior(child: View): CoordinatorLayout.Behavior<*>? {
        return super.onCreateBehavior(child)
    }

    override fun onInitFragment(savedInstanceState: Bundle?) {
        super.onInitFragment(savedInstanceState)

        _vh.v<RCoordinatorLayout>(R.id.lib_coordinator_wrap_layout)?.isEnableCoordinator = false

        renderDslAdapter {
            for (i in 0..40) {
                renderDemoListItem("TestItem${i}...")
            }
        }
    }
}