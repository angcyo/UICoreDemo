package com.angcyo.uicore.component

import android.animation.Animator
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.Px
import androidx.core.graphics.withClip
import androidx.core.math.MathUtils
import androidx.core.view.GestureDetectorCompat
import com.angcyo.library.ex.anim
import com.angcyo.library.ex.evaluateColor
import com.angcyo.library.ex.textHeight
import com.angcyo.tablayout.textWidth

/**
 * 带刻度的滑块
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/07/27
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class RuleSliderView2(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet) {

    /**滑轨的高度*/
    @Px
    var sliderHeight: Int = -1

    /**滑轨的圆角*/
    var sliderRound: Int = -1

    /**浮子的半径*/
    @Px
    var thumbRadius: Int = -1

    /**浮子上下距离, 预留足够多的位置, 用来显示上面的刻度, 和下面的文本*/
    var thumbOffsetTop: Int = -1
    var thumbOffsetBottom: Int = -1

    /**最小和最大值*/
    var minValue = 50
    var maxValue = 300

    /**吸附阈值*/
    var adsorbThreshold: Int = 20

    /**当前的值*/
    var currentValue = minValue

    /**刻度列表*/
    var ruleList = mutableListOf<RuleInfo>()

    /**滑块背景颜色*/
    var sliderBgColor = Color.DKGRAY

    /**滑轨的渐变颜色值*/
    //蓝色 #96E1FF -》#00C1FF
    //粉色 #FF6CBC -》 #FF008A
    var sliderColors = intArrayOf(
        Color.parseColor("#96E1FF"),
        Color.parseColor("#00C1FF"),
        Color.parseColor("#FF6CBC"),
        Color.parseColor("#FF008A"),
    )

    //滑轨画笔
    val sliderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    //浮子画笔
    val thumbPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    //刻度画笔
    val rulePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }

    init {
        val dp = context.resources.displayMetrics.density
        sliderHeight = 4 * dp.toInt()
        sliderRound = 25 * dp.toInt()
        thumbRadius = 10 * dp.toInt()
        thumbOffsetTop = 40 * dp.toInt()
        thumbOffsetBottom = 40 * dp.toInt()

        //textSize
        rulePaint.textSize = 14 * dp
        thumbPaint.textSize = 14 * dp
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var heightSpec = heightMeasureSpec
        val heightMode = MeasureSpec.getMode(heightSpec)
        if (heightMode != MeasureSpec.EXACTLY) {
            val heightSize =
                thumbRadius * 2 + thumbOffsetTop + thumbOffsetBottom + paddingTop + paddingBottom
            heightSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightSpec)
    }

    //浮子中点x坐标
    val thumbCenterX: Float
        get() = calcCenterX(currentValue)

    //浮子中点y坐标
    val thumbCenterY: Float
        get() = (paddingTop + thumbOffsetTop + thumbRadius).toFloat()

    val sliderRect = RectF()

    var sliderShader: Shader? = null

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        sliderRect.set(
            paddingLeft.toFloat(),
            (thumbCenterY - sliderHeight / 2),
            (measuredWidth - paddingRight).toFloat(),
            (thumbCenterY + sliderHeight / 2)
        )
        sliderShader = LinearGradient(
            sliderRect.left,
            0f,
            sliderRect.right,
            0f,
            sliderColors,
            null,
            Shader.TileMode.REPEAT
        )
    }

    /**计算当前的值, 在整个范围中的比例*/
    fun calcValueRatio(value: Int = currentValue): Float {
        return (value - minValue) * 1f / (maxValue - minValue)
    }

    /**计算指定进度, 绘制的x坐标*/
    fun calcCenterX(value: Int): Float {
        val ratio = calcValueRatio(value)
        return sliderRect.left + sliderRect.width() * ratio
    }

    val _ruleRect = RectF()
    val _progressRect = RectF()
    val _progressTextRect = RectF()

    fun getProgressRect(): RectF {
        _progressRect.set(sliderRect.left, sliderRect.top, thumbCenterX, sliderRect.bottom)
        return _progressRect
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        //绘制滑轨
        sliderPaint.shader = null
        sliderPaint.color = sliderBgColor
        canvas.drawRoundRect(sliderRect, sliderRound.toFloat(), sliderRound.toFloat(), sliderPaint)
        sliderPaint.shader = sliderShader
        canvas.withClip(getProgressRect()) {
            canvas.drawRoundRect(
                sliderRect,
                sliderRound.toFloat(),
                sliderRound.toFloat(),
                sliderPaint
            )
        }

        //刻度
        if (isTouchDown) {
            ruleList.forEach { info ->
                rulePaint.color = info.color
                val x = calcCenterX(info.value)
                //rect
                _ruleRect.set(
                    x - info.width / 2,
                    sliderRect.top - info.height,
                    x + info.width / 2,
                    sliderRect.top
                )
                canvas.drawRect(_ruleRect, rulePaint)
                //text
                rulePaint.color = info.textColor
                info.text?.let {
                    drawText(canvas, it, info.value, rulePaint)
                }
            }
        }

        //绘制浮子
        thumbPaint.color = evaluateColor(calcValueRatio(currentValue), sliderColors, null)
        canvas.drawCircle(thumbCenterX, thumbCenterY, thumbRadius.toFloat(), thumbPaint)

        //浮子进度
        drawProgressText(canvas)
    }

    /**在指定进度位置绘制文本*/
    fun drawText(canvas: Canvas, text: String, progress: Int, paint: Paint, offsetY: Float = 0f) {
        val x = calcCenterX(progress)
        val textWidth = paint.textWidth(text)
        val textHeight = paint.textHeight()
        canvas.drawText(
            text,
            x - textWidth / 2,
            sliderRect.bottom + textHeight + offsetY,
            paint
        )
    }

    fun drawProgressText(canvas: Canvas) {
        val x = thumbCenterX
        val progressText = "$currentValue"
        val textWidth = thumbPaint.textWidth(progressText)
        val textHeight = thumbPaint.textHeight()

        val offsetY = 8
        val offsetWidth = 40
        val top = thumbCenterY - thumbRadius - offsetY - textHeight
        _progressTextRect.set(
            x - textWidth / 2 - offsetWidth / 2,
            top,
            x + textWidth / 2 + offsetWidth / 2,
            top + textHeight
        )
        /*thumbPaint.shader = LinearGradient(
            _progressTextRect.left,
            0f,
            _progressTextRect.right,
            0f,
            sliderColors.reversed().toIntArray(),
            null,
            Shader.TileMode.REPEAT
        )
        canvas.drawRoundRect(
            _progressTextRect, sliderRound.toFloat(),
            sliderRound.toFloat(),
            thumbPaint
        )*/

        thumbPaint.shader = null
        thumbPaint.color = Color.WHITE
        canvas.drawText(
            progressText,
            x - textWidth / 2,
            _progressTextRect.bottom - thumbPaint.descent(),
            thumbPaint
        )
    }

    var _lastVelocityX = 0f

    //手势检测
    val _gestureDetector: GestureDetectorCompat by lazy {
        GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                _lastVelocityX = velocityX
                /*var ruleInfo: RuleInfo? = null
                if (velocityX > 0) {
                    //向右快速滑动
                    for (info in ruleList) {
                        if (info.progress > sliderProgress &&
                            adsorbProgress(sliderProgress, info.progress)
                        ) {
                            ruleInfo = info
                            break
                        }
                    }
                } else {
                    //向左快速滑动
                    for (info in ruleList) {
                        if (info.progress < sliderProgress &&
                            adsorbProgress(sliderProgress, info.progress)
                        ) {
                            ruleInfo = info
                            break
                        }
                    }
                }
                if (ruleInfo == null) {
                    //未找到推荐数据
                    val increase = if (velocityX > 0) {
                        100 * 0.2
                    } else {
                        -100 * 0.2
                    }.toInt()
                    updateProgress(sliderProgress + increase, true)
                } else {
                    updateProgress(ruleInfo.progress, true)
                }*/
                return true
            }
        })
    }

    /**回调监听*/
    var onSliderConfig: SliderConfig? = null

    /**是否按下*/
    var isTouchDown = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)
        _gestureDetector.onTouchEvent(event)

        val action = event.actionMasked
        _onTouchMoveTo(
            event.x,
            event.y,
            action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL
        )
        if (action == MotionEvent.ACTION_DOWN) {
            isTouchDown = true
            _lastVelocityX = 0f
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (action == MotionEvent.ACTION_MOVE) {
            //no op
        } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            isTouchDown = false
            parent.requestDisallowInterceptTouchEvent(false)

            if (_lastVelocityX != 0f) {
                if (_lastVelocityX > 0) {
                    //向右快速滑动
                    for (info in ruleList) {
                        if (info.value > currentValue &&
                            (info.value - currentValue) <= adsorbThreshold
                        ) {
                            updateValue(info.value, true, true)
                            break
                        }
                    }
                } else {
                    //向左快速滑动
                    for (info in ruleList) {
                        if (info.value < currentValue &&
                            (currentValue - info.value) <= adsorbThreshold
                        ) {
                            updateValue(info.value, true, true)
                            break
                        }
                    }
                }
            }

            onSliderConfig?.apply { onSeekTouchEnd(currentValue, calcValueRatio(currentValue)) }
        }
        invalidate()
        return true
    }

    /**手指移动*/
    fun _onTouchMoveTo(x: Float, y: Float, isFinish: Boolean) {
        val ratio: Float = (x - sliderRect.left) / sliderRect.width()

        val value: Int = (minValue + (maxValue - minValue) * ratio).toInt()
        updateValue(value, false, isFinish)

        Log.i("angcyo", "touch:$value")
    }

    fun updateValue(value: Int, anim: Boolean, fromUser: Boolean) {
        val oldValue = currentValue
        val newValue = validValue(value)
        currentValue = newValue
        onSliderConfig?.apply { onSeekChanged(newValue, calcValueRatio(), fromUser) }
        invalidate()

        _animtor?.cancel()
        _animtor = null
        if (anim) {
            currentValue = oldValue
            invalidate()
            _animtor = anim(oldValue, newValue) {
                onAnimatorConfig = {
                    it.duration = 300
                    it.interpolator = AccelerateDecelerateInterpolator()
                }
                onAnimatorUpdateValue = { value, _ ->
                    currentValue = value as Int
                    postInvalidateOnAnimation()
                }
            }
        }
    }

    /**限制设置的非法进度值*/
    fun validValue(value: Int): Int {
        return MathUtils.clamp(value, minValue, maxValue)
    }

    //</editor-fold desc="Touch事件">

    fun config(action: SliderConfig.() -> Unit) {
        if (onSliderConfig == null) {
            onSliderConfig = SliderConfig()
        }
        onSliderConfig?.action()
    }

    var _animtor: Animator? = null

    /**刻度信息*/
    data class RuleInfo(
        //刻度的颜色
        val color: Int = Color.RED,
        //刻度的文本
        val text: String? = null,
        //刻度所在的值
        val value: Int = 10,
        //刻度文本的颜色
        val textColor: Int = Color.DKGRAY,
        //宽度
        @Px
        val width: Int = 4,
        //高度
        @Px
        val height: Int = 20,
    )

    open class SliderConfig {
        /**进度改变回调,
         * [value] 进度值
         * [fraction] 进度比例
         * [fromUser] 是否是用户触发*/
        var onSeekChanged: (value: Int, fraction: Float, fromUser: Boolean) -> Unit = { _, _, _ -> }

        /**Touch结束后的回调*/
        var onSeekTouchEnd: (value: Int, fraction: Float) -> Unit = { _, _ -> }
    }
}