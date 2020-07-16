package com.angcyo.uicore.demo

import android.graphics.RectF
import android.os.Bundle
import com.angcyo.dsladapter.dslItem
import com.angcyo.ilayer.ILayer
import com.angcyo.ilayer.LayerParams
import com.angcyo.ilayer.container.DragRectFConstraint
import com.angcyo.ilayer.container.IContainer
import com.angcyo.ilayer.container.ViewContainer
import com.angcyo.ilayer.container.WindowContainer
import com.angcyo.library.L
import com.angcyo.library._satusBarHeight
import com.angcyo.library._screenHeight
import com.angcyo.library.ex.simpleHash
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.DslViewHolder

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

            override fun onInitLayer(
                container: IContainer,
                viewHolder: DslViewHolder,
                params: LayerParams
            ) {
                super.onInitLayer(container, viewHolder, params)
                viewHolder.clickItem {
                    L.w("${this.simpleHash()} this....click")
                }
            }
        }

        renderDslAdapter {
            dslItem(R.layout.demo_layer) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.click(R.id.button) {
                        layer.show(WindowContainer(fContext()).apply {
                            dragContainer = DragRectFConstraint(
                                RectF(
                                    0f,
                                    _satusBarHeight * 1f / _screenHeight,
                                    0f,
                                    0.0000001f
                                )
                            )
                        })
                    }

                    layer.show(ViewContainer(itemHolder.group(R.id.lib_item_root_layout)!!).apply {
                        enableDrag = true
                    })

                    itemHolder.click(R.id.frame_top_layout) {
                        layer.show(ViewContainer(itemHolder.group(R.id.frame_top_layout)!!).apply {
                            enableDrag = true
                            dragContainer = DragRectFConstraint(RectF(0.1f, 0.1f, 0.1f, 0.1f))
                        })
                    }
                    itemHolder.click(R.id.frame_bottom_layout) {
                        layer.show(ViewContainer(itemHolder.group(R.id.frame_bottom_layout)!!).apply {
                            enableLongPressDrag = true
                            dragContainer = DragRectFConstraint(RectF(-0.1f, -0.1f, -0.1f, -0.1f))
                        })
                    }
                }
            }
        }
    }

}