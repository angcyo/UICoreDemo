package com.angcyo.uicore.component

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.angcyo.uicore.demo.R
import com.angcyo.widget.base.find
import kotlin.math.max

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/11
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class StationLayout(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    companion object {
        //站点开始的位置
        const val STATION_TYPE_START = 1

        //站点的位置
        const val STATION_TYPE_ITEM = 2

        //站点结束的位置
        const val STATION_TYPE_END = 3

        //边缘, 竖向拐角处
        const val STATION_TYPE_EDGE = 4
    }

    var needUpdateStationFlag = false

    var adapter: StationAdapter? = null
        set(value) {
            field = value
            needUpdateStationFlag = true
        }

    init {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (needUpdateStationFlag) {
            updateStation(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
    }

    /**更新站台*/
    fun updateStation(adapter: StationAdapter) {
        this.adapter = adapter
        requestLayout()
    }

    fun updateStation(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val adapter = adapter ?: return

        val measureWidthSize = MeasureSpec.getSize(widthMeasureSpec)
        val measureWidthMode = MeasureSpec.getMode(widthMeasureSpec)
        val measureHeightSize = MeasureSpec.getSize(heightMeasureSpec)
        val measureHeightMode = MeasureSpec.getMode(heightMeasureSpec)

        removeAllViews()

        val stationCount = adapter.getStationCount()

        //当前布局行所剩余的空间
        var lineSpace = measureWidthSize

        //当前行数
        var line = 0

        //var

        val stationLineParamList = mutableListOf<StationLineParam>()
        var stationLineParam = StationLineParam()

        val allStationItemList = mutableListOf<List<StationViewItem>>()
        var stationItemList = mutableListOf<StationViewItem>()

        for (i in 0 until stationCount) {
            if (i == 0) {
                val startView = adapter.createStationView(this, STATION_TYPE_START, i)
                val stationViewItem = StationViewItem(STATION_TYPE_START, line, startView)
                addView(startView)
                measureChild(startView, widthMeasureSpec, heightMeasureSpec)
                lineSpace -= startView.measuredWidth
                adapter.onLayoutStation(stationViewItem)

                stationViewItem.stationWidth = startView.measuredWidth
                stationItemList.add(stationViewItem)
            }

            val itemView = adapter.createStationView(this, STATION_TYPE_ITEM, i)
            addView(itemView)
            measureChild(itemView, widthMeasureSpec, heightMeasureSpec)

            if (lineSpace < itemView.measuredWidth) {
                //放不下
                line++
                stationLineParamList.add(stationLineParam)
                allStationItemList.add(stationItemList)

                lineSpace = measureWidthSize
                stationLineParam = StationLineParam()
                stationItemList = mutableListOf()
            }

            stationLineParam.lineHeightSum =
                max(stationLineParam.lineHeightSum, itemView.measuredHeight)
            stationLineParam.topHeight = max(
                stationLineParam.topHeight,
                itemView.find<View>(R.id.station_line_top_layout)?.measuredHeight ?: 0
            )
            stationLineParam.bottomHeight = max(
                stationLineParam.bottomHeight,
                itemView.find<View>(R.id.station_line_bottom_layout)?.measuredHeight ?: 0
            )
            stationLineParam.lineHeight = max(
                stationLineParam.lineHeight,
                itemView.find<View>(R.id.station_line_layout)?.measuredHeight ?: 0
            )
            val stationViewItem = StationViewItem(STATION_TYPE_START, line, itemView)
            stationViewItem.stationWidth = max(
                itemView.find<View>(R.id.station_line_top_layout)?.measuredWidth ?: 0,
                itemView.find<View>(R.id.station_line_bottom_layout)?.measuredHeight ?: 0
            )
            adapter.onLayoutStation(stationViewItem)
            stationItemList.add(stationViewItem)

            if (i == stationCount - 1) {
                val endView = adapter.createStationView(this, STATION_TYPE_END, i)
                addView(endView)
                measureChild(endView, widthMeasureSpec, heightMeasureSpec)
            }
        }
    }
}

/**记录每一行的站点视图关键数据*/
data class StationLineParam(
    //当前行的总高度
    var lineHeightSum: Int = -1,
    //上面布局的高度
    var topHeight: Int = -1,
    //下面布局的高度
    var bottomHeight: Int = -1,
    //线布局的高度
    var lineHeight: Int = -1
)

data class StationViewItem(
    /**站台视图的类型, 首尾中边缘*/
    val type: Int,
    /**所在的行*/
    val line: Int,
    /**视图*/
    val view: View,
    /**线/站台视图应该被设置的宽度*/
    var stationWidth: Int = -1
)

/**站点适配器*/
abstract class StationAdapter {

    /**返回站点的数量*/
    abstract fun getStationCount(): Int

    /**创建站点的布局*/
    abstract fun createStationView(layout: StationLayout, type: Int, index: Int): View

    /**已经布局了站点, 此时可以处理布局的位置*/
    open fun onLayoutStation(stationViewItem: StationViewItem) {

    }

}