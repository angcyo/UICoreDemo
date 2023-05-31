package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.DrawTextView
import com.angcyo.uicore.getRandomText
import com.angcyo.widget.progress.DslSeekBar
import kotlin.random.Random

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/10/28
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class DrawTextDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_draw_text) { itemHolder, itemPosition, adapterItem, payloads ->
                val drawTextView = itemHolder.v<DrawTextView>(R.id.draw_text_view)
                drawTextView?.text = getRandomText(1, Random.nextInt(5, 10))

                itemHolder.v<DslSeekBar>(R.id.seek_bar)?.apply {
                    progressMinValue = -360f
                    progressMaxValue = 360f
                    showCenterProgressTip = true
                    progressTextFormatAction = {
                        getValue().toInt().toString()
                    }
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            drawTextView?.curvature = value
                            drawTextView?.invalidate()
                        }
                    }
                }
                itemHolder.v<DslSeekBar>(R.id.seek_bar2)?.apply {
                    val count = 10
                    progressTextFormatAction = {
                        "${(progressValue / 100f * count).toInt()}"
                    }
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            drawTextView?.text = getRandomText(1, (fraction * count).toInt())
                            drawTextView?.invalidate()
                        }
                    }
                }
            }
        }
    }
}