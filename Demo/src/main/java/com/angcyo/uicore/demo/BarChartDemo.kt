package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.chart.dslBarChart
import com.angcyo.chart.formatter.ArrayFormatter
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.randomColor
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.colors
import com.angcyo.uicore.value
import com.github.mikephil.charting.components.Legend
import kotlin.math.max
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
                        chartDataSetColors = colors()
                        barDrawValueAboveBar = false

                        for (i in 0..0) {
                            for (j in 0..10) {
                                val value = nextInt(0, 100) * Math.random()
                                addEntry(j.toFloat() * 2, value.toFloat())
                            }
                            addDataSet("B$i")
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
                itemLayoutId = R.layout.demo_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
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

            //4 horizontal
            renderItem {
                itemLayoutId = R.layout.demo_horizontal_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartDataSetColors = colors()
                        chartAnimateDurationY = 2000
                        barDrawValueAboveBar = true
                        for (i in 0..0) {
                            for (j in 0..10) {
                                val value = nextInt(0, 20) * Math.random()
                                addEntry(j.toFloat() + 3 * i, value.toFloat())
                            }
                            addDataSet("B$i")
                        }
                    }
                }
            }

            //5
            renderItem {
                itemLayoutId = R.layout.demo_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
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

            //6 让X轴的Label, 一一对应Bar
            renderItem {
                itemLayoutId = R.layout.demo_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
                        chartAnimateDurationY = 2000
                        chartDrawValues = true
                        chartXAxisCenterLabels = false

                        chartScaleXEnabled = false
                        chartDragXEnabled = true

                        chartLeftAxisForceLabels = false
                        chartDataSetWidth = 0.5f
                        //label和entry的数量相等, 那么Label就会在Entry下面绘制
                        chartXAxisLabelCount = 6
                        //左右预览一半BarWidth的空隙
                        barFitBars = true

                        val entryCount = 3

                        for (i in 0..0) {
                            val labels = mutableListOf<String>()
                            for (j in 0 until entryCount) {
                                val value = value(0, 20)
                                addEntry(j.toFloat(), value.toFloat())

                                labels.add("$j 级")
                            }

                            //补位
                            for (j in entryCount until chartXAxisLabelCount) {
                                labels.add("$j 补")
                                addEntry(j.toFloat(), 0f) {
                                    isDrawValue = false
                                }
                            }

                            chartXAxisValueFormatter = ArrayFormatter(labels)
                            chartDataSetColors = colors()
                            addLeftAxisLimitLine(5f, "L$i") {
                                enableDashedLine(2 * dp, 3 * dp, 0f)
                            }
                            addLeftAxisLimitLine(15f, "L$i") {
                                enableDashedLine(3 * dp, 2 * dp, 10f)
                            }
                            addXAxisLimitLine(5f, "L$i") {
                                enableDashedLine(3 * dp, 4 * dp, 10f)
                            }
                            addDataSet("B$i")
                        }
                    }
                }
            }

            //7
            renderItem {
                itemLayoutId = R.layout.demo_bar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
                        chartAnimateDurationY = 2000
                        chartDrawValues = true
                        chartXAxisCenterLabels = false

                        chartLeftAxisForceLabels = false
                        chartDataSetWidth = 0.5f
                        //label和entry的数量相等, 那么Label就会在Entry下面绘制
                        chartXAxisLabelCount = 6
                        //左右预览一半BarWidth的空隙
                        barFitBars = true

                        chartScaleXEnabled = false
                        chartDragXEnabled = true

                        val entryCount = 30

                        chartScaleMinimumX =
                            max(entryCount, chartXAxisLabelCount) * 1f / chartXAxisLabelCount

                        for (i in 0..0) {
                            val labels = mutableListOf<String>()
                            for (j in 0 until entryCount) {
                                val value = value(0, 20)
                                addEntry(j.toFloat(), value.toFloat())

                                labels.add("$j 级")
                            }
                            chartXAxisValueFormatter = ArrayFormatter(labels)
                            chartDataSetColors = colors()
                            addLeftAxisLimitLine(5f, "L$i") {
                                enableDashedLine(2 * dp, 3 * dp, 0f)
                            }
                            addLeftAxisLimitLine(15f, "L$i") {
                                enableDashedLine(3 * dp, 2 * dp, 10f)
                            }
                            addXAxisLimitLine(5f, "L$i") {
                                enableDashedLine(3 * dp, 4 * dp, 10f)
                            }
                            addDataSet("B$i")
                        }
                    }
                }
            }

            //8
            renderItem {
                itemLayoutId = R.layout.demo_bar_chart_wrap
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBarChart(itemHolder.v(R.id.chart)) {
                        chartAnimateDurationY = 2000
                        chartDrawValues = true
                        chartXAxisCenterLabels = false

                        chartLeftAxisForceLabels = false
                        chartDataSetWidth = 0.5f
                        //label和entry的数量相等, 那么Label就会在Entry下面绘制
                        chartXAxisLabelCount = 6
                        //左右预览一半BarWidth的空隙
                        barFitBars = true

                        chartScaleXEnabled = false
                        chartDragXEnabled = true

                        chartLegendOrientation = Legend.LegendOrientation.VERTICAL

                        val entryCount = 30

                        chartScaleMinimumX =
                            max(entryCount, chartXAxisLabelCount) * 1f / chartXAxisLabelCount

                        val labels = mutableListOf<String>()
                        for (i in 0 until entryCount) {
                            for (j in 0 until 1) {
                                val value = value(4, 20)
                                addEntry(i.toFloat(), value.toFloat())
                                labels.add("$i 级")
                            }
                            chartDataSetColors = colors()
                            chartXAxisValueFormatter = ArrayFormatter(labels)
                            addDataSet("B$i")
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