package com.angcyo.uicore.demo

import android.graphics.RectF
import android.os.Bundle
import com.angcyo.core.component.step.DslStep
import com.angcyo.core.component.step.StepModel
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.dslItem
import com.angcyo.ilayer.ILayer
import com.angcyo.ilayer.LayerParams
import com.angcyo.ilayer.container.DragRectFConstraint
import com.angcyo.ilayer.container.ViewContainer
import com.angcyo.ilayer.container.WindowContainer
import com.angcyo.library.L
import com.angcyo.library._satusBarHeight
import com.angcyo.library._screenHeight
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.randomColor
import com.angcyo.library.ex.simpleHash
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.*
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/07/15
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class LayerDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        val layer: ILayer = object : ILayer() {
            init {
                iLayerLayoutId = R.layout.float_window_layout
            }

            override fun onInitLayer(viewHolder: DslViewHolder, params: LayerParams) {
                super.onInitLayer(viewHolder, params)
                viewHolder.clickItem {
                    L.w("${this.simpleHash()} this....click")
                }
            }
        }

        var index = 0

        renderDslAdapter {
            dslItem(R.layout.demo_layer) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->

                    val button = itemHolder.button(R.id.button)

                    itemHolder.click(R.id.button) {
                        layer.showCancelLayer = true
                        layer.dragContainer = DragRectFConstraint(
                            RectF(
                                0f,
                                _satusBarHeight * 1f / _screenHeight,
                                0f,
                                0.0000001f
                            )
                        )
                        layer.show(WindowContainer(fContext()))

                        button?.apply {
                            when (index++) {
                                0 -> updateSolidColor(randomColor())
                                1 -> updateGradientColors(
                                    intArrayOf(
                                        randomColor(),
                                        randomColor()
                                    )
                                )
                                2 -> updateRadius(nextInt(0, 45) * dp)
                                3 -> updateStrokeColor(randomColor(), nextInt(1, 10) * dpi)
                                else -> {
                                    updateStrokeColor(randomColor(), 0)
                                    updateRadius(nextInt(5, 10) * dp)
                                    index = 0
                                }
                            }
                        }
                    }

                    layer.enableDrag = true
                    layer.show(ViewContainer(itemHolder.group(R.id.lib_item_root_layout)!!))

                    itemHolder.click(R.id.frame_top_layout) {
                        layer.dragContainer = DragRectFConstraint(RectF(0.1f, 0.1f, 0.1f, 0.1f))
                        layer.show(ViewContainer(itemHolder.group(R.id.frame_top_layout)!!))
                    }
                    itemHolder.click(R.id.frame_bottom_layout) {
                        layer.enableLongPressDrag = true
                        layer.dragContainer = DragRectFConstraint(RectF(-0.1f, -0.1f, -0.1f, -0.1f))
                        layer.show(ViewContainer(itemHolder.group(R.id.frame_bottom_layout)!!))
                    }
                }
            }
        }

        //step
        val stepModel = vmApp<StepModel>()
        stepModel.stepCountData.observe {
            _vh.tv(R.id.text_step_view)?.text = "计步:$it"
        }
        DslStep.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        DslStep.stop()
    }
}