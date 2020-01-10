package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.view.Gravity
import com.angcyo.drawable.dp
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

        _vh.tv(R.id.text_view2)?.text = span {

            text {
                paddingLeft = 10 * dpi
                showText = "angcyo"
                backgroundDrawable = getDrawable(R.mipmap.ic_logo_png)
            }
            text {
                marginLeft = 20 * dpi
                backgroundDrawable = ColorDrawable(Color.RED)
                foregroundDrawable = getDrawable(R.drawable.ic_logo_small)
            }

//            return@span

            append("fg")
            appendln()
            append("fg")
            text("fg") {
                paddingLeft = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            append("fg")
            text("fg") {
                paddingRight = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            text("fg") {
                paddingTop = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            text("fg") {
                paddingBottom = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            text("fg") {
                marginRight = 10 * dpi
                marginLeft = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            text("fg") {
                marginTop = 10 * dpi
                textColor = Color.WHITE
                paddingHorizontal(4 * dpi)
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            text("fg") {
                marginBottom = 10 * dpi
                textColor = Color.WHITE
                paddingVertical(4 * dpi)
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            appendln()
            append("fg")
            text("fg") {
                marginRight = 10 * dpi
                marginLeft = 10 * dpi
                marginTop = 10 * dpi
                marginBottom = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            text("fg.lb") {
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.BOTTOM
            }
            text("fg.rb") {
                backgroundDrawable = ColorDrawable(Color.RED)
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.RIGHT or Gravity.BOTTOM
            }
            text("fg.rt") {
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.RIGHT
            }
            text("fg.lt") {
                backgroundDrawable = ColorDrawable(Color.RED).apply {
                    setBounds(0, 0, 0, 20 * dpi)
                }
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.LEFT
            }
            text("fg.c") {
                backgroundDrawable = getDrawable(R.drawable.app_round_shape)
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.CENTER
            }
            text("abcdefghijklmnopqrstuvwxyz.angcyo")
            text("a") {
                showText = "angcyo"
                offsetY = -10 * dp
            }
            text {
                paddingLeft = 10 * dpi
                showText = "angcyo"
                backgroundDrawable = getDrawable(R.mipmap.ic_logo_png)
            }
            text {
                marginLeft = 20 * dpi
                foregroundDrawable = getDrawable(R.drawable.ic_logo_small)
            }
        }
        _vh.tv(R.id.text_view3)?.text = span {
            text("angcyo") {
                marginLeft = 10 * dpi
                paddingLeft = 10 * dpi

                marginRight = 10 * dpi
                paddingRight = 10 * dpi
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