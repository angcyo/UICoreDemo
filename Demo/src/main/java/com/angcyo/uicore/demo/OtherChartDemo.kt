package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import com.angcyo.chart.*
import com.angcyo.chart.formatter.ArrayFormatter
import com.angcyo.dsladapter.renderEmptyItem
import com.angcyo.dsladapter.renderItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.colors
import com.angcyo.uicore.test.RadarMarkerView
import com.angcyo.uicore.value
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.MarkerView

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/10
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class OtherChartDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            //1 CombinedChart
            renderItem {
                itemLayoutId = R.layout.demo_combined_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslCombinedChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartDataSetColors = colors()
                        chartDesText = "CombinedChart 合并图表"

                        chartLegendOrientation = Legend.LegendOrientation.VERTICAL

                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000

                        for (i in 0..1) {
                            for (j in 0..10) {
                                addLineEntry(j.toFloat(), value().toFloat())
                                addBarEntry(j.toFloat(), value().toFloat())
                                addScatterEntry(j.toFloat(), value().toFloat())
                                scatterShape = when (i) {
                                    0 -> ScatterChart.ScatterShape.CIRCLE
                                    1 -> ScatterChart.ScatterShape.CHEVRON_DOWN
                                    2 -> ScatterChart.ScatterShape.CHEVRON_UP
                                    3 -> ScatterChart.ScatterShape.CROSS
                                    4 -> ScatterChart.ScatterShape.SQUARE
                                    5 -> ScatterChart.ScatterShape.TRIANGLE
                                    else -> ScatterChart.ScatterShape.X
                                }

                                addBubbleEntry(
                                    j.toFloat(),
                                    value().toFloat(),
                                    value(20, 100).toFloat()
                                )

                                val v = value().toFloat()
                                val open = value(10, 60).toFloat()
                                val close = value(10, 60).toFloat()
                                val even = j % 2 == 0
                                addCandleEntry(
                                    (j).toFloat() + i * 10,
                                    (v + value(60, 100)),
                                    (v - value(60, 100)),
                                    if (even) v + open else v - open,
                                    if (even) v - close else v + close
                                )
                            }
                            addLineDataSet("L$i")
                            addBarDataSet("B$i")
                            addScatterDataSet("S$i")
                            addCandleDataSet("C$i")
                            addBubbleDataSet("Q$i")
                        }
                    }
                }
            }

            //1.1 CombinedChart
            renderItem {
                itemLayoutId = R.layout.demo_combined_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslCombinedChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartDataSetColors = colors()
                        chartDesText = "CombinedChart 合并图表"

                        chartLegendOrientation = Legend.LegendOrientation.VERTICAL

                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000

                        chartDesPositionGravity = Gravity.CENTER

                        for (i in 0..1) {
                            for (j in 0..3) {
                                addLineEntry(j.toFloat(), value().toFloat())
                                addBarEntry(j.toFloat(), value().toFloat())
                                addScatterEntry(j.toFloat(), value().toFloat())
                            }
                            addLineDataSet("L$i")
                            addBarDataSet("B$i")
                            addScatterDataSet("S$i")
                        }
                    }
                }
            }

            //1.2 CombinedChart
            renderItem {
                itemLayoutId = R.layout.demo_combined_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslCombinedChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartDataSetColors = colors()
                        chartDesText = "CombinedChart 合并图表"
                        chartDesTextColor = Color.RED

                        chartLegendOrientation = Legend.LegendOrientation.VERTICAL

                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000

                        chartDesPositionGravity = Gravity.LEFT or Gravity.BOTTOM

                        for (i in 0..1) {
                            for (j in 0..3) {
                                addLineEntry(j.toFloat(), value().toFloat())
                                addScatterEntry(j.toFloat(), value().toFloat())
                            }
                            addLineDataSet("L$i")
                            addScatterDataSet("S$i")
                        }
                    }
                }
            }

            //2 ScatterChart
            renderItem {
                itemLayoutId = R.layout.demo_scatter_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslScatterChart(itemHolder.v(R.id.chart)) {
                        chartDesText = "ScatterChart 散列图表"
                        chartDesPositionGravity = Gravity.LEFT
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000
                        for (i in 0..6) {
                            for (j in 0..10) {
                                addEntry((j).toFloat() + i, value().toFloat())
                            }
                            chartDataSetColors = colors()
                            scatterShape = when (i) {
                                0 -> ScatterChart.ScatterShape.CIRCLE
                                1 -> ScatterChart.ScatterShape.CHEVRON_DOWN
                                2 -> ScatterChart.ScatterShape.CHEVRON_UP
                                3 -> ScatterChart.ScatterShape.CROSS
                                4 -> ScatterChart.ScatterShape.SQUARE
                                5 -> ScatterChart.ScatterShape.TRIANGLE
                                else -> ScatterChart.ScatterShape.X
                            }
                            addDataSet("S$i")
                        }
                    }
                }
            }

            //3 CandleStickChart
            renderItem {
                itemLayoutId = R.layout.demo_candle_stick_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslCandleStickChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartDesText = "CandleStickChart 蜡烛图表"
                        chartDesPositionGravity = Gravity.RIGHT
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000
                        for (i in 0..0) {
                            for (j in 0..10) {
                                val v = value().toFloat()
                                val open = value(10, 60).toFloat()
                                val close = value(10, 60).toFloat()

                                val even = j % 2 == 0

                                addEntry(
                                    (j).toFloat() + i * 10,
                                    (v + value(60, 100)),
                                    (v - value(60, 100)),
                                    if (even) v + open else v - open,
                                    if (even) v - close else v + close
                                )
                            }
                            chartDataSetColors = colors()

                            addDataSet("S$i")
                        }
                    }
                }
            }

            //4 BubbleChart
            renderItem {
                itemLayoutId = R.layout.demo_bubble_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslBubbleChart(itemHolder.v(R.id.chart)) {
                        chartDrawValues = true
                        chartDataSetColors = colors()
                        chartDesText = "BubbleChart 气泡图表"
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000
                        chartDesPositionGravity = Gravity.CENTER_HORIZONTAL or Gravity.TOP
                        chartDesTextColor = Color.YELLOW
                        for (i in 0..2) {
                            for (j in 0..10) {
                                val v = value().toFloat()

                                addEntry(
                                    (j).toFloat(),
                                    v * (i + 0.5f),
                                    value(20, 100).toFloat()
                                )
                            }
                            chartDataSetColors = colors()

                            addDataSet("B$i")
                        }
                    }
                }
            }

            //5 RadarChart
            renderItem {
                itemLayoutId = R.layout.demo_radar_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslRadarChart(itemHolder.v(R.id.chart)) {
                        chartDataSetColors = colors()
                        chartDesText = "RadarChart 雷达图表"
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000
                        chartDrawFilled = true

                        chartDesPositionGravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM

                        val values = mutableListOf<String>()
                        chartXAxisValueFormatter = ArrayFormatter(values)

                        for (i in 0..2) {
                            values.clear()
                            for (j in 0..5) {
                                val v = value().toFloat()
                                addEntry(value = v)
                                values.add("值$j")
                            }

                            chartDataSetColors = colors()
                            chartFillColor = chartDataSetColors.first()

                            addDataSet("B$i")
                        }
                    }?.apply {
                        // create a custom MarkerView (extend MarkerView) and specify the layout
                        // to use for it
                        val mv: MarkerView = RadarMarkerView(fContext(), R.layout.radar_markerview)
                        mv.chartView = this // For bounds control
                        marker = mv // Set the marker to the chart
                    }
                }
            }

            renderEmptyItem()
        }
    }
}