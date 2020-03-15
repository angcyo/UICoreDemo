package com.angcyo.uicore.dslitem

import com.angcyo.coroutine.LifecycleScope
import com.angcyo.coroutine.launchSafe
import com.angcyo.coroutine.onBack
import com.angcyo.coroutine.onMain
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.jsoup.dslJsoup
import com.angcyo.jsoup.toAbsUrl
import com.angcyo.library.LTime
import com.angcyo.library.ex._color
import com.angcyo.library.ex.elseNull
import com.angcyo.library.ex.patternList
import com.angcyo.library.ex.string
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.progress.DYProgressView
import com.angcyo.widget.span.span
import org.jsoup.nodes.Document

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/14
 */
class AppJsoupHtmlItem : DslAdapterItem() {

    var jsoupData = JsoupData()

    init {
        itemLayoutId = R.layout.item_jsoup_html
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)
        itemHolder.tv(R.id.lib_sub_text_view)?.text = buildString {
            append(jsoupData.jsoupUrl)
            append(" ")
            append(jsoupData.selectCss)
        }

        //获取页面
        if (jsoupData.getUrlHtml) {
            itemHolder.v<DYProgressView>(R.id.lib_progress_view)?.startAnimator()
            LTime.tick()
            dslJsoup {
                scope = LifecycleScope(this@AppJsoupHtmlItem)
                url = jsoupData.jsoupUrl.toString()

                //异常处理
                onErrorAction = {
                    itemHolder.v<DYProgressView>(R.id.lib_progress_view)?.stopAnimator()
                    itemHolder.tv(R.id.select_view)?.text = span {
                        append("耗时:${LTime.time()}")
                    }
                    itemHolder.tv(R.id.html_view)?.text = it.string()
                }

                //文档准备完成
                onDocumentReady = { document ->
                    jsoupData.document = document

                    val title = document.title()
                    val html = document.html()

                    val select = _parseCss(document, jsoupData.selectCss)

                    onMain {
                        itemHolder.v<DYProgressView>(R.id.lib_progress_view)?.stopAnimator()

                        //选中内容
                        itemHolder.tv(R.id.select_view)?.text = span {
                            append("耗时:${LTime.time()}")
                            appendln()
                            append(select)
                        }

                        //获取html原始内容
                        itemHolder.tv(R.id.html_view)?.text = span {
                            append(title) {
                                foregroundColor = _color(R.color.colorAccent)
                            }
                            appendln()
                            append(document.baseUri()) {
                                foregroundColor = _color(R.color.colorAccent)
                            }
                            appendln()
                            append(document.location()) {
                                foregroundColor = _color(R.color.colorAccent)
                            }
                            appendln()
                            append(document.documentType()?.toString()) {
                                foregroundColor = _color(R.color.colorAccent)
                            }
                            appendln()
                            append(html)
                        }
                    }
                }
            }
        }

        //获取css
        if (jsoupData.getUrlSelect) {
            jsoupData.document?.apply {
                LifecycleScope(this@AppJsoupHtmlItem).launchSafe {
                    LTime.tick()
                    onBack {
                        _parseCss(jsoupData.document!!, jsoupData.selectCss)
                    }.await().also { select ->
                        //选中内容
                        itemHolder.tv(R.id.select_view)?.text = span {
                            append("耗时:${LTime.time()}")
                            appendln()
                            append(select)
                        }
                    }
                }
            }.elseNull {
                itemHolder.tv(R.id.select_view)?.text = "请先调用Get"
            }
        }

        jsoupData.getUrlHtml = false
        jsoupData.getUrlSelect = false
    }

    override fun onItemUpdateFrom(fromItem: DslAdapterItem) {
        super.onItemUpdateFrom(fromItem)
        if (fromItem is AppJsoupInputItem) {
            jsoupData = fromItem.jsoupData
        }
    }

    fun _parseCss(document: Document, select: String?): String {
        return if (select.isNullOrEmpty()) {
            ""
        } else {
            val elements = document.select(select)
            buildString {
                elements.forEachIndexed { index, element ->
                    append("$index->")
                    append(element.absUrl("href"))
                    appendln()

                    val style = element.attr("style")
                    append(style)
                    appendln()

                    append(
                        style.patternList("(?<=url\\(').*(?=')").firstOrNull()
                            ?.toAbsUrl(document.baseUri())
                    )
                    appendln()
                }
            }
        }
    }
}