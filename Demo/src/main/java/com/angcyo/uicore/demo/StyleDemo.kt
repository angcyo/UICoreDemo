package com.angcyo.uicore.demo

import android.content.res.TypedArray
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._color
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/17
 */

class StyleDemo : AppDslFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        renderDslAdapter {
            DslAdapterItem()() {
                itemLayoutId = R.layout.lib_text_layout

                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.tv(R.id.lib_text_view)?.text = span {

                        fun log(typedArray: TypedArray) {
                            for (i in 0 until typedArray.length()) {
                                appendln()

                                val index = typedArray.getIndex(i)
                                val type = typedArray.getType(index)
                                val value = typedArray.peekValue(index)

                                append("$i->")
                                append(" type:") {
                                    foregroundColor = _color(R.color.colorPrimaryDark)
                                }
                                append("$type")
                                append(" value:") {
                                    foregroundColor = _color(R.color.colorPrimaryDark)
                                }
                                append("$value")

                                appendln()

                                append("${value?.string}") {
                                    foregroundColor = _color(R.color.colorPrimaryDark)
                                }
                                append(" ${typedArray.getString(index)}")

                                appendln()
                            }

                            typedArray.recycle()
                        }

                        val animTypedArray = fContext().obtainStyledAttributes(
                            R.style.LibToastTopAnimation,
                            intArrayOf(
                                android.R.attr.windowEnterAnimation,
                                android.R.attr.windowExitAnimation
                            )
                        )
                        append("AnimTypedArray:") {
                            foregroundColor = Color.RED
                        }
                        log(animTypedArray)
                        appendln()

                        /*
                        * value存在[StylesDemo]资源中
                        * key存在[StylesDemoStyleable]数组中
                        * */
                        val typedArray = fContext().obtainStyledAttributes(
                            R.style.StylesDemo,
                            R.styleable.StylesDemoStyleable
                        )

                        val typedArray2 = fContext().obtainStyledAttributes(
                            null,
                            R.styleable.StylesDemoStyleable,
                            0,
                            R.style.StylesDemo
                        )

                        append("TypedArray1:") {
                            foregroundColor = Color.RED
                        }
                        log(typedArray)
                        appendln()
                        append("TypedArray2:") {
                            foregroundColor = Color.RED
                        }
                        log(typedArray2)
                    }
                }
            }
        }
    }
}