package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import com.angcyo.chart.dslBubbleChart
import com.angcyo.chart.dslCandleStickChart
import com.angcyo.chart.dslRadarChart
import com.angcyo.chart.dslScatterChart
import com.angcyo.chart.formatter.ArrayFormatter
import com.angcyo.dsladapter.renderEmptyItem
import com.angcyo.dsladapter.renderItem
import com.angcyo.library._screenWidth
import com.angcyo.library.ex.dp
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.colors
import com.angcyo.uicore.test.RadarMarkerView
import com.angcyo.uicore.value
import com.github.mikephil.charting.charts.ScatterChart
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
//                    dslBarChart(itemHolder.v(R.id.chart)) {
//                        chartDrawValues = true
//                        chartDataSetColors = colors()
//                    }
                }
            }

            //2 ScatterChart
            renderItem {
                itemLayoutId = R.layout.demo_scatter_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslScatterChart(itemHolder.v(R.id.chart)) {
                        chartDesText = "ScatterChart 散列图表"
                        chartDesPositionX = 24f * dp
                        chartDesPositionY = 24f * dp
                        chartDesTextAlign = Paint.Align.LEFT
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
                        chartDesPositionY = 18f * dp
                        chartDesPositionX = _screenWidth.toFloat() - chartDesPositionY
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
                        radarDrawFill = true

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
                            radarFillColor = chartDataSetColors.first()

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