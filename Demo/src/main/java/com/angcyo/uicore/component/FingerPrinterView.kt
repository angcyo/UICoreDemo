package com.angcyo.uicore.component

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import com.angcyo.uicore.demo.R

/**
 * Created by Zhangwh on 2016/12/29 0029.
 * email:616505546@qq.com
 */
class FingerPrinterView : View {

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
        const val STATE_NO_SCANING = 0
        const val STATE_WRONG_PWD = 1
        const val STATE_CORRECT_PWD = 2
        const val STATE_SCANING = 3
        var DEFAULT_DURATION = 700
    }

    var mCurrentState = STATE_NO_SCANING
    private val mResources: Resources
    private val mShakeAnim: Animation? = null // 抖动的动画
    var valueAnimator: ValueAnimator? = null
    private var mFraction = 0f
    private var mFraction2 = 1f
    private var scaningCount = 0
    var scale = 1.0f
    private var isAnim = true //判断是否要继续动画
    private var isScale = false //判断是否要缩放
    private var mBitPaint: Paint? = null
    private var mFingerRed: Bitmap? = null
    private var mFingerGreen: Bitmap? = null
    private var mFingerGrey: Bitmap? = null
    private var mSrcRect: Rect? = null
    private var mDestRect: Rect? = null
    private var mBitWidth = 0
    private var mBitHeight = 0
    private var mWidth = 0
    private var mHeight = 0
    var listener: OnStateChangedListener? = null

    init {
        mResources = resources
        //mShakeAnim = AnimationUtils.loadAnimation(context, R.anim.anim_lockpattern_shake_x);
        initBitmap()
        initPaint()
    }

    private fun initPaint() {
        mBitPaint = Paint()
        // 防抖动
        mBitPaint!!.isDither = true
        // 开启图像过滤
        mBitPaint!!.isFilterBitmap = true
    }

    private fun initBitmap() {
        mFingerRed = (mResources.getDrawable(R.drawable.finger_red) as BitmapDrawable).bitmap
        mFingerGreen = (mResources.getDrawable(R.drawable.finger_green) as BitmapDrawable).bitmap
        mFingerGrey = (mResources.getDrawable(R.drawable.finger_grey) as BitmapDrawable).bitmap
        mBitWidth = mFingerRed?.width ?: 0
        mBitHeight = mFingerRed?.height ?: 0
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        mFingerRed = setScale(mFingerRed)
        mFingerGreen = setScale(mFingerGreen)
        mFingerGrey = setScale(mFingerGrey)
        mBitWidth = mFingerRed!!.width
        mBitHeight = mFingerRed!!.height
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mBitPaint!!.alpha = 255
        mDestRect = Rect(
            (mBitHeight * (1 - mFraction2)).toInt(),
            (mBitHeight * (1 - mFraction2)).toInt(),
            (mBitHeight * mFraction2).toInt(),
            (mBitHeight * mFraction2).toInt()
        )
        mSrcRect = Rect(0, 0, mBitWidth, mBitHeight)
        canvas.drawBitmap(mFingerGrey!!, mSrcRect, mDestRect!!, mBitPaint)
        if (true) {
            if (scaningCount == 0) {
                mDestRect = Rect(0, 0, mBitWidth, (mBitHeight * mFraction).toInt())
                mSrcRect = Rect(0, 0, mBitWidth, (mBitHeight * mFraction).toInt())
                canvas.drawBitmap(mFingerGreen!!, mSrcRect, mDestRect!!, mBitPaint)
            } else if (scaningCount % 2 == 0) {
                if (mFraction <= 0.5) {
                    mBitPaint!!.alpha = (255 * (1 - mFraction)).toInt()
                    canvas.drawBitmap(mFingerRed!!, mSrcRect, mDestRect!!, mBitPaint)
                } else {
                    mBitPaint!!.alpha = (255 * mFraction).toInt()
                    canvas.drawBitmap(mFingerGreen!!, mSrcRect, mDestRect!!, mBitPaint)
                }
            } else {
                if (mFraction <= 0.5) {
                    mBitPaint!!.alpha = (255 * (1 - mFraction)).toInt()
                    canvas.drawBitmap(mFingerGreen!!, mSrcRect, mDestRect!!, mBitPaint)
                } else {
                    mBitPaint!!.alpha = (255 * mFraction).toInt()
                    canvas.drawBitmap(mFingerRed!!, mSrcRect, mDestRect!!, mBitPaint)
                }
            }
        }
        if (isScale) {
            if (mCurrentState == STATE_WRONG_PWD) {
                canvas.drawBitmap(mFingerRed!!, mSrcRect, mDestRect!!, mBitPaint)
            }
            if (mCurrentState == STATE_CORRECT_PWD) {
                canvas.drawBitmap(mFingerGreen!!, mSrcRect, mDestRect!!, mBitPaint)
            }
        }
    }

    fun startScaning() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 100f)
        valueAnimator.duration = DEFAULT_DURATION.toLong()
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { valueAnimator ->
            mFraction = valueAnimator.animatedFraction
            invalidate()
        }
        valueAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                if (isScale) {
                    isScale = false
                }
            }

            override fun onAnimationEnd(animator: Animator) {
                mFraction = 0f
                scaningCount++
                if (mCurrentState == STATE_WRONG_PWD && scaningCount % 2 == 1) {
                    isScale = true
                    isAnim = false
                    startScale()
                }
                if (mCurrentState == STATE_CORRECT_PWD && scaningCount % 2 == 0) {
                    isScale = true
                    isAnim = false
                    startScale()
                }
                if (isAnim) {
                    startScaning()
                }
            }

            override fun onAnimationCancel(animator: Animator) {}
            override fun onAnimationRepeat(animator: Animator) {}
        })
        if (!valueAnimator.isRunning) {
            valueAnimator.start()
        }
    }

    private fun startScale() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 100f)
        valueAnimator.duration = 500
        valueAnimator.interpolator = OvershootInterpolator(1.2f)
        valueAnimator.addUpdateListener { valueAnimator ->
            mFraction2 = 0.85f + 0.15f * valueAnimator.animatedFraction
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                if (listener != null) {
                    listener!!.onChange(mCurrentState)
                }
            }
        })
        if (!valueAnimator.isRunning) {
            valueAnimator.start()
        }
    }

    private fun startReset() {
        val valueAnimator = ValueAnimator.ofFloat(0f, 100f)
        valueAnimator.duration = DEFAULT_DURATION.toLong()
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addUpdateListener { valueAnimator ->
            mFraction = 1 - valueAnimator.animatedFraction
            invalidate()
        }
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                startScaning()
            }
        })
        if (!valueAnimator.isRunning) {
            valueAnimator.start()
        }
    }

    var state: Int
        get() = mCurrentState
        set(state) {
            mCurrentState = state
            when (state) {
                STATE_SCANING -> startScaning()
                STATE_WRONG_PWD -> Unit
                STATE_CORRECT_PWD -> Unit
                STATE_NO_SCANING -> resetConfig()
                else -> Unit
            }
        }

    /**
     * 处理图片缩放
     */
    private fun setScale(a: Bitmap?): Bitmap {
        scale = mWidth.toFloat() / mBitWidth
        val mMatrix = Matrix()
        mMatrix.postScale(scale, scale)
        return Bitmap.createBitmap(
            a!!, 0, 0, a.width, a.height,
            mMatrix, true
        )
    }

    fun resetConfig() {
        mCurrentState = STATE_NO_SCANING
        startReset()
        mFraction = 0f
        mFraction2 = 1f
        scaningCount = 0
        scale = 1.0f
        isAnim = true
        isScale = false
    }

    fun setOnStateChangedListener(listener: OnStateChangedListener?) {
        this.listener = listener
    }

    interface OnStateChangedListener {
        fun onChange(state: Int)
    }
}