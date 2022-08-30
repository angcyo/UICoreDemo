package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex.*
import com.angcyo.library.utils.*
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.auto
import com.angcyo.widget.base.isEmpty
import com.angcyo.widget.base.setInputText
import com.angcyo.widget.base.string
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/06/16
 */
class RegularPatternDemo : AppDslFragment() {

    companion object {
        const val KEY_PATTERN = "pattern_edit"
        const val KEY_CONTENT = "content_edit"
    }

    init {
        //默认值
        KEY_PATTERN.hawkPutList(PATTERN_MOBILE_SIMPLE, false)
        KEY_PATTERN.hawkPutList(PATTERN_MOBILE_EXACT, false)
        KEY_PATTERN.hawkPutList(PATTERN_TEL, false)
        KEY_PATTERN.hawkPutList(PATTERN_EMAIL, false)
        KEY_PATTERN.hawkPutList(PATTERN_URL, false)
        KEY_PATTERN.hawkPutList("(?<=compile).*(?=JavaWithJavac)", false)
        KEY_PATTERN.hawkPutList("(?<=id=)\\d+", false)
        KEY_PATTERN.hawkPutList("(?<=name=).+", false)
        KEY_PATTERN.hawkPutList("(?<=[Gg])[-]?[\\d.]*\\d+", false)
        KEY_PATTERN.hawkPutList("(?<=[Xx])[-]?[\\d.]*\\d+", false)
        KEY_PATTERN.hawkPutList("(?<=[Yy])[-]?[\\d.]*\\d+", false)
        KEY_PATTERN.hawkPutList("(?<=[Zz])[-]?[\\d.]*\\d+", false)

        //内容
        KEY_CONTENT.hawkPutList("13678953476", false)
        KEY_CONTENT.hawkPutList("compile_devDebugJavaWithJavac", false)
        KEY_CONTENT.hawkPutList("http://www.angcyo.com/api?id=123&name=xxx", false)
        KEY_CONTENT.hawkPutList("G2X1.00Y20y20 x9. z.9,9 g-.9", false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderDslAdapter {
            renderItem {
                itemLayoutId = R.layout.item_regular_pattern_layout

                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.auto(R.id.pattern_edit, KEY_PATTERN.hawkGetList())
                        ?.setInputText(KEY_PATTERN.hawkGetList().first())
                    itemHolder.auto(R.id.content_edit, KEY_CONTENT.hawkGetList())
                        ?.setInputText(KEY_CONTENT.hawkGetList().first())

                    //match
                    itemHolder.click(R.id.matcher_button) {
                        val regexList = if (itemHolder.ev(R.id.pattern_edit).isEmpty()) {
                            patternTelAndMobile()
                        } else {
                            mutableSetOf(itemHolder.ev(R.id.pattern_edit).string())
                        }

                        val content = itemHolder.ev(R.id.content_edit).string()

                        KEY_PATTERN.hawkPutList(regexList.first())
                        KEY_CONTENT.hawkPutList(content)

                        itemHolder.tv(R.id.lib_text_view)?.text = span {
                            appendLine("match结果:${content.pattern(regexList, false, false)}")
                            appendLine("find结果:${content.pattern(regexList, false, true)}")
                            val list = content.patternList(regexList.first())
                            append("matcher.find()->matcher.group():${list.size()}↓")
                            list.forEach {
                                appendln()
                                append(it) {
                                    foregroundColor = Color.RED
                                }
                                appendln()
                            }
                            append("--end↑")
                        }
                    }

                    //replace
                    itemHolder.click(R.id.replace_button) {
                        val regexList = if (itemHolder.ev(R.id.pattern_edit).isEmpty()) {
                            patternTelAndMobile()
                        } else {
                            mutableSetOf(itemHolder.ev(R.id.pattern_edit).string())
                        }
                        val content = itemHolder.ev(R.id.content_edit).string()
                        val newContent = itemHolder.ev(R.id.replace_edit).string()

                        itemHolder.tv(R.id.lib_text_view)?.text =
                            content.replace(regexList.first().toRegex(), newContent)
                    }
                }
            }
        }
    }
}