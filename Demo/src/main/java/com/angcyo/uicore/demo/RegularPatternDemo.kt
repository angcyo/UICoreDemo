package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.renderItem
import com.angcyo.library.ex.hawkGetList
import com.angcyo.library.ex.hawkPutList
import com.angcyo.library.ex.pattern
import com.angcyo.library.ex.patternList
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

        KEY_CONTENT.hawkPutList("13678953476", false)
        KEY_CONTENT.hawkPutList("compile_devDebugJavaWithJavac", false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderDslAdapter {
            renderItem {
                itemLayoutId = R.layout.item_regular_pattern_layout

                onItemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.auto(R.id.pattern_edit, KEY_PATTERN.hawkGetList())
                        ?.setInputText(KEY_PATTERN.hawkGetList().first())
                    itemHolder.auto(R.id.content_edit, KEY_CONTENT.hawkGetList())
                        ?.setInputText(KEY_CONTENT.hawkGetList().first())

                    itemHolder.click(R.id.matcher_button) {
                        val regexList = if (itemHolder.ev(R.id.pattern_edit).isEmpty()) {
                            patternTelAndMobile()
                        } else {
                            mutableSetOf(itemHolder.ev(R.id.pattern_edit).string())
                        }

                        val contentString = itemHolder.ev(R.id.content_edit).string()
                        val pattern = contentString.pattern(regexList, false)

                        KEY_PATTERN.hawkPutList(regexList.first())
                        KEY_CONTENT.hawkPutList(contentString)

                        itemHolder.tv(R.id.text_view)?.text = span {
                            append("匹配结果:$pattern")
                            appendln()
                            contentString.patternList(regexList.first()).forEach {
                                append("matcher.find()->matcher.group()")
                                appendln()
                                append(it)
                                appendln()
                            }
                            append("--end")
                        }
                    }
                }
            }
        }
    }
}