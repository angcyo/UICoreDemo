package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.core.component.model.LanguageModel
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemText
import com.angcyo.library.L
import com.angcyo.library.app
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.span.span
import java.util.*

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/08
 */
class MultiLanguageDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        vmApp<LanguageModel>().localData.observe {
            L.i(it)
            _adapter.updateAllItem()
        }

        renderDslAdapter {
            DslTextItem()() {
                itemData = null
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemText = span {
                        append(getString(R.string.language_auto))
                        append(" ")
                        append("${LanguageModel.getAppLocaleList(requireContext()).first()}")
                        append("/")
                        append("${LanguageModel.getSystemLocalList().first()}")
                        append("/${LanguageModel.readLocalString(app())}")
                    }
                    initTextItem(itemHolder, itemPosition, adapterItem, payloads)
                }
                itemClick = {
                    LanguageModel.changeAppLanguage(requireActivity(), null)
                    addTextItem()
                    //requireActivity().recreate()
                }
            }
            LanguageModel.getAppLocaleList().forEach { locale ->
                DslTextItem()() {
                    itemData = locale
                    itemText = span {
                        append("language:${locale.language} ")
                        append("country:${locale.country} ")
                        append("variant:${locale.variant} ")
                        append(locale.toString()) {
                            foregroundColor = Color.RED
                        }
                    }
                    itemClick = {
                        LanguageModel.changeAppLanguage(requireActivity(), itemData as Locale)
                        addTextItem()
                    }
                }
            }
            LanguageModel.getSystemLocalList().forEach { locale ->
                DslTextItem()() {
                    itemData = locale
                    itemText = span {
                        append("language:${locale.language} ") {
                            fontSize = 9 * dpi
                        }
                        append("country:${locale.country} ") {
                            fontSize = 9 * dpi
                        }
                        append("variant:${locale.variant} ") {
                            fontSize = 9 * dpi
                        }
                        append(locale.toString()) {
                            foregroundColor = Color.RED
                            fontSize = 9 * dpi
                        }
                    }
                    itemClick = {
                        LanguageModel.changeAppLanguage(requireActivity(), itemData as Locale)
                        addTextItem()
                    }
                }
            }
        }
    }

    fun DslAdapter.addTextItem() {
        render {
            DslTextItem()() {
                itemData = null
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemText = span {
                        append(nowTimeString())
                        append(" ")
                        append(getString(R.string.language_auto))
                        append(" ")
                        append("${LanguageModel.getAppLocaleList(requireContext()).first()}")
                        append("/")
                        append("${LanguageModel.getSystemLocalList().first()}")
                        append("/${LanguageModel.readLocalString(app())}")
                    }
                    initTextItem(itemHolder, itemPosition, adapterItem, payloads)
                }
            }
        }
    }

}