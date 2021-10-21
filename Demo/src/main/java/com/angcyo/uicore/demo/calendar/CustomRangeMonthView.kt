package com.angcyo.uicore.demo.calendar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import com.haibin.calendarview.Calendar
import com.haibin.calendarview.RangeMonthView
import kotlin.math.min

/**
 * 范围选择月视图
 * Created by huanghaibin on 2018/9/13.
 */
class CustomRangeMonthView(context: Context?) : RangeMonthView(context) {

    private var mRadius = 0

    override fun onPreviewHook() {
        mRadius = min(mItemWidth, mItemHeight) / 5 * 2
        mSchemePaint.style = Paint.Style.STROKE
    }

    override fun onDrawSelected(
        canvas: Canvas, calendar: Calendar,
        x: Int, y: Int, hasScheme: Boolean,
        isSelectedPre: Boolean, isSelectedNext: Boolean
    ): Boolean {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        if (isSelectedPre) {
            if (isSelectedNext) {
                canvas.drawRect(
                    x.toFloat(),
                    (cy - mRadius).toFloat(),
                    (x + mItemWidth).toFloat(),
                    (cy + mRadius).toFloat(),
                    mSelectedPaint
                )
            } else { //最后一个，the last
                canvas.drawRect(
                    x.toFloat(),
                    (cy - mRadius).toFloat(),
                    cx.toFloat(),
                    (cy + mRadius).toFloat(),
                    mSelectedPaint
                )
                canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSelectedPaint)
            }
        } else {
            if (isSelectedNext) {
                canvas.drawRect(
                    cx.toFloat(),
                    (cy - mRadius).toFloat(),
                    (x + mItemWidth).toFloat(),
                    (cy + mRadius).toFloat(),
                    mSelectedPaint
                )
            }
            canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSelectedPaint)
            //
        }
        return false
    }

    override fun onDrawScheme(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        isSelected: Boolean
    ) {
        val cx = x + mItemWidth / 2
        val cy = y + mItemHeight / 2
        canvas.drawCircle(cx.toFloat(), cy.toFloat(), mRadius.toFloat(), mSchemePaint)
    }

    override fun onDrawText(
        canvas: Canvas,
        calendar: Calendar,
        x: Int,
        y: Int,
        hasScheme: Boolean,
        isSelected: Boolean
    ) {
        val baselineY = mTextBaseLine + y
        val cx = x + mItemWidth / 2
        val isInRange = isInRange(calendar)
        val isEnable = !onCalendarIntercept(calendar)
        if (isSelected) {
            canvas.drawText(
                calendar.day.toString(),
                cx.toFloat(),
                baselineY,
                mSelectTextPaint
            )
        } else if (hasScheme) {
            canvas.drawText(
                calendar.day.toString(),
                cx.toFloat(),
                baselineY,
                if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth && isInRange && isEnable) mSchemeTextPaint else mOtherMonthTextPaint
            )
        } else {
            canvas.drawText(
                calendar.day.toString(), cx.toFloat(), baselineY,
                if (calendar.isCurrentDay) mCurDayTextPaint else if (calendar.isCurrentMonth && isInRange && isEnable) mCurMonthTextPaint else mOtherMonthTextPaint
            )
        }
    }
}