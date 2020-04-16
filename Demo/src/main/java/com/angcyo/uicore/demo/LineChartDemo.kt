package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.chart.dslLineChart
import com.angcyo.coroutine.onBack
import com.angcyo.coroutine.sleep
import com.angcyo.dsladapter.isItemAttached
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex.randomColor
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.colors
import com.angcyo.uicore.test.MyMarkerView
import com.angcyo.uicore.value
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlin.math.max
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/07
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class LineChartDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {

            //1
            renderItem {
                itemLayoutId = R.layout.demo_line_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslLineChart(itemHolder.v(R.id.chart)) {
                        chartXAxisLabelTextColor = Color.RED
                        chartDesText = "(h)"
                        chartDesEnabled = true
                        lineMode = LineDataSet.Mode.LINEAR

                        for (i in 0..3) {
                            addEntry(1f + i * nextInt(0, 3), 1f + i * nextInt(0, 3))
                            addEntry(2f + i * nextInt(0, 3), 2f + i * nextInt(0, 3))
                            addEntry(3f + i * nextInt(0, 3), 3f + i * nextInt(0, 3))
                            addDataSet("L$i")
                        }

                        addXAxisLimitLine(2.5f) {
                            lineColor = Color.RED
                        }

                        addXAxisLimitLine(3.5f, "Limit") {
                            lineColor = Color.GREEN
                        }
                    }?.apply {
                        description.setPosition(
                            viewPortHandler.contentLeft(),
                            viewPortHandler.contentTop()
                        )

                        //动画
                        animateXY(600, 600)
                    }
                }
            }

            //2
            renderItem {
                itemLayoutId = R.layout.demo_line_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslLineChart(itemHolder.v(R.id.chart)) {
                        chartXAxisEnable = false
                        lineMode = LineDataSet.Mode.CUBIC_BEZIER
                        chartDrawValues = true
                        chartHighlightEnabled = true

                        //X轴, 网格(竖线)颜色
                        chartXAxisGridLineColor = randomColor()
                        //左边Y轴, 网格(横线)颜色
                        chartLeftAxisGridLineColor = randomColor()
                        //左边Y轴, 轴线颜色
                        chartLeftAxisLineColor = randomColor()

                        for (i in 0..3) {
                            addEntry(1f + i * nextInt(0, 3), 1f + i * nextInt(0, 3))
                            addEntry(2f + i * nextInt(0, 3), 2f + i * nextInt(0, 3))
                            addEntry(3f + i * nextInt(0, 3), 3f + i * nextInt(0, 3))
                            addDataSet("L$i") {
                                color = randomColor()
                            }
                        }

                        addXAxisLimitLine(2.5f) {
                            lineColor = Color.RED
                        }

                        addXAxisLimitLine(3.5f, "Limit") {
                            lineColor = Color.GREEN
                        }
                    }?.apply {
                        // create marker to display box when values are selected
                        // create marker to display box when values are selected
                        val mv = MyMarkerView(fContext(), R.layout.custom_marker_view)
                        // Set the marker to the chart

                        // Set the marker to the chart
                        mv.chartView = this
                        marker = mv

                        animateXY(600, 600)
                    }
                }
            }

            //3
            renderItem {
                itemLayoutId = R.layout.demo_line_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslLineChart(itemHolder.v(R.id.chart)) {
                        lineMode = LineDataSet.Mode.STEPPED
                        chartXAxisEnable = false
                        chartDrawValues = true
                        chartHighlightEnabled = true
                        lineDrawCircleEnable = false

                        for (i in 0..3) {
                            addEntry(1f + i * nextInt(0, 3), 1f + i * nextInt(0, 3))
                            addEntry(2f + i * nextInt(0, 3), 2f + i * nextInt(0, 3))
                            addEntry(3f + i * nextInt(0, 3), 3f + i * nextInt(0, 3))
                            addDataSet("L$i") {
                                color = randomColor()
                            }
                        }

                        addXAxisLimitLine(2.5f) {
                            lineColor = Color.RED
                        }

                        addXAxisLimitLine(3.5f, "Limit") {
                            lineColor = Color.GREEN
                        }
                    }?.apply {
                        animateXY(600, 600)
                    }
                }
            }

            //4
            renderItem {
                itemLayoutId = R.layout.demo_line_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslLineChart(itemHolder.v(R.id.chart)) {
                        lineMode = LineDataSet.Mode.HORIZONTAL_BEZIER
                        chartXAxisEnable = false
                        chartDrawValues = true
                        lineDrawCircleEnable = false
                        chartDrawFilled = true
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000

                        for (i in 0..3) {
                            addEntry(1f + i * nextInt(0, 3), 1f + i * nextInt(0, 3))
                            addEntry(2f + i * nextInt(0, 3), 2f + i * nextInt(0, 3))
                            addEntry(3f + i * nextInt(0, 3), 3f + i * nextInt(0, 3))
                            addDataSet("L$i") {
                                color = randomColor()
                            }
                        }

                        addXAxisLimitLine(2.5f) {
                            lineColor = Color.RED
                        }

                        addXAxisLimitLine(3.5f, "Limit") {
                            lineColor = Color.GREEN
                        }
                    }?.moveViewToX(-2f)
                }
            }

            //5 Label 和 Value 一一对应
            renderItem {
                itemLayoutId = R.layout.demo_line_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslLineChart(itemHolder.v(R.id.chart)) {
                        lineMode = LineDataSet.Mode.HORIZONTAL_BEZIER
                        chartDrawValues = true
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000

                        chartScaleXEnabled = false
                        chartDragXEnabled = true
                        chartXAxisLabelCount = 6
                        val valueCount = 30
                        chartScaleMinimumX =
                            max(valueCount, chartXAxisLabelCount) * 1f / chartXAxisLabelCount

                        for (i in 0..2) {
                            for (j in 0 until valueCount) {
                                addEntry(j.toFloat(), value(2, 10).toFloat())
                            }
                            chartDataSetColors = colors()
                            addDataSet("L$i")
                        }
                    }?.moveViewToX(2f)
                }
            }

            //6
            renderItem {
                itemLayoutId = R.layout.demo_line_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslLineChart(itemHolder.v(R.id.chart)) {
                        lineMode = LineDataSet.Mode.HORIZONTAL_BEZIER
                        chartDrawValues = true
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000

                        chartScaleXEnabled = false
                        chartDragXEnabled = true
                        chartXAxisLabelCount = 6
                        val valueCount = 3
                        chartScaleMinimumX =
                            max(valueCount, chartXAxisLabelCount) * 1f / chartXAxisLabelCount

                        chartExtraOffsetLeft = 10f

                        for (i in 0..2) {
                            for (j in 0 until valueCount) {
                                addEntry(j.toFloat(), value(2, 10).toFloat())
                            }

                            //补位
                            for (j in valueCount..chartXAxisLabelCount) {
                                addEntry(j.toFloat(), 0f) {
                                    isDrawValue = false
                                }
                            }

                            chartDataSetColors = colors()
                            addDataSet("L$i")
                        }
                    }?.moveViewTo(3f, 3f, YAxis.AxisDependency.LEFT)
                }
            }

            //7 实时
            renderItem {
                var lineData: LineData? = null
                itemLayoutId = R.layout.demo_line_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    val valueCount = itemData?.toString()?.toIntOrNull() ?: 2
                    fun initChartData() {
                        dslLineChart(itemHolder.v(R.id.chart)) {
                            lineMode = LineDataSet.Mode.CUBIC_BEZIER
                            chartDrawValues = true

                            chartScaleXEnabled = false
                            chartDragXEnabled = false

                            chartLegendEnable = false

                            chartMaxVisibleCount = 60

                            //chartPinchZoomEnabled = true

                            for (i in 0..0) {
                                for (j in 0 until valueCount) {
                                    addEntry(Entry(j.toFloat(), value(2, 10).toFloat()))
                                }
                                chartDataSetColors = colors()
                                addDataSet("L$i")
                            }
                        }?.apply {
                            lineData = data
                        }
                    }
                    initChartData()

                    //循环添加数据
                    launchLifecycle {
                        onBack {
                            while (isItemAttached()) {
                                val count = itemData?.toString()?.toIntOrNull() ?: 2
                                itemData = count + 1

                                lineData?.addEntry(
                                    Entry(
                                        (count + 1).toFloat(),
                                        value(2, 10).toFloat()
                                    ), 0
                                )

                                itemHolder.v<LineChart>(R.id.chart)?.apply {
                                    data = lineData
                                    setVisibleXRangeMaximum(60f)
                                    moveViewToX(data.entryCount.toFloat())
                                }

                                sleep(60)
                            }
                        }
                    }
                }
            }

            //no data
            renderItem {
                itemLayoutId = R.layout.demo_line_chart

                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslLineChart(itemHolder.v(R.id.chart))
                }
            }
        }
    }
}