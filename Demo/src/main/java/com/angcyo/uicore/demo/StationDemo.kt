package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.dslItem
import com.angcyo.library.annotation.Implementation
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/11
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
@Implementation
class StationDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        val stationItemList = mutableListOf<StationItem>()
        for (i in 0..30) {
            stationItemList.add(StationItem("站台$i"))
        }

        renderDslAdapter {
            /*dslItem(R.layout.station_layout) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.v<StationLayout>(R.id.station_layout)
                        ?.updateStation(object : StationAdapter() {
                            override fun getStationCount(): Int {
                                return stationItemList.size()
                            }

                            override fun createStationView(
                                layout: StationLayout,
                                type: Int,
                                index: Int
                            ): View {
                                val view = LayoutInflater.from(layout.context)
                                    .inflate(R.layout.station_item_layout, layout, false)
                                when (type) {
                                    StationLayout.STATION_TYPE_START -> {}
                                    StationLayout.STATION_TYPE_END -> {}
                                    else -> {
                                        view.findViewById<TextView>(R.id.text_view)?.text =
                                            stationItemList[index].text
                                    }
                                }
                                return view
                            }
                        })
                }
            }*/

            dslItem(R.layout.station_layout) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->

                }
            }
        }
    }
}

data class StationItem(
    val text: String
)