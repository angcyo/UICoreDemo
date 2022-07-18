package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.view.Gravity
import com.angcyo.library.ex.*
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.widget.base.clickIt
import com.angcyo.widget.progress.ArcLoadingView
import com.angcyo.widget.progress.DslSeekBar
import com.angcyo.widget.span.SpanClickMethod
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/02
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class DrawableSpanDemo : AppTitleFragment() {

    init {
        contentLayoutId = R.layout.demo_drawable_span
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        //scroll
        _vh.tv(R.id.scroll_text_view)?.text = span {
            drawable {
                foregroundDrawable = _drawable(R.drawable.ic_logo_small)
            }
            drawable("angcyo")
            drawable(" 跑马灯滚动起来")
            text("fgabcdefghijklmnopqrstuvwxyz.angcyo") {
                textBold = true
            }
        }

        //span
        SpanClickMethod.install(_vh.tv(R.id.text_view2))
        _vh.tv(R.id.text_view2)?.text = span {
            append("fg")

            drawable {
                showText = "angcyo"
                spanWeight = 0.5f
                backgroundDrawable = _drawable(R.mipmap.ic_logo_png)
            }
            append("fg")
            appendln()
            text("fg")
            text("fgabcdefghijklmnopqrstuvwxyz.angcyo abcdefghijklmnopqrstuvwxyz.angcyo") {
                textBold = true
                textItalic = true
                leadingWeight(0.3f)
            }

//            return@span

            appendln()
            append(" label: ")
            append("abcdefghijklmnopqrstuvwxyz.angcyo abcdefghijklmnopqrstuvwxyz.angcyo")
            drawable(" label: ")
            drawable("abcdefghijklmnopqrstuvwxyz.angcyo\n abcdefghijklmnopqrstuvwxyz.angcyo")
            drawable("fg") { }
            drawable {
                paddingLeft = 10 * dpi
                showText = "angcyo"
                spanWeight = 0.5f
                backgroundDrawable = _drawable(R.mipmap.ic_logo_png)
            }
            drawable("fg") {
                textGravity = Gravity.TOP
                gradientStrokeColor = Color.RED
            }
            drawable("fg") {
                paddingVertical(2 * dpi)
                paddingHorizontal(4 * dpi)
                textGravity = Gravity.CENTER
                gradientSolidColor = Color.RED
            }
            drawable("g") {
                textGravity = Gravity.CENTER
                widthSameHeight = true
                paddingBottom = 4 * dpi
                gradientSolidColor = Color.RED
                gradientStrokeColor = Color.YELLOW
            }
            drawable {
                marginLeft = 20 * dpi
                backgroundDrawable = ColorDrawable(Color.RED)
                foregroundDrawable = _drawable(R.drawable.ic_logo_small)
            }

//            return@span

            append("fg")
            appendln()
            append("fg")
            drawable("fg") {
                paddingLeft = 10 * dpi
                textColor = Color.WHITE
                textGravity = Gravity.CENTER
                spanWeight = 0.5f
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            append("fg")
            drawable("fg") {
                paddingRight = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            drawable("fg") {
                paddingTop = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            drawable("fg") {
                paddingBottom = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            drawable("fg") {
                marginRight = 10 * dpi
                marginLeft = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            drawable("fg") {
                marginTop = 10 * dpi
                textColor = Color.WHITE
                paddingHorizontal(4 * dpi)
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            drawable("fg") {
                marginBottom = 10 * dpi
                textColor = Color.WHITE
                paddingVertical(4 * dpi)
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            appendln()
            append("fg")
            drawable("fg") {
                marginRight = 10 * dpi
                marginLeft = 10 * dpi
                marginTop = 10 * dpi
                marginBottom = 10 * dpi
                textColor = Color.WHITE
                backgroundDrawable = ColorDrawable(Color.RED)
            }
            drawable("fg.lb") {
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.BOTTOM
            }
            drawable("fg.rb") {
                backgroundDrawable = ColorDrawable(Color.RED)
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.RIGHT or Gravity.BOTTOM
            }
            drawable("fg.rt") {
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.RIGHT
            }
            drawable("fg.lt") {
                backgroundDrawable = ColorDrawable(Color.RED).apply {
                    setBounds(0, 0, 0, 20 * dpi)
                }
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.LEFT
            }
            drawable("fg.c") {
                backgroundDrawable = _drawable(R.drawable.app_round_shape)
                spanWidth = 80 * dpi
                spanHeight = 80 * dpi
                textGravity = Gravity.CENTER
            }
            drawable("abcdefghijklmnopqrstuvwxyz.angcyo")
            drawable("a") {
                showText = "angcyo"
                offsetY = -10 * dp
            }
            drawable {
                paddingLeft = 10 * dpi
                showText = "angcyo"
                backgroundDrawable = _drawable(R.mipmap.ic_logo_png)
            }
            drawable {
                marginLeft = 20 * dpi
                foregroundDrawable = _drawable(R.drawable.ic_logo_small)
            }
        }
        _vh.tv(R.id.text_view3)?.text = span {

            drawable {
                backgroundDrawable =
                    _drawable(R.drawable.lib_back).colorFilter(getColor(R.color.text_general_color))
            }
            drawable("返回") {
                textGravity = Gravity.CENTER
                marginLeft = -8 * dpi
            }
        }

        _vh.tv(R.id.text_view3)?.clickIt {
            it.isSelected = !it.isSelected
        }

        _vh.tv(R.id.text_view4)?.text = span {
            drawable {
                foregroundDrawable = _drawable(R.drawable.logo_selector)
            }
            appendln()
            drawable("checked")
        }

        _vh.tv(R.id.text_view4)?.clickIt {
            it.isSelected = !it.isSelected
        }

        //text

        _vh.tv(R.id.lib_text_view)?.text = span {
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
            appendImage(_drawable(R.mipmap.ic_logo_png))
            append("a") {
                fontSize = 16 * dpi
            }
            appendImage(_drawable(R.drawable.ic_logo_small))
            append("A")
            appendImage(_drawable(R.drawable.ic_logo_small), 2)
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

        //baseViewHolder.group(R.id.lib_wrap_layout).helper()
    }
}