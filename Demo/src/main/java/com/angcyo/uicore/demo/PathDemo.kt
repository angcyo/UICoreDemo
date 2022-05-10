package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.dslItem
import com.angcyo.library._screenHeight
import com.angcyo.library._screenWidth
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.component.PathView
import com.angcyo.widget.progress.DslSeekBar
import kotlin.math.max

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/04
 */
class PathDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            dslItem(R.layout.demo_path) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    val pathView = itemHolder.v<PathView>(R.id.path_view)
                    itemHolder.v<DslSeekBar>(R.id.oval_w_seek)?.config {
                        onSeekChanged = { value, fraction, fromUser ->
                            pathView?.ovalWidth = _screenWidth * max(0.1f, fraction)
                            pathView?.invalidate()
                        }
                    }
                    itemHolder.v<DslSeekBar>(R.id.oval_h_seek)?.config {
                        onSeekChanged = { value, fraction, fromUser ->
                            pathView?.ovalHeight = _screenHeight * max(0.1f, fraction)
                            pathView?.invalidate()
                        }
                    }

                    itemHolder.v<DslSeekBar>(R.id.rect_w_seek)?.config {
                        onSeekChanged = { value, fraction, fromUser ->
                            pathView?.inputWidth = _screenWidth * max(0.1f, fraction)
                            pathView?.invalidate()
                        }
                    }
                    itemHolder.v<DslSeekBar>(R.id.rect_h_seek)?.config {
                        onSeekChanged = { value, fraction, fromUser ->
                            pathView?.inputHeight = _screenHeight * max(0.1f, fraction)
                            pathView?.invalidate()
                        }
                    }
                }
            }
        }

    }

}