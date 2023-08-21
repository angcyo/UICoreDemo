package com.angcyo.uicore.demo

import android.os.Bundle
import android.widget.LinearLayout
import com.angcyo.dsladapter.renderItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.CurveTextView
import com.angcyo.widget.progress.DslSeekBar
import com.angcyo.widget.tab

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
                    itemHolder.check(R.id.debug_box, false) { buttonView, isChecked ->
                        curveTextView?.isCurveDebug = isChecked
                        curveTextView?.invalidate()
                    }

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
                    itemHolder.tab(R.id.lib_tab_layout)?.configTabLayoutConfig {
                        onSelectIndexChange = { fromIndex, selectIndexList, reselect, fromUser ->
                            val toIndex = selectIndexList.firstOrNull() ?: 0
                            curveTextView?.orientation =
                                if (toIndex == 1) LinearLayout.VERTICAL else LinearLayout.HORIZONTAL
                            curveTextView?.invalidate()
                        }
                    }
                }
            }
        }
    }
}