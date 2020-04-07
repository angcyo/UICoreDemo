package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.chart.dslBarChart
import com.angcyo.dsladapter.renderItem
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
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            //1
            renderItem {
                itemLayoutId = R.layout.demo_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
                        for (i in 0..30) {
                            for (j in 0..100) {
                                val value = nextInt(0, 100) * Math.random()
                                addBarEntry(i.toFloat(), value.toFloat())
                            }
                            addBarDataSet("B$i")
                        }
                    }
                }
            }
        }
    }
}