package com.angcyo.uicore.component

import android.graphics.Bitmap
import android.widget.TextView
import com.angcyo.library.ex._color
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.find
import com.angcyo.library.ex.nowTimeString
import com.angcyo.media.video.record.WaterMarkerRecordVideoCallback
import com.angcyo.uicore.demo.R
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/24
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class AppWaterMarkerRecordVideoCallback : WaterMarkerRecordVideoCallback() {
    init {
        waterLayoutId = R.layout.layout_water_marker
        waterLayoutInit = {
            it.find<TextView>(R.id.lib_text_view)?.text = span {
                append("强大的水印")
                drawable {
                    backgroundDrawable = _drawable(R.drawable.ic_logo_small)
                }
                appendln()
                append(nowTimeString()) {
                    foregroundColor = _color(R.color.text_sub_color)
                }
            }
        }
    }

    override fun onTakePhotoBefore(photo: Bitmap, width: Int, height: Int): Bitmap {
        return super.onTakePhotoBefore(photo, width, height)
    }
}