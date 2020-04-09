package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.angcyo.chart.dslPieChart
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex.randomColor
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.colors
import com.github.mikephil.charting.utils.ColorTemplate
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/09
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class PieChartDemo : AppDslFragment() {

    private fun generateCenterSpannableText(): SpannableString? {
        val s = SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda")
        s.setSpan(RelativeSizeSpan(1.7f), 0, 14, 0)
        s.setSpan(StyleSpan(Typeface.NORMAL), 14, s.length - 15, 0)
        s.setSpan(ForegroundColorSpan(Color.GRAY), 14, s.length - 15, 0)
        s.setSpan(RelativeSizeSpan(.8f), 14, s.length - 15, 0)
        s.setSpan(StyleSpan(Typeface.ITALIC), s.length - 14, s.length, 0)
        s.setSpan(ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length - 14, s.length, 0)
        return s
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            //1
            renderItem {
                itemLayoutId = R.layout.demo_pie_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslPieChart(itemHolder.v(R.id.chart)) {
                        chartDataSetColors = colors()
                        chartAnimateDurationY = 2000
                        pieCenterText = generateCenterSpannableText()
                        for (i in 0..3) {
                            val value = nextInt(0, 100) * Math.random()
                            addEntry(value.toFloat()) {
                                label = "Pie $i"
                            }
                        }
                        addDataSet("P1")
                    }
                }
            }

            //2
            renderItem {
                itemLayoutId = R.layout.demo_pie_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslPieChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        //chartDataSetWidth = 0.5f
                        chartLeftAxisMinimum = 0f
                        chartLeftAxisMaximum = 20f
                        barFitBars = true
                        for (i in 0..3) {
                            for (j in 0..10) {
                                val value = nextInt(0, 20) * Math.random()
                                addEntry(j.toFloat() * 2 + 3 * i, value.toFloat())
                            }
                            addDataSet("B$i") {
                                color = randomColor()
                            }
                        }
                    }?.animateXY(300, 300)
                }
            }

            //3
            renderItem {
                itemLayoutId = R.layout.demo_pie_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslPieChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartDataSetWidth = 2f
                        for (i in 0..10) {
                            val value = nextInt(0, 100) * Math.random()
                            addEntry(i.toFloat() * 3, value.toFloat())
                            addDataSet("B$i") {
                                color = randomColor()
                            }
                        }
                    }
                }
            }

            //5
            renderItem {
                itemLayoutId = R.layout.demo_pie_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslPieChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartAnimateDurationY = 2000
                        chartHighlightEnabled = true
                        barHighlightFullEnabled = true
                        for (i in 0..3) {
                            for (j in 0..10) {
                                val value = nextInt(0, 20) * Math.random()
                                addEntry(j.toFloat() + 3 * i, value.toFloat() * i)
                            }
                            chartDataSetColors = colors()
                            addDataSet("B$i")
                        }
                    }
                }
            }

            //no data
            renderItem {
                itemLayoutId = R.layout.demo_pie_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslPieChart(itemHolder.v(R.id.chart)) {
                    }
                }
            }
        }
    }
}