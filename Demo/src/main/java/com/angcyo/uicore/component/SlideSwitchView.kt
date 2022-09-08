package com.angcyo.uicore.component

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Checkable
import android.widget.OverScroller
import kotlin.math.max
import kotlin.math.min

/**
 * 滑动切换开关按钮
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/09/07
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class SlideSwitchView(context: Context, attrs: AttributeSet? = null) : View(context, attrs),
    Checkable {

    val dp = context.resources.displayMetrics.density

    var leftText: String? = "推荐档位"
    var rightText: String? = "阻力系数"

    val leftPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 18 * dp
    }

    val rightPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 18 * dp
    }

    /**中间的浮子*/
    var thumbDrawable: Drawable? = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(Color.RED)
        val radius = 45f
        cornerRadii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
    }

    var thumbMargin: Int = 2 * dp.toInt()

    /**状态*/
    var _checked = false

    val leftTextWidth: Float
        get() = leftPaint.textWidth(leftText)

    val rightTextWidth: Float
        get() = rightPaint.textWidth(rightText)

    val thumbSize: Int
        get() = thumbDrawable?.run {
            measuredHeight -
                    paddingTop - paddingBottom -
                    thumbMargin - thumbMargin
        } ?: 0

    val centerY: Int
        get() = paddingTop + (measuredHeight - paddingTop - paddingBottom) / 2

    val _thumbRect = Rect()

    val thumbRect: Rect
        get() {
            val right = measuredWidth - paddingRight - thumbMargin
            _thumbRect.set(
                right - thumbSize,
                centerY - thumbSize / 2,
                right,
                centerY + thumbSize / 2
            )
            return _thumbRect
        }

    val overScroller = OverScroller(context)

    var _lastVelocityX: Float = 0f

    var _isScroll = false

    //手势检测
    val _gestureDetector: GestureDetector by lazy {
        GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {

            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                _lastVelocityX = velocityX
                if (velocityX > 0) {
                    setChecked(false, true, true)
                } else {
                    setChecked(true, true, true)
                }
                return true
            }

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                _isScroll = distanceX != 0f
                scrollBy(distanceX.toInt(), 0)
                return true
            }
        })
    }

    val minScrollX = 0
    val maxScrollX: Int
        get() = max(leftTextWidth, rightTextWidth).toInt()

    override fun scrollTo(x: Int, y: Int) {
        super.scrollTo(clamp(x, minScrollX, maxScrollX), y)
        invalidate()
    }

    override fun computeScroll() {
        super.computeScroll()
        if (overScroller.computeScrollOffset()) {
            scrollTo(overScroller.currX, overScroller.currY)
        }
    }

    override fun setChecked(checked: Boolean) {
        setChecked(checked, false, false)
    }

    fun setChecked(checked: Boolean, anim: Boolean, fromUser: Boolean) {
        if (_checked != checked) {
            overScroller.abortAnimation()
            _checked = checked
            checkedChangeListener?.onCheckedChanged(this, checked, fromUser)
            refreshDrawableState()
        }
        if (checked) {
            if (anim) {
                overScroller.startScroll(scrollX, 0, maxScrollX - scrollX, 0)
                postInvalidate()
            } else {
                scrollTo(maxScrollX, 0)
            }
        } else {
            if (anim) {
                overScroller.startScroll(scrollX, 0, -scrollX, 0)
                postInvalidate()
            } else {
                scrollTo(minScrollX, 0)
            }
        }
    }

    override fun isChecked(): Boolean = _checked

    override fun toggle() {
        isChecked = !_checked
    }

    override fun performClick(): Boolean {
        if (_lastVelocityX == 0f) {
            setChecked(!_checked, true, true)
            return super.performClick()
        } else {
            return false
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        interceptParentTouchEvent(event)
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            _lastVelocityX = 0f
            _isScroll = false
        }
        _gestureDetector.onTouchEvent(event)
        if ((event.actionMasked == MotionEvent.ACTION_CANCEL ||
                    event.actionMasked == MotionEvent.ACTION_UP) &&
            _lastVelocityX == 0f && _isScroll
        ) {
            if (scrollX > maxScrollX / 2) {
                setChecked(false, true, true)
            } else {
                setChecked(true, true, true)
            }
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthSpec = widthMeasureSpec
        var heightSpec = heightMeasureSpec

        if (MeasureSpec.getMode(heightSpec) != MeasureSpec.EXACTLY) {
            //wrap_content
            heightSpec = MeasureSpec.makeMeasureSpec(
                (30 * dp + paddingTop + paddingBottom).toInt(),
                MeasureSpec.EXACTLY
            )
        }

        val heightSize = MeasureSpec.getSize(heightSpec)

        if (MeasureSpec.getMode(widthSpec) != MeasureSpec.EXACTLY) {
            //wrap_content
            val textWidth = max(leftTextWidth, rightTextWidth)
            val thumbSize =
                thumbDrawable?.run { heightSize - paddingTop - paddingBottom - thumbMargin - thumbMargin }
                    ?: 0
            widthSpec = MeasureSpec.makeMeasureSpec(
                (textWidth + thumbSize + 2 * thumbMargin + paddingLeft + paddingRight).toInt(),
                MeasureSpec.EXACTLY
            )
        }

        super.onMeasure(widthSpec, heightSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //canvas.drawColor(Color.BLACK)
        var x = thumbRect.left - thumbMargin - leftTextWidth
        var y = centerY + leftPaint.textHeight() / 2 - rightPaint.descent()
        canvas.drawText(leftText ?: "", x, y, leftPaint)

        thumbDrawable?.apply {
            bounds = thumbRect
            draw(canvas)
        }

        x = thumbRect.right.toFloat()
        y = centerY + rightPaint.textHeight() / 2 - rightPaint.descent()
        canvas.drawText(rightText ?: "", x, y, rightPaint)
    }

    val checkedChangeListener: OnCheckedChangeListener? = null

    interface OnCheckedChangeListener {
        fun onCheckedChanged(buttonView: SlideSwitchView, isChecked: Boolean, fromUser: Boolean)
    }

    /**文本的高度*/
    fun Paint?.textHeight(): Float = this?.run { descent() - ascent() } ?: 0f

    fun View?.interceptParentTouchEvent(event: MotionEvent) {
        if (event.actionMasked == MotionEvent.ACTION_DOWN) {
            disableParentInterceptTouchEvent()
        } else if (event.actionMasked == MotionEvent.ACTION_UP ||
            event.actionMasked == MotionEvent.ACTION_CANCEL
        ) {
            disableParentInterceptTouchEvent(false)
        }
    }

    /**禁止[Parent]拦截[TouchEvent]*/
    fun View?.disableParentInterceptTouchEvent(disable: Boolean = true) {
        this?.parent?.requestDisallowInterceptTouchEvent(disable)
    }

    /**文本的宽度*/
    fun Paint.textWidth(text: String?): Float {
        if (text == null) {
            return 0f
        }
        return measureText(text)
    }

    fun clamp(value: Int, min: Int, max: Int): Int = min(max(value, min), max)
}