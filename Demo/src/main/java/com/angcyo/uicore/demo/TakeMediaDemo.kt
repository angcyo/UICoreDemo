package com.angcyo.uicore.demo

import android.os.Bundle
import androidx.core.net.toUri
import com.angcyo.base.dslAHelper
import com.angcyo.dsladapter.updateOrInsertItem
import com.angcyo.dslitem.DslLabelMediaItem
import com.angcyo.item.DslBaseLabelItem
import com.angcyo.item.DslImageItem
import com.angcyo.item.DslTextItem
import com.angcyo.library.ex.fileSizeString
import com.angcyo.media.video.record.recordVideo
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/20
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class TakeMediaDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            DslLabelMediaItem()() {
                itemLabelText = "选择媒体"
                itemShowAddMediaItem = true
                itemFragment = this@TakeMediaDemo
            }

            DslBaseLabelItem()() {
                itemLabelText = "录像"
                itemClick = {
                    dslAHelper {
                        recordVideo { path ->
                            if (path.isNullOrEmpty()) {
                                updateOrInsertItem<DslTextItem>("result", 2) { item ->
                                    item.itemText = "录制取消"
                                    item
                                }
                            } else {
                                updateOrInsertItem<DslTextItem>("result", 2) { item ->
                                    item.itemText = "$path ${path.fileSizeString()}"
                                    item
                                }
                                changeFooterItems {
                                    it.add(DslImageItem().apply {
                                        itemLoadUri = path.toUri()
                                    })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}