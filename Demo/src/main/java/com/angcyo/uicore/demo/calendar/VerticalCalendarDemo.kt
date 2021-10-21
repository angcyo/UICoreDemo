package com.angcyo.uicore.demo.calendar

import android.view.View
import com.angcyo.library.L
import com.angcyo.library.ex.str
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.uicore.demo.R
import com.haibin.calendarview.VerticalCalendarView
import com.haibin.calendarview.onCalendarSelectListener

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/10/21
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class VerticalCalendarDemo : AppTitleFragment() {

    init {
        contentLayoutId = R.layout.demo_vertical_calendar_layout
    }

    var doCount = 0

    override fun onInitFragment() {
        super.onInitFragment()
        val calendarView: VerticalCalendarView? = _vh.v(R.id.calendar_view)
        var year = 2021
        _vh.click(R.id.tv_month_day) {
            calendarView?.showYearSelectLayout(year)
            _vh.tv(R.id.tv_lunar)?.visibility = View.GONE
            _vh.tv(R.id.tv_year)?.visibility = View.GONE
            _vh.tv(R.id.tv_month_day)?.text = year.toString()
        }

        _vh.click(R.id.fl_current) { calendarView?.scrollToCurrent() }
        _vh.click(R.id.to_pre) { calendarView?.scrollToPre(true) }
        _vh.click(R.id.to_next) { calendarView?.scrollToNext(true) }
        _vh.click(R.id.switch_month) {
            if (doCount++ % 2 == 0) {
                calendarView?.setMonthView(CustomRangeMonthView::class.java)
            } else {
                calendarView?.setMonthView(CustomMultiMonthView::class.java)
            }
        }

        calendarView?.onCalendarSelectListener { calendar, isClick, isOutOfRange ->
            L.i("选中日期: ", calendar, " :", isClick, " :", isOutOfRange)
            _vh.tv(R.id.tv_lunar)?.visibility = View.VISIBLE
            _vh.tv(R.id.tv_year)?.visibility = View.VISIBLE
            _vh.tv(R.id.tv_month_day)?.text = calendar.month.toString() + "月" + calendar.day + "日"
            _vh.tv(R.id.tv_year)?.text = calendar.year.toString()
            _vh.tv(R.id.tv_lunar)?.text = calendar.lunar
            year = calendar.year

        }
        calendarView?.setOnYearChangeListener {
            L.i(it)
            _vh.tv(R.id.tv_month_day)?.text = it.toString()
        }
        _vh.tv(R.id.tv_year)?.text = calendarView?.curYear?.str()
        year = calendarView?.curYear ?: year
        _vh.tv(R.id.tv_month_day)?.text =
            calendarView?.curMonth.toString() + "月" + calendarView?.curDay + "日"

        _vh.tv(R.id.tv_lunar)?.text = "今日"
        _vh.tv(R.id.tv_current_day)?.text = calendarView?.curDay?.str()
    }

}