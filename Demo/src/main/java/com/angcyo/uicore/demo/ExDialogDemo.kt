package com.angcyo.uicore.demo

import android.net.Uri
import android.os.Bundle
import com.angcyo.base.dslFHelper
import com.angcyo.core.component.fileSelector
import com.angcyo.dialog.openWidthDialog
import com.angcyo.dsladapter.findItem
import com.angcyo.item.DslButtonItem
import com.angcyo.item.DslInputItem
import com.angcyo.item.style.itemButtonText
import com.angcyo.item.style.itemEditText
import com.angcyo.library.ex.hawkGet
import com.angcyo.library.ex.hawkPut
import com.angcyo.library.ex.loadUrl
import com.angcyo.library.ex.str
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2021/06/28
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class ExDialogDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {

            //输入
            DslInputItem()() {
                itemEditText = "ExDialogDemo".hawkGet()
                editItemConfig.itemTextChangeAction = {
                    "ExDialogDemo".hawkPut(it)
                }
            }

            //选择
            DslButtonItem()() {
                itemButtonText = "选择文件..."
                itemClick = {
                    dslFHelper {
                        fileSelector {
                            it?.run {
                                findItem<DslInputItem> {
                                    itemEditText = fileUri.loadUrl()
                                    updateAdapterItem()
                                }
                            }
                        }
                    }
                }
            }

            //open width
            DslButtonItem()() {
                itemButtonText = "打开文件(OpenOpenWidth)..."
                itemClick = {
                    fContext().openWidthDialog {
                        findItem<DslInputItem> {
                            openUri = Uri.parse(itemEditText.str())
                        }
                    }
                }
            }
        }
    }
}