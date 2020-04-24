package com.angcyo.uicore.demo

import android.os.Bundle
import androidx.core.net.toUri
import com.angcyo.base.dslAHelper
import com.angcyo.camerax.recordVideoCameraX
import com.angcyo.dsladapter.updateOrInsertItem
import com.angcyo.dslitem.DslLabelMediaItem
import com.angcyo.item.DslButtonItem
import com.angcyo.item.DslImageItem
import com.angcyo.item.DslTextItem
import com.angcyo.library.ex.fileSizeString
import com.angcyo.library.ex.nowTimeString
import com.angcyo.media.video.record.RecordVideoActivity
import com.angcyo.media.video.record.recordVideo
import com.angcyo.pager.dslPager
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.component.AppWaterMarkerRecordVideoCallback

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

            val lastIndex = 3

            fun result(path: String?) {
                path.apply {
                    if (path.isNullOrEmpty()) {
                        updateOrInsertItem<DslTextItem>("result", lastIndex) { item ->
                            item.itemText = "操作取消 ${nowTimeString()}"
                            item
                        }
                    } else {
                        updateOrInsertItem<DslTextItem>("result", lastIndex) { item ->
                            item.itemText = "$path ${path.fileSizeString()}"
                            item
                        }
                        changeFooterItems {
                            it.add(DslImageItem().apply {
                                itemLoadUri = path.toUri()
                                itemClick = {
                                    dslPager {
                                        addMedia(itemLoadUri)
                                    }
                                }
                            })
                        }
                    }
                }
            }

            DslButtonItem()() {
                itemButtonText = "拍照or摄像"
                itemClick = {
                    RecordVideoActivity.recordVideoCallback = AppWaterMarkerRecordVideoCallback()
                    dslAHelper {
                        recordVideo(result = ::result)
                    }
                }
            }

            DslButtonItem()() {
                itemButtonText = "拍照or摄像(CameraX)"
                itemClick = {
                    RecordVideoActivity.recordVideoCallback = AppWaterMarkerRecordVideoCallback()
                    dslAHelper {
                        recordVideoCameraX(result = ::result)
                    }
                }
            }
        }
    }
}