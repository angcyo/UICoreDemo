package com.angcyo.uicore.demo

import android.os.Bundle
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.LatLng
import com.angcyo.amap3d.*
import com.angcyo.amap3d.core.MapLocation
import com.angcyo.amap3d.core.latLng
import com.angcyo.amap3d.fragment.aMapDetail
import com.angcyo.amap3d.fragment.aMapSelector
import com.angcyo.base.dslFHelper
import com.angcyo.library.L
import com.angcyo.library.ex._color
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

    /*纵向 小值在下*/
    var latitude = 22.588031111895674
        get() {
            val result = field
            field = result + 0.0006f
            return result
        }

    /*横向 小值在左*/
    var longitude = 114.06615196236346
        get() {
            val result = field
            field = result - 0.0002f
            return result
        }

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

        val mapView = _vh.initMapView(this, savedInstanceState) {

            dslAMap.apply {
                showCompass = true
                showScaleControl = true
                locationMoveFirst = false
                locationIcon = BitmapDescriptorFactory.fromResource(R.drawable.map_location_gps_3d)
            }

            map.onMapLoadedListener {

                map.myLatLng()?.let {
                    //在地图上添加一个导航指示箭头对象（navigateArrow）对象。
                    map.addNavigateArrow {
                        add(it)
                        add(latLngList.first().offsetDistance(5f, -5f))
                        topColor(_color(R.color.colorAccent))
                        width(10f)
                    }
                }

                dslMarker.apply {
                    toHeatMapZoom = -1f

                    markerSelectedOptions = markerOptions {
                        icon(markerIcon(R.drawable.icon_marker_danger))
                    }
                    latLngList.forEach {
                        addMarker(it) {
                            icons(iconList, 24)
                        }
                    }

                    //2020-11-27
                    addMarker(LatLng(latitude, longitude)) {
                        icon(R.drawable.map_gps_point)
                        title("Title")
                        snippet("Snippet")
                    }?.showInfoWindow()

                    moveToShowAllMarker(true)
                }
            }

            map.onMyLocationChange {
                //L.d(it)
            }

            map.onCameraChangeListener {
                L.d(it)
            }
        }

        //选择地址
        _vh.click(R.id.button) {
            dslFHelper {
                aMapSelector {
                    _mapLocation = it
                    if (it == null) {
                        _vh.tv(R.id.result_text_view)?.text = "取消选址:${nowTimeString()}"
                    } else {
                        _vh.tv(R.id.result_text_view)?.text = it.toString()
                        L.i(it)

                        mapView?.dslMarker?.addMarker(it.latLng())
                    }
                }
            }
        }
        //地址详情
        _vh.throttleClick(R.id.result_text_view) {
            _mapLocation?.let {
                dslFHelper {
                    aMapDetail(it)
                }
            }
        }
        //添加Marker
        _vh.throttleClick(R.id.add_marker_button) {
            mapView?.dslMarker?.addMarker(LatLng(latitude, longitude)) {
                icon(R.drawable.map_gps_point)
                title("Title")
                snippet("Snippet")
            }?.showInfoWindow()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        _vh.saveMapInstanceState(outState)
    }
}