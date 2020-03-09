package com.angcyo.game.core

import android.graphics.Canvas
import com.angcyo.game.layer.BaseLayer
import com.angcyo.game.layer.BaseLayer.Companion.LAYER_STATUS_PAUSE_DRAW
import com.angcyo.game.layer.BaseLayer.Companion.LAYER_STATUS_PAUSE_UPDATE
import com.angcyo.library.ex.have
import java.util.concurrent.CopyOnWriteArrayList

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class GameLayerManager(val engine: GameRenderEngine) {

    val layerList = CopyOnWriteArrayList<BaseLayer>()

    /**引擎参数更新*/
    fun onEngineUpdate(engineParams: EngineParams) {
        val iterator = layerList.iterator()
        while (iterator.hasNext()) {
            iterator.next()?.also { layer ->
                layer.onLayerUpdate(engineParams.engineWidth, engineParams.engineHeight)
            }
        }
    }

    fun addLayer(layer: BaseLayer) {
        layerList.add(layer)
        layer.onLayerUpdate(engine._engineParams.engineWidth, engine._engineParams.engineHeight)
    }

    fun removeLayer(layer: BaseLayer) {
        layerList.remove(layer)
    }

    fun draw(canvas: Canvas) {
        val iterator = layerList.iterator()
        while (iterator.hasNext()) {
            iterator.next()?.also { layer ->
                if (!layer.layerStatus.have(LAYER_STATUS_PAUSE_DRAW)) {
                    layer.draw(canvas)
                }
            }
        }
    }

    fun update() {
        val iterator = layerList.iterator()
        while (iterator.hasNext()) {
            iterator.next()?.also { layer ->
                if (!layer.layerStatus.have(LAYER_STATUS_PAUSE_UPDATE)) {
                    layer.update()
                }
            }
        }
    }
}