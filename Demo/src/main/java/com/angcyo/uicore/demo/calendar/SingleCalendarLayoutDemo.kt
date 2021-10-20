package com.angcyo.uicore.demo.calendar

import android.os.Bundle
import androidx.core.graphics.drawable.toDrawable
import com.angcyo.library.L
import com.angcyo.library.ex.toColor
import com.angcyo.uicore.demo.R
import com.haibin.calendarview.*

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/20
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class SingleCalendarLayoutDemo : CalendarLayoutDemo() {

    init {
        contentLayoutId = R.layout.demo_single_calendar_layout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentConfig.titleBarBackgroundDrawable = "#334555".toColor().toDrawable()
        //fragmentConfig.titleItemIconColor = Color.WHITE
        //fragmentConfig.titleItemTextColor = Color.WHITE
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        val calendarView: CalendarView? = _vh.v(R.id.calendar_view)
        //calendarView?.mothv

        calendarView?.apply {
            //_updateCurrentMonth()
            _vh.click(R.id.pre_month_view) {
                toPreMonth()
            }
            _vh.click(R.id.next_month_view) {
                toNextMonth()
            }
            _vh.click(R.id.current_month_view) {
                toYearSelect(selectedCalendar.year)
            }

            onCalendarSelectListener { calendar, isClick, isOutOfRange ->
                L.i("选中日期: ", calendar, " :", isClick, " :", isOutOfRange)
                //_updateCurrentMonth()
            }
            onWeekChangeListener {
                L.i(it)
                //_updateCurrentMonth()
            }
            onMonthChangeListener { year, month ->
                _vh.tv(R.id.current_month_view)?.text = "${year}年${month}月"
            }
            onCalendarInterceptListener { calendar, isClick, isIntercept ->
                L.i("拦截日期: ", calendar, " :", isClick, " :", isIntercept)
                false
            }

            toToday()
            _updateCurrentMonth()
        }
    }

    fun _updateCurrentMonth() {
        val calendarView: CalendarView? = _vh.v(R.id.calendar_view)
        val calendar = calendarView?.selectedCalendar
        _vh.tv(R.id.current_month_view)?.text = "${calendar?.year}年${calendar?.month}月"
    }
}