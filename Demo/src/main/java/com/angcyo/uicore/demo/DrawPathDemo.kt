package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.canvas.utils.ShapesHelper
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.mH
import com.angcyo.library.ex.mW
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.DrawPathView
import com.angcyo.widget.progress.DslSeekBar
import com.pixplicity.sharp.Sharp

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/15
 */
class DrawPathDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.item_draw_path_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                val drawPathView = itemHolder.v<DrawPathView>(R.id.draw_path_view)
                drawPathView?.apply {
                    val path = Sharp.loadPath("M14,85l3,9h72c0,0,5-9,4-10c-2-2-79,0-79,1")
                    pathList.add(path)
                    invalidate()
                }

                itemHolder.v<DslSeekBar>(R.id.seek_bar)?.apply {
                    progressTextFormatAction = {
                        "$progressValue"
                    }
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            if (value > 2) {
                                drawPathView?.polygonPath = ShapesHelper.polygonPath(
                                    value,
                                    drawPathView.mW() - 100f,
                                    drawPathView.mH() - 100f
                                )
                                drawPathView?.invalidate()
                            }
                        }
                    }
                }
            }
        }
    }

}