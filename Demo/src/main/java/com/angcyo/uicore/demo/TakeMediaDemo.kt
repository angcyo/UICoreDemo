package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.base.dslAHelper
import com.angcyo.dslitem.DslLabelMediaItem
import com.angcyo.item.DslBaseLabelItem
import com.angcyo.library.L
import com.angcyo.media.video.record.RecordVideoActivity
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
                        start(RecordVideoActivity::class.java) {
                            requestCode = RecordVideoActivity.REQUEST_CODE
                            onActivityResult = { resultCode, data ->
                                L.w(
                                    RecordVideoActivity.getResultPath(
                                        requestCode,
                                        resultCode,
                                        data
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}