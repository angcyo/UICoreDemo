package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.amap3d.core.RTextureMapView
import com.angcyo.uicore.base.AppTitleFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/06/11
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class AMapDemo : AppTitleFragment() {
    init {
        contentLayoutId = R.layout.fragment_amap
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        _vh.v<RTextureMapView>(R.id.map_view)?.bindLifecycle(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _vh.v<RTextureMapView>(R.id.map_view)?.saveInstanceState(outState)
    }
}