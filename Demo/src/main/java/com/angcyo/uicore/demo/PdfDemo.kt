package com.angcyo.uicore.demo

import android.net.Uri
import android.os.Bundle
import androidx.core.net.toUri
import com.angcyo.base.dslFHelper
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.core.component.fileSelector
import com.angcyo.dsladapter.bindItem
import com.angcyo.item.DslSingleImageItem
import com.angcyo.item.style.itemLoadImage
import com.angcyo.library.component.lastDecorView
import com.angcyo.library.component.pdf.Pdf
import com.angcyo.library.component.pdf.pdfWriter
import com.angcyo.library.ex.decode
import com.angcyo.library.libCacheFile
import com.angcyo.library.libFolderPath
import com.angcyo.library.utils.fileNameTime
import com.angcyo.uicore.base.AppDslFragment

/**
 * Pdf demo
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023-9-12
 */
class PdfDemo : AppDslFragment() {

    /**选中的文件*/
    var fileUri: Uri? = null

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.demo_pdf_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                itemHolder.hawkInstallAndRestore("_pdf_demo")

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
                        Pdf.readPdfToBitmap(fileUri)?.let { list ->
                            itemHolder.tv(R.id.result_text_view)?.text = "读取成功:${list.size}页"
                            render {
                                for (bitmap in list) {
                                    DslSingleImageItem()() {
                                        itemLoadImage = bitmap
                                    }
                                }
                            }
                        } ?: let {
                            itemHolder.tv(R.id.result_text_view)?.text = "读取失败"
                        }
                    }
                }

                itemHolder.click(R.id.write_button) {
                    val file = libCacheFile(fileNameTime(suffix = ".pdf"))
                    pdfWriter(file.absolutePath) {
                        lastDecorView?.let {
                            writePage(it)
                        }
                        writePage(itemHolder.itemView)
                    }
                    fileUri = file.toUri()
                    itemHolder.tv(R.id.result_text_view)?.text = "$file"
                }
            }
        }
    }
}