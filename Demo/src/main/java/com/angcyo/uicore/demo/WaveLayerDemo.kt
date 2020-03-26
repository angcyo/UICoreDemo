package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.renderItem
import com.angcyo.game.GameRenderView
import com.angcyo.game.layer.WaveLayer
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/26
 */
class WaveLayerDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            renderItem {
                itemLayoutId = R.layout.demo_wave_layer
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.v<GameRenderView>(R.id.game_render_view)?.gameRenderEngine?.apply {
                        gameLayerManager.apply {
                            clearLayer()
                            addLayer(WaveLayer())
                        }
                    }
                }
            }
        }
    }
}