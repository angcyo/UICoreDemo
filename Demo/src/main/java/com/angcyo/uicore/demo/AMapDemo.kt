package com.angcyo.uicore.demo

import android.os.Bundle
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.angcyo.amap3d.*
import com.angcyo.amap3d.DslMarker
import com.angcyo.amap3d.core.MapLocation
import com.angcyo.amap3d.core.RTextureMapView
import com.angcyo.amap3d.core.latLng
import com.angcyo.amap3d.fragment.aMapDetail
import com.angcyo.amap3d.fragment.aMapSelector
import com.angcyo.base.dslFHelper
import com.angcyo.library.L
import com.angcyo.library.ex.nowTimeString
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

    //latitude 纬度, 决定上下距离
    //longitude 经度, 决定左右距离
    val latLngList = mutableListOf(
        LatLng(22.610401703960985, 114.04578058524385),
        LatLng(22.610815204617325, 114.04641895097848),
        LatLng(22.610931578829426, 114.04645381969507),
        LatLng(22.610977385673138, 114.047281281162),
        LatLng(22.610977385673138, 114.048281281162),
        LatLng(22.610977385673138, 114.04881281162),
        LatLng(22.610977385673138, 114.049081281162),

        //
        LatLng(22.632077385673138, 114.049081281162),
        LatLng(22.632177385673138, 114.049091281162),
        LatLng(22.632277385673138, 114.049081281162),
        LatLng(22.632377385673138, 114.049101281162),
        LatLng(22.632477385673138, 114.049111281162),
        LatLng(22.632577385673138, 114.049121281162),
        LatLng(22.632677385673138, 114.049131281162)
    )

    var _mapLocation: MapLocation? = null

    lateinit var dslMarker: DslMarker



    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        val iconList = listOf(
            markerIcon(R.drawable.icon_marker_1),
            markerIcon(R.drawable.icon_marker_2),
            markerIcon(R.drawable.icon_marker_3),
            markerIcon(R.drawable.icon_marker_4),
            markerIcon(R.drawable.icon_marker_5),
            markerIcon(R.drawable.icon_marker_6),
            markerIcon(R.drawable.icon_marker_7),
            markerIcon(R.drawable.icon_marker_8),
            markerIcon(R.drawable.icon_marker_9),
            markerIcon(R.drawable.icon_marker_10),
            markerIcon(R.drawable.icon_marker_11),
            markerIcon(R.drawable.icon_marker_12)
        )

        _vh.v<RTextureMapView>(R.id.map_view)?.apply {
            this@AMapDemo.dslMarker = dslMarker

            dslAMap.apply {
                locationIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_gps_point)
            }

            bindLifecycle(this@AMapDemo, savedInstanceState)

            map.bindControlLayout(_vh, dslAMap.customStyleOptions)

            map.onMapLoadedListener {
                map.addNavigateArrow {
                    add(LatLng(map.myLocation.latitude, map.myLocation.longitude))
                    width(100f)
                }

                dslMarker.apply {
                    markerSelectOptions = markerOptions {
                        icon(markerIcon(R.drawable.icon_marker_danger))
                    }
                    latLngList.forEach {
                        addMarker(it) {
                            icons(iconList, 24)
                        }
                    }
                    moveToShowAllMarker()
                }
            }

            map.onMyLocationChange {
                //L.d(it)
            }

            map.onCameraChangeListener {
                L.d(it)
            }
        }

        _vh.click(R.id.button) {
            dslFHelper {
                aMapSelector {
                    _mapLocation = it
                    if (it == null) {
                        _vh.tv(R.id.result_text_view)?.text = "取消选址:${nowTimeString()}"
                    } else {
                        _vh.tv(R.id.result_text_view)?.text = it.toString()
                        L.i(it)

                        dslMarker.addMarker(it.latLng())
                    }
                }
            }
        }
        _vh.throttleClick(R.id.result_text_view) {
            _mapLocation?.let {
                dslFHelper {
                    aMapDetail(it)
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _vh.v<RTextureMapView>(R.id.map_view)?.saveInstanceState(outState)
    }
}