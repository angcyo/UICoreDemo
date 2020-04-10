package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import com.angcyo.chart.BaseChartConfig
import com.angcyo.chart.dslPieChart
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex._color
import com.angcyo.library.ex._drawable
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.colors
import com.angcyo.widget.span.span
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieDataSet
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
                        pieUsePercentValues = true
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
                        chartDataSetColors = colors()
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000
                        pieUsePercentValues = false
                        pieDrawRoundedSlices = true
                        chartLegendEnable = true
                        chartLegendHorizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                        chartLegendVerticalAlignment = Legend.LegendVerticalAlignment.TOP
                        chartLegendOrientation = Legend.LegendOrientation.VERTICAL
                        pieCenterText = span {
                            append("span...") {
                                foregroundColor = ColorTemplate.getHoloBlue()
                            }
                            drawable {
                                backgroundDrawable = _drawable(R.drawable.ic_logo)
                            }
                        }
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

            //3
            renderItem {
                itemLayoutId = R.layout.demo_pie_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslPieChart(itemHolder.v(R.id.chart)) {
                        chartDataSetColors = colors()
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000
                        pieUsePercentValues = false
                        pieDrawRoundedSlices = true
                        pieValuePositionY = PieDataSet.ValuePosition.OUTSIDE_SLICE
                        chartLegendEnable = true
                        chartLegendHorizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
                        chartLegendVerticalAlignment = Legend.LegendVerticalAlignment.CENTER
                        chartLegendOrientation = Legend.LegendOrientation.VERTICAL
                        pieCenterText = span {
                            append("span...") {
                                foregroundColor = ColorTemplate.getHoloBlue()
                            }
                            drawable {
                                backgroundDrawable = _drawable(R.drawable.ic_logo)
                            }
                            text("text") {
                                textColor = BaseChartConfig.DEFAULT_TEXT_COLOR
                            }
                        }
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

            //4
            renderItem {
                itemLayoutId = R.layout.demo_pie_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslPieChart(itemHolder.v(R.id.chart)) {
                        chartDataSetColors = colors()
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000
                        pieUsePercentValues = false
                        pieDrawRoundedSlices = true
                        pieValuePositionY = PieDataSet.ValuePosition.OUTSIDE_SLICE
                        chartLegendEnable = true
                        chartLegendHorizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        chartLegendVerticalAlignment = Legend.LegendVerticalAlignment.TOP
                        chartLegendOrientation = Legend.LegendOrientation.HORIZONTAL
                        chartLegendDirection = Legend.LegendDirection.RIGHT_TO_LEFT
                        pieTransparentCircleColor = _color(R.color.info)
                        pieCenterText = span {
                            append("span...") {
                                foregroundColor = ColorTemplate.getHoloBlue()
                            }
                            text("\ntext") {
                                textColor = BaseChartConfig.DEFAULT_TEXT_COLOR
                            }
                        }
                        pieRotationAngle = 180f
                        pieMaxAngle = 90f
                        pieCenterTextOffsetY = -20f
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

            //5
            renderItem {
                itemLayoutId = R.layout.demo_pie_chart
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    dslPieChart(itemHolder.v(R.id.chart)) {
                        chartDataSetColors = colors()
                        chartAnimateDurationX = 2000
                        chartAnimateDurationY = 2000
                        pieUsePercentValues = false
                        pieDrawRoundedSlices = false
                        pieValuePositionY = PieDataSet.ValuePosition.OUTSIDE_SLICE
                        chartLegendEnable = true
                        chartLegendHorizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                        chartLegendVerticalAlignment = Legend.LegendVerticalAlignment.TOP
                        chartLegendOrientation = Legend.LegendOrientation.HORIZONTAL
                        pieTransparentCircleColor = _color(R.color.info)
                        pieHoleColor = _color(R.color.warning)
                        pieCenterText = span {
                            append("span...") {
                                foregroundColor = ColorTemplate.getHoloBlue()
                            }
                            text("\ntext") {
                                textColor = BaseChartConfig.DEFAULT_TEXT_COLOR
                            }
                        }
                        pieRotationAngle = 180f
                        pieMaxAngle = 180f
                        pieCenterTextOffsetY = -20f
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