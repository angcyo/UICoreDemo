package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.chart.dslLineChart
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex.randomColor
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.test.MyMarkerView
import com.github.mikephil.charting.data.LineDataSet
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
                            addLineEntry(1f + i * nextInt(0, 3), 1f + i * nextInt(0, 3))
                            addLineEntry(2f + i * nextInt(0, 3), 2f + i * nextInt(0, 3))
                            addLineEntry(3f + i * nextInt(0, 3), 3f + i * nextInt(0, 3))
                            addLineDataSet("L$i")
                        }

                        addLimitLine(2.5f) {
                            lineColor = Color.RED
                        }

                        addLimitLine(3.5f, "Limit") {
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
                            addLineEntry(1f + i * nextInt(0, 3), 1f + i * nextInt(0, 3))
                            addLineEntry(2f + i * nextInt(0, 3), 2f + i * nextInt(0, 3))
                            addLineEntry(3f + i * nextInt(0, 3), 3f + i * nextInt(0, 3))
                            addLineDataSet("L$i") {
                                color = randomColor()
                            }
                        }

                        addLimitLine(2.5f) {
                            lineColor = Color.RED
                        }

                        addLimitLine(3.5f, "Limit") {
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
                            addLineEntry(1f + i * nextInt(0, 3), 1f + i * nextInt(0, 3))
                            addLineEntry(2f + i * nextInt(0, 3), 2f + i * nextInt(0, 3))
                            addLineEntry(3f + i * nextInt(0, 3), 3f + i * nextInt(0, 3))
                            addLineDataSet("L$i") {
                                color = randomColor()
                            }
                        }

                        addLimitLine(2.5f) {
                            lineColor = Color.RED
                        }

                        addLimitLine(3.5f, "Limit") {
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
                        lineDrawFilled = true

                        for (i in 0..3) {
                            addLineEntry(1f + i * nextInt(0, 3), 1f + i * nextInt(0, 3))
                            addLineEntry(2f + i * nextInt(0, 3), 2f + i * nextInt(0, 3))
                            addLineEntry(3f + i * nextInt(0, 3), 3f + i * nextInt(0, 3))
                            addLineDataSet("L$i") {
                                color = randomColor()
                            }
                        }

                        addLimitLine(2.5f) {
                            lineColor = Color.RED
                        }

                        addLimitLine(3.5f, "Limit") {
                            lineColor = Color.GREEN
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