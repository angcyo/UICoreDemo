package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.library.ex.getDrawable
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.widget.progress.ArcLoadingView
import com.angcyo.widget.progress.DslSeekBar
import com.angcyo.widget.span.span
import com.angcyo.widget.spinner

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

        //spinner
        _vh.spinner(R.id.spinner)
            ?.setStrings(listOf("angcyo1", "angcyo2", "测试文本1", "测试文本2", "22222", "333333333"))
        _vh.spinner(R.id.spinner2)
            ?.setStrings(listOf("angcyo1", "angcyo2", "测试文本1", "测试文本2", "22222", "333333333"))

        //scroll
        _vh.tv(R.id.scroll_text_view)?.text = span {
            drawable {
                foregroundDrawable = getDrawable(R.drawable.ic_logo_small)
            }
            drawable("angcyo")
            drawable(" 跑马灯滚动起来")
            text("fgabcdefghijklmnopqrstuvwxyz.angcyo") {
                textBold = true
            }
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