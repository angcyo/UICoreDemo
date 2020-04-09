package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.chart.dslBarChart
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex.randomColor
import com.angcyo.uicore.base.AppDslFragment
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/07
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class BarChartDemo : AppDslFragment() {

    init {
        //创建字体
        //val typeface = Typeface.createFromAsset(fContext().assets, "OpenSans-Light.ttf")
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            //1
            renderItem {
                itemLayoutId = R.layout.demo_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartDataSetColors =
                            listOf(
                                randomColor(),
                                randomColor(),
                                randomColor(),
                                randomColor(),
                                randomColor()
                            )
                        for (i in 0..0) {
                            for (j in 0..10) {
                                val value = nextInt(0, 100) * Math.random()
                                addBarEntry(j.toFloat() * 2, value.toFloat())
                            }
                            addBarDataSet("B$i")
                        }
                    }
                }
            }

            //2
            renderItem {
                itemLayoutId = R.layout.demo_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        //chartDataSetWidth = 0.5f
                        chartLeftAxisMinimum = 0f
                        chartLeftAxisMaximum = 20f
                        barGroupFromX = 0f
                        barFitBars = true
                        for (i in 0..3) {
                            for (j in 0..10) {
                                val value = nextInt(0, 20) * Math.random()
                                addBarEntry(j.toFloat() * 2 + 3 * i, value.toFloat())
                            }
                            addBarDataSet("B$i") {
                                color = randomColor()
                            }
                        }
                    }?.animateXY(300, 300)
                }
            }

            //3
            renderItem {
                itemLayoutId = R.layout.demo_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartDataSetWidth = 2f
                        for (i in 0..10) {
                            val value = nextInt(0, 100) * Math.random()
                            addBarEntry(i.toFloat() * 3, value.toFloat())
                            addBarDataSet("B$i") {
                                color = randomColor()
                            }
                        }
                    }
                }
            }

            //no data
            renderItem {
                itemLayoutId = R.layout.demo_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
                    }
                }
            }
        }
    }
}