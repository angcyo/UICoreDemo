package com.angcyo.uicore.demo.accessibility

import com.angcyo.github.window.DslFloatWindow
import com.angcyo.github.window.dslFloatWindow
import com.angcyo.library.app
import com.angcyo.library.ex.openApp
import com.angcyo.uicore.demo.R
import com.angcyo.widget.progress.CircleLoadingView

/**
 * 无障碍悬浮窗 任务状态提示
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
object AccessibilityWindow {

    val FLOAT_TAG = "AccessibilityWindow"

    /**[text] 需要提示的文本
     * [duration] 转圈时长, 毫秒*/
    fun show(text: CharSequence?, duration: Long = -1) {
        app().dslFloatWindow {
            floatTag = FLOAT_TAG
            floatLayoutId = R.layout.layout_accessibility_window

            initFloatLayout = { holder ->
                //常亮
                holder.itemView.keepScreenOn = true

                if (duration > 0) {
                    holder.v<CircleLoadingView>(R.id.progress_bar)?.setProgress(100, 0, duration)
                } else if (duration == 0L) {
                    holder.v<CircleLoadingView>(R.id.progress_bar)?.setProgress(0)
                }
                holder.tv(R.id.text_view)?.text = text

                holder.clickItem {
                    app().openApp()
                }
            }
        }.apply {
            show()
//            if (this is IFloatWindowImpl) {
//                this.resetPosition(getView(), false)
//            }
        }
    }

    fun hide() {
        DslFloatWindow.get(FLOAT_TAG)?.hide()
    }
}