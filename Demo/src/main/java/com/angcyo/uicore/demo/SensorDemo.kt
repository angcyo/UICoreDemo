package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.findItemByTag
import com.angcyo.dsladapter.marginVertical
import com.angcyo.item.DslBaseLabelItem
import com.angcyo.item.style.itemLabelText
import com.angcyo.library.LTime
import com.angcyo.library.component.DslSensor
import com.angcyo.library.ex._color
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.nowTime
import com.angcyo.library.ex.simpleClassName
import com.angcyo.library.utils.orientationString
import com.angcyo.library.utils.rotationString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/21
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class SensorDemo : AppDslFragment() {

    val dslSensor = DslSensor()

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        var startTime = nowTime()
        dslSensor.listenerOrientation(fContext()) {
            _adapter.findItemByTag("orientation")?.apply {
                this as DslBaseLabelItem
                /*传感器旋转角度*/
                val orientation = dslSensor.lastOrientation
                /*显示器旋转角度*/
                val rotation = activity?.windowManager?.defaultDisplay?.rotation?.rotationString()
                val time = LTime.time(startTime, nowTime())

                /*布局方向*/
                val resOrientation = resources.configuration.orientation.orientationString()

                itemLabelText =
                    "$time orientation:$orientation rotation:$rotation $resOrientation"

                updateAdapterItem()
            }
            startTime = nowTime()
        }

        renderDslAdapter {

            DslBaseLabelItem()() {
                itemTag = "orientation"
                marginVertical(1 * dpi)
                itemLabelText = "loading..."
            }

            DslSensor.getAllSensor(fContext()).apply {
                fragmentTitle = this@SensorDemo.simpleClassName() + "(${this.size})"
                forEach {
                    DslBaseLabelItem()() {
                        marginVertical(1 * dpi)
                        itemLabelText = span {
                            append(it.stringType) {
                                foregroundColor = _color(R.color.info)
                            }
                            appendln()
                            append("$it")
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dslSensor.release()
    }
}