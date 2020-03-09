package com.angcyo.uicore.demo

import android.os.Bundle
import android.view.View
import com.angcyo.game.GameRenderView
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.uicore.game.layer.ColorMatrixLayer

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/09
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class GameRenderEngineDemo : AppTitleFragment() {
    init {
        contentLayoutId = R.layout.demo_game_render_engine
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _vh.v<GameRenderView>(R.id.game_render_view)?.apply {
            gameRenderEngine.gameLayerManager.addLayer(ColorMatrixLayer())
        }
    }
}