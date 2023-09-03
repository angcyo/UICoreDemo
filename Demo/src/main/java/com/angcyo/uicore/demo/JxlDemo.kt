package com.angcyo.uicore.demo

import android.net.Uri
import android.os.Bundle
import com.angcyo.base.dslFHelper
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.core.component.fileSelector
import com.angcyo.dsladapter.bindItem
import com.angcyo.jxl.Jxl
import com.angcyo.library.component.parser.parseDateTemplate
import com.angcyo.library.component.parser.parseNumberTemplate
import com.angcyo.library.ex._color
import com.angcyo.library.ex.decode
import com.angcyo.library.ex.nowTime
import com.angcyo.library.ex.toStr
import com.angcyo.library.libCacheFile
import com.angcyo.library.libFolderPath
import com.angcyo.library.utils.fileNameTime
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.string
import com.angcyo.widget.span.span

/**
 * Jxl 读取/写入Excel
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/08/24
 */
class JxlDemo : AppDslFragment() {

    /**选中的文件*/
    var fileUri: Uri? = null

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.demo_jxl_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                itemHolder.hawkInstallAndRestore("_jxl_demo")

                itemHolder.click(R.id.select_button) {
                    dslFHelper {
                        fileSelector({
                            targetPath = libFolderPath("")
                            showFileMd5 = true
                            showFileMenu = true
                            showHideFile = true
                        }) {
                            it?.fileUri?.let {
                                fileUri = it
                                itemHolder.tv(R.id.file_path_view)?.text = "$it".decode()
                            }
                        }
                    }
                }

                itemHolder.click(R.id.read_button) {
                    if (fileUri == null) {
                        itemHolder.tv(R.id.result_text_view)?.text = "请先选择文件"
                    } else {
                        val map = Jxl.readExcel(fileUri)
                        itemHolder.tv(R.id.result_text_view)?.text = span {
                            if (map == null) {
                                append("读取失败")
                            } else {
                                map.forEach { (key, value) ->
                                    append(key) {
                                        bold = true
                                        foregroundColor = _color(R.color.colorAccent)
                                    }
                                    append("\n")
                                    value.forEach { line ->
                                        append(line.toStr())
                                        append("\n")
                                    }
                                    append("\n")
                                }
                            }
                        }
                    }
                }
                itemHolder.click(R.id.write_button) {
                    val file = libCacheFile(fileNameTime(suffix = ".xls"))
                    Jxl.writeExcel(file, createExcelData())
                    itemHolder.tv(R.id.result_text_view)?.text = "$file"
                }

                //---

                itemHolder.click(R.id.parse_date_button) {
                    val text = itemHolder.tv(R.id.edit_text)?.string(false)
                    itemHolder.tv(R.id.result_text_view)?.text = text?.parseDateTemplate()
                }

                itemHolder.click(R.id.parse_number_button) {
                    val template = itemHolder.tv(R.id.edit_number_template)?.string(false)
                    val text = itemHolder.tv(R.id.edit_number)?.string(true)
                    itemHolder.tv(R.id.result_text_view)?.text = span {
                        val number1 = text ?: nowTime().toString()
                        append(number1 + "->" + template?.parseNumberTemplate(number1))
                        append("\n")
                        val number2 = nowTime().toString()
                        append(number2 + "->" + template?.parseNumberTemplate(number2))
                        append("\n")
                        val number3 = "${nowTime()}.${nowTime()}"
                        append(number3 + "->" + template?.parseNumberTemplate(number3))
                        append("\n")
                        val number4 = "${nowTime()}."
                        append(number4 + "->" + template?.parseNumberTemplate(number4))
                        append("\n")
                        val number5 = ".${nowTime()}"
                        append(number5 + "->" + template?.parseNumberTemplate(number5))
                    }
                }
            }
        }
    }

    private fun createExcelData(): List<List<Any?>> {
        val result = mutableListOf<List<Any?>>()

        for (line in 0..10) {
            val lineList = mutableListOf<Any?>()
            for (column in 0..5) {
                lineList.add("行:$line 列:$column")
            }
            result.add(lineList)
        }

        return result
    }

}