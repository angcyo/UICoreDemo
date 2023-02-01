package com.angcyo.uicore.demo.draw

import android.content.Context
import android.util.AttributeSet
import android.view.View

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/02/01
 */
open class BaseMatrixView(context: Context, attributeSet: AttributeSet? = null) :
    View(context, attributeSet), IMatrixView {

    override var groupRotate: Float = 0f
        set(value) {
            field = value % 360
            subRotate += value
        }

    override var subRotate: Float = 0f
        set(value) {
            field = value % 360
            invalidate()
        }

    override var groupScaleX: Float = 1f
        set(value) {
            field = value
            invalidate()
        }
    override var groupScaleY: Float = 1f
        set(value) {
            field = value
            invalidate()
        }
    override var subScaleX: Float = 1f
        set(value) {
            field = value
            invalidate()
        }
    override var subScaleY: Float = 1f
        set(value) {
            field = value
            invalidate()
        }

    override var subSkewX: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
    override var subSkewY: Float = 0f
        set(value) {
            field = value
            invalidate()
        }
}