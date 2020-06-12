package com.angcyo.uicore.demo

import android.os.Bundle
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.angcyo.amap3d.addNavigateArrow
import com.angcyo.amap3d.core.RTextureMapView
import com.angcyo.amap3d.onMapLoadedListener
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
        _vh.v<RTextureMapView>(R.id.map_view)?.apply {
            dslAMap.locationIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_gps_point)

            bindLifecycle(this@AMapDemo, savedInstanceState)

            map.onMapLoadedListener {
                map.addNavigateArrow {
                    add(LatLng(map.myLocation.latitude, map.myLocation.longitude))
                    width(100f)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _vh.v<RTextureMapView>(R.id.map_view)?.saveInstanceState(outState)
    }
}