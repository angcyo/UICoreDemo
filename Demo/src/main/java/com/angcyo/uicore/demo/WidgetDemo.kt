package com.angcyo.uicore.demo

import android.os.Bundle
import android.widget.TextView
import com.angcyo.base.dslAHelper
import com.angcyo.base.lightStatusBar
import com.angcyo.drawable.loading.PathLoadingDrawable
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.toastQQ
import com.angcyo.putData
import com.angcyo.uicore.activity.ShortcutActivity
import com.angcyo.uicore.base.AppTitleFragment
import com.angcyo.uicore.fragment.ShortcutFragment
import com.angcyo.widget._ev
import com.angcyo.widget.base.animatorOf
import com.angcyo.widget.base.clickIt
import com.angcyo.widget.base.fullscreen
import com.angcyo.widget.base.lowProfile
import com.angcyo.widget.progress.ArcLoadingView
import com.angcyo.widget.progress.DslSeekBar
import com.angcyo.widget.progress.HSProgressView
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

        //path loading
        val pathLoadingDrawable = PathLoadingDrawable()
        _vh.view(R.id.path_loading_view)?.background = pathLoadingDrawable
        _vh.v<DslSeekBar>(R.id.path_loading_seek_bar)?.config {
            onSeekChanged = { value, fraction, fromUser ->
                pathLoadingDrawable.progress = value
            }
        }

        //hint text
        _vh._ev(R.id.hint_edit_1)?.editDelegate?.inputHitTipTextList?.apply {
            add("188888888")
            add("188822888")
        }

        _vh._ev(R.id.hint_edit_2)?.editDelegate?.inputHitTipTextList?.apply {
            add("188888888")
            add("188822888")
            add("182822888")
            add("28888282")
            add("22888282")
            add("33888282")
        }

        //spinner
        _vh.spinner(R.id.spinner)
            ?.setStrings(listOf("angcyo1", "angcyo2", "测试文本1", "测试文本2", "22222", "333333333"))
        _vh.spinner(R.id.spinner2)
            ?.setStrings(listOf("angcyo1", "angcyo2", "测试文本1", "测试文本2", "22222", "333333333"))

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

        //---

        _vh.v<DslSeekBar>(R.id.seek_bar)?.config {
            onSeekChanged = { value, _, _ ->
                _vh.v<ArcLoadingView>(R.id.arc_load_view)?.run {
                    endLoading()
                    progress = value
                }
            }
        }

        _vh.click(R.id.button_start) {
            _vh.v<ArcLoadingView>(R.id.arc_load_view)?.startLoading()
        }

        //baseViewHolder.group(R.id.wrap_layout).helper()

        _vh.click(R.id.button_full) {
            _vh.itemView.fullscreen(true)
        }

        _vh.click(R.id.button_not_full) {
            _vh.itemView.fullscreen(false)
        }

        _vh.click(R.id.button_low) {
            _vh.itemView.lowProfile(true)
        }

        _vh.click(R.id.button_not_low) {
            _vh.itemView.lowProfile(false)
        }

        _vh.click(R.id.light_button) {
            val light = !it.isSelected
            activity?.lightStatusBar(light)
            it.isSelected = light

            animatorOf(fContext(), R.animator.lib_translate_x_show_enter_animator)?.apply {
                setTarget(it)
                start()
            }
        }

        //hs progress
        _vh.selectorClick(R.id.start_hs) {
            if (it) {
                _vh.v<HSProgressView>(R.id.hs_progress_view)?.startAnimator()
            } else {
                _vh.v<HSProgressView>(R.id.hs_progress_view)?.stopAnimator()
            }
            false
        }

        //跳板测试
        _vh.click(R.id.jump) {
            dslAHelper {
                start(ShortcutActivity::class.java) {
                    useJumpActivity = true
                }
            }
        }
        _vh.click(R.id.jump_fragment) {
            dslAHelper {
                start(ShortcutFragment::class.java, false) {
                    intent.putData(nowTimeString())
                }
            }
        }
        _vh.click(R.id.jump_fragment_single_task) {
            dslAHelper {
                start(ShortcutFragment::class.java, true) {
                    intent.putData(nowTimeString())
                }
            }
        }

        //max line
        _vh.tv(R.id.text_max_line_view)?.text = span {
            for (i in 0..10) {
                drawable {
                    foregroundDrawable = _drawable(R.drawable.ic_logo_small)
                }
                drawable("angcyo")
                drawable(" 跑马灯滚动起来")
                text("fgabcdefghijklmnopqrstuvwxyz.angcyo") {
                    textBold = true
                }
            }
        }

        //max line
        _vh.tv(R.id.text_max_line_view1)?.setText(span {
            for (i in 0..10) {
                drawable {
                    foregroundDrawable = _drawable(R.drawable.ic_logo_small)
                }
                drawable("angcyo")
                drawable(" 跑马灯滚动起来")
                text("fgabcdefghijklmnopqrstuvwxyz.angcyo") {
                    textBold = true
                }
            }
        }, TextView.BufferType.EDITABLE)

        //max line
        _vh.tv(R.id.text_max_line_view2)?.apply {
            text = span {
                for (i in 0..10) {
                    drawable {
                        foregroundDrawable = _drawable(R.drawable.ic_logo_small)
                    }
                    drawable("angcyo")
                    drawable(" 跑马灯滚动起来")
                    text("fgabcdefghijklmnopqrstuvwxyz.angcyo") {
                        textBold = true
                    }
                }
            }
            clickIt {
                toastQQ("click it ${nowTimeString()}")
            }
        }

        //max line
        _vh.tv(R.id.text_max_line_view3)?.text = """
            很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试1
            很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试2
            很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试3
            很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试4
            很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试5
            很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试6
            很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试7
            很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试8
            很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试,很长的文本测试9
        """.trimIndent()
    }
}