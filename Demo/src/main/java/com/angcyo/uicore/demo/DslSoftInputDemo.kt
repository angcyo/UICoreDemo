package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.behavior.placeholder.TitleBarPlaceholderBehavior
import com.angcyo.drawable.toDpi
import com.angcyo.http.base.fullTime
import com.angcyo.http.base.nowTime
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.clickIt
import com.angcyo.widget.base.setBehavior
import com.angcyo.widget.layout.DslSoftInputLayout
import com.angcyo.widget.layout.OnSoftInputListener
import com.angcyo.widget.layout.softAction
import com.angcyo.widget.soft
import com.angcyo.widget.span.span
import com.angcyo.widget.spinner

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/15
 */

open class DslSoftInputDemo : AppDslFragment() {
    init {
        contentLayoutId = R.layout.demo_soft_input_layout
    }

    override fun onInitBehavior() {
        super.onInitBehavior()

        _vh.view(R.id.lib_title_wrap_layout)?.setBehavior(TitleBarPlaceholderBehavior())
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        _vh.soft(R.id.lib_soft_input_layout)?.apply {
            softInputListener.add(object : OnSoftInputListener {
                override fun onSoftInputChangeStart(action: Int, height: Int, oldHeight: Int) {
                    super.onSoftInputChangeStart(action, height, oldHeight)
                    _vh.tv(R.id.tip_text_view)?.append(span {
                        append("->") {
                            foregroundColor = Color.RED
                        }
                        append(action.softAction())
                        appendln()
                    })
                }

                override fun onSoftInputChangeEnd(action: Int, height: Int, oldHeight: Int) {
                    _vh.tv(R.id.tip_text_view)?.append(span {
                        append(nowTime().fullTime("HH:mm:ss.SSS"))
                        append(" from:")
                        append(oldHeight.toString())
                        append(" to:")
                        append(height.toString())
                        append(" ")
                        append(action.softAction())
                        appendln()
                    })
                }
            })
        }

        _vh.cb(R.id.cb_animator)?.apply {
            clickIt {
                _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout)?.enableAnimator(isChecked)
                _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout2)?.enableAnimator(isChecked)
            }
        }

        _vh.cb(R.id.cb_emoji)?.apply {
            clickIt {
                if (isChecked) {
                    _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout)?.showEmojiLayout()
                    _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout2)?.showEmojiLayout()
                } else {
                    _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout)?.hideEmojiLayout()
                    _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout2)?.hideEmojiLayout()
                }
            }
        }

        _vh.click(R.id.emoji200) {
            _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout)?.showEmojiLayout(200.toDpi())
            _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout2)?.showEmojiLayout(200.toDpi())
        }

        _vh.click(R.id.emoji400) {
            _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout)?.showEmojiLayout(400.toDpi())
            _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout2)?.showEmojiLayout(400.toDpi())
        }

        _vh.click(R.id.emoji600) {
            _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout)?.showEmojiLayout(600.toDpi())
            _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout2)?.showEmojiLayout(600.toDpi())
        }

        _vh.spinner(R.id.spinner)?.setStrings(
            listOf(
                "HEIGHT",
                "OFFSET",
                "CONTENT_HEIGHT",
                "EMOJI_HEIGHT"
            )
        ) {
            _vh.v<DslSoftInputLayout>(R.id.lib_soft_input_layout)?.handlerMode = it + 1
        }
    }

    override fun onBackPressed(): Boolean {
        return super.onBackPressed() && _vh.soft(R.id.lib_soft_input_layout)?.onBackPress() == true
    }
}