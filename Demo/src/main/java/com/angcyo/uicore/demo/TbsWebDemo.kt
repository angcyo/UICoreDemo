package com.angcyo.uicore.demo

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.browser.openCustomTab
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.core.component.file.file
import com.angcyo.core.component.fileSelector
import com.angcyo.dsladapter.renderItem
import com.angcyo.dsladapter.updateItem
import com.angcyo.library.ex.*
import com.angcyo.tbs.open
import com.angcyo.tbs.openSingle
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.clickIt
import com.angcyo.widget.base.string
import kotlin.toString

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/01
 */
class TbsWebDemo : AppDslFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderDslAdapter {
            renderItem {
                itemLayoutId = R.layout.demo_tbs_web

                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.click(R.id.open_url) {
                        dslAHelper {
                            open {
                                uri = Uri.parse(itemHolder.tv(R.id.edit_text).string())
                            }
                        }
                    }
                    itemHolder.click(R.id.browser_button) {
                        itemHolder.tv(R.id.edit_text).string().openCustomTab(requireActivity())
                    }
                    itemHolder.click(R.id.single_container_button) {
                        dslAHelper {
                            openSingle {
                                uri = Uri.parse(itemHolder.tv(R.id.edit_text).string())
                            }
                        }
                    }

                    itemHolder.click(R.id.open_url2) {
                        dslAHelper {
                            open {
                                uri = Uri.parse(itemHolder.tv(R.id.edit_text2).string())
                            }
                        }
                    }

                    itemHolder.click(R.id.open_url3) {
                        dslAHelper {
                            open {
                                uri = Uri.parse(itemHolder.tv(R.id.edit_text3).string())
                            }
                        }
                    }

                    itemHolder.click(R.id.local_file_chooser) {
                        dslAHelper {
                            open {
                                uri = Uri.parse("file:///android_asset/webpage/fileChooser.html")
                            }
                        }
                    }

                    itemHolder.click(R.id.local_file_play) {
                        dslAHelper {
                            open {
                                uri =
                                    Uri.parse("file:///android_asset/webpage/fullscreenVideo.html")
                            }
                        }
                    }

                    //文件选择, 打开文件
                    itemHolder.click(R.id.open_file) {
                        dslFHelper {
                            fileSelector({
                                showFileMd5 = true
                                showFileMenu = true
                                showHideFile = false
                            }) {

                                itemHolder.tv(R.id.result_text_view)?.text =
                                    it.file()?.absolutePath ?: "选择被取消 ${nowTimeString()}"

                                it?.run {
                                    dslAHelper {
                                        open {
                                            uri = fileUri
                                        }
                                    }
                                }
                            }
                        }
                    }

                    //点击的时候, 判断之前是否选择过文件, 直接打开之前选择过的文件
                    itemHolder.click(R.id.result_text_view) {
                        if (it is TextView) {
                            val path = it.text.toString()
                            if (path.isFileExist()) {
                                dslAHelper {
                                    open {
                                        uri = fileUri(context, path)
                                    }
                                }
                            }
                        }
                    }

                    //获取剪切板信息
                    itemHolder.tv(R.id.clip_text_view)?.apply {
                        text = "${nowTimeString()}\n${logClipboard()}"
                        clickIt {
                            try {
                                dslAHelper {
                                    open {
                                        uri = Uri.parse(getPrimaryClip().toString())
                                        mimeType = "text/plain"
                                    }
                                }
                            } catch (e: Exception) {
                                text = e.toString()
                            }
                        }
                    }

                    itemHolder.hawkInstallAndRestore("tbs_")
                }
            }
        }
    }

    override fun onFragmentNotFirstShow(bundle: Bundle?) {
        super.onFragmentNotFirstShow(bundle)
        _adapter.updateItem {
            true
        }
    }
}