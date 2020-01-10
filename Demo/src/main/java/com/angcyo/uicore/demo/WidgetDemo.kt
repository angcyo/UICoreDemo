package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import com.angcyo.drawable.dpi
import com.angcyo.drawable.getDrawable
import com.angcyo.drawable.toDpi
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.widget.progress.ArcLoadingView
import com.angcyo.widget.progress.DslSeekBar
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class WidgetDemo : AppTitleFragment() {

    init {
        contentLayoutId = R.layout.demo_widget
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        //span

        _vh.tv(R.id.text_view2)?.text = "angcyo"
        _vh.tv(R.id.text_view3)?.text = span {
            appendText("angcyo") {

            }
        }

        //text

        _vh.tv(R.id.text_view)?.text = span {
            append("\t测试") {
                foregroundColor = Color.WHITE
                tabStopOffset = 20 * dpi
            }
            append("angcyo") {
                quoteColor = Color.RED
            }
            append("angcyo") {
                isSubscript = true
            }
            append("angcyo") {
                isSuperscript = true
            }
            append(
                "ang\ncyo",
                LeadingMarginSpan.Standard(10.toDpi(), 10.toDpi()),
                ForegroundColorSpan(Color.YELLOW)
            )
            appendln()

            append("测试") {
                underline = true
                deleteLine = true
                relativeSizeScale = 0.5f
            }
            appendSpace(10 * dpi)
            append("测试") {
                underline = true
                deleteLine = true
                relativeSizeScale = 2.5f
            }
            appendSpace(30 * dpi)
            appendSpace(30 * dpi, Color.GREEN)
            appendSpace(30 * dpi)
            append("测试") {
                foregroundColor = Color.WHITE
                backgroundColor = Color.RED
                lineBackgroundColor = Color.BLUE
                underline = true
                deleteLine = true
                scaleX = 2f
            }
            appendln()
            appendSpace(30 * dpi, Color.GREEN)
            append("测试") {
                relativeSizeScale = 1.5f
                foregroundColor = Color.WHITE
                backgroundColor = Color.RED
                lineBackgroundColor = Color.BLUE
                underline = true
                deleteLine = true
                scaleX = 2f
            }
            appendln()
            append("angcyo") {
                quoteGapLeftWidth = 2 * dpi
                quoteColor = Color.RED
            }
            appendImage(getDrawable(R.mipmap.ic_logo_png))
            append("a") {
                fontSize = 16 * dpi
            }
            appendImage(getDrawable(R.drawable.ic_logo_small))
            append("A")
            appendImage(getDrawable(R.drawable.ic_logo_small), 2)
        }

        //---

        _vh.v<DslSeekBar>(R.id.seek_bar)?.config {
            onSeekChanged = { value, _, _ ->
                _vh.v<ArcLoadingView>(R.id.arc_load_view)?.run {
                    endLoading()
                    progress = value
                }
            }
        }

        _vh.click(R.id.button) {
            _vh.v<ArcLoadingView>(R.id.arc_load_view)?.startLoading()
        }

        //baseViewHolder.group(R.id.wrap_layout).helper()
    }
}