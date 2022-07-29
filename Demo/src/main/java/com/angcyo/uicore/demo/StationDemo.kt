package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.dsladapter.dslItem
import com.angcyo.library.annotation.Implementation
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.component.RuleSliderView2
import com.angcyo.widget.slider.RuleSliderView

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
                    val ruleView = itemHolder.v<RuleSliderView>(R.id.rule_slider_view)
                    val ruleView2 = itemHolder.v<RuleSliderView2>(R.id.rule_slider_view2)

                    ruleView?.ruleList?.add(
                        RuleSliderView.RuleInfo(
                            Color.YELLOW,
                            "YELLOW",
                            RuleSliderView.calcProgress(50)
                        )
                    )
                    ruleView?.ruleList?.add(
                        RuleSliderView.RuleInfo(
                            Color.RED,
                            "RED",
                            RuleSliderView.calcProgress(150)
                        )
                    )
                    ruleView?.ruleList?.add(
                        RuleSliderView.RuleInfo(
                            Color.BLUE,
                            "BLUE",
                            RuleSliderView.calcProgress(200)
                        )
                    )
                    ruleView?.ruleList?.add(
                        RuleSliderView.RuleInfo(
                            Color.GREEN,
                            "GREEN",
                            RuleSliderView.calcProgress(250)
                        )
                    )
                    ruleView?.ruleList?.add(
                        RuleSliderView.RuleInfo(
                            Color.MAGENTA,
                            "MAGENTA",
                            RuleSliderView.calcProgress(300)
                        )
                    )
                    ruleView?.postInvalidate()

                    //2
                    ruleView2?.ruleList?.add(
                        RuleSliderView2.RuleInfo(
                            Color.YELLOW,
                            "YELLOW",
                            50
                        )
                    )
                    ruleView2?.ruleList?.add(
                        RuleSliderView2.RuleInfo(
                            Color.RED,
                            "RED",
                            150
                        )
                    )
                    ruleView2?.ruleList?.add(
                        RuleSliderView2.RuleInfo(
                            Color.BLUE,
                            "BLUE",
                            200
                        )
                    )
                    ruleView2?.ruleList?.add(
                        RuleSliderView2.RuleInfo(
                            Color.GREEN,
                            "GREEN",
                            250
                        )
                    )
                    ruleView2?.ruleList?.add(
                        RuleSliderView2.RuleInfo(
                            Color.MAGENTA,
                            "MAGENTA",
                            300
                        )
                    )
                    ruleView2?.postInvalidate()
                }
            }
        }
    }
}

data class StationItem(
    val text: String
)