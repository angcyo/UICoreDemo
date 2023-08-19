package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.renderItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.CurveTextView
import com.angcyo.widget.progress.DslSeekBar

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/08/17
 */
class CurveTextDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            renderItem {
                itemLayoutId = R.layout.demo_curve_text

                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    val curveTextView = itemHolder.v<CurveTextView>(R.id.curve_text_view)
                    itemHolder.v<DslSeekBar>(R.id.seek_bar)?.apply {
                        progressTextFormatAction = {
                            "${progressValue.toInt()}"
                        }
                        config {
                            onSeekChanged = { value, fraction, fromUser ->
                                curveTextView?.curve = value
                                curveTextView?.invalidate()
                            }
                        }
                    }
                    itemHolder.v<DslSeekBar>(R.id.seek_bar2)?.apply {
                        progressTextFormatAction = {
                            "${progressValue.toInt()}"
                        }
                        config {
                            onSeekChanged = { value, fraction, fromUser ->
                                curveTextView?.curve = value
                                curveTextView?.invalidate()
                            }
                        }
                    }
                }
            }
        }
    }
}