package com.angcyo.uicore.test

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.angcyo.uicore.demo.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
class MyMarkerView(context: Context, layoutResource: Int) : MarkerView(context, layoutResource) {
    val tvContent: TextView

    init {
        tvContent = findViewById(R.id.text_view)
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    override fun refreshContent(entry: Entry, highlight: Highlight) {
        if (entry is CandleEntry) {
            tvContent.text = Utils.formatNumber(
                entry.high,
                0,
                true
            )
        } else {
            tvContent.text = Utils.formatNumber(
                entry.y,
                0,
                true
            )
        }
        super.refreshContent(entry, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF((-(width / 2)).toFloat(), (-height).toFloat())
    }
}