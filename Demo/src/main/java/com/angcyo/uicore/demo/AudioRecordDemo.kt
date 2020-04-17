package com.angcyo.uicore.demo

import android.graphics.Color
import android.os.Bundle
import com.angcyo.dsladapter.filter.batchLoad
import com.angcyo.dsladapter.margin
import com.angcyo.dsladapter.renderEmptyItem
import com.angcyo.library.L
import com.angcyo.library.ex.*
import com.angcyo.library.utils.audioFocusString
import com.angcyo.media.dslitem.DslPlayAudioItem
import com.angcyo.media.dslitem.DslRecordAudioItem
import com.angcyo.tablayout.logw
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/15
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class AudioRecordDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            val uri1 = "http://ting6.yymp3.net:86/new27/nzbz/2.mp3".toUri()
            val uri2 = "http://ting6.yymp3.net:86/new27/gdys/1.mp3".toUri()

            batchLoad()

            DslPlayAudioItem()() {
                //http://www.yymp3.com/
                itemAudioUri = uri1
                margin(10 * dpi)
            }

            DslPlayAudioItem()() {
                itemAudioUri = uri1
                itemAudioDuration = 2 * 1_000
                itemRecordMaxDuration = 60 * 1_000
                itemShowDelete = true
                margin(10 * dpi)
            }

            DslPlayAudioItem()() {
                itemAudioUri = uri1
                itemAudioDuration = 20 * 1_000
                itemRecordMaxDuration = 60 * 1_000
                itemShowDelete = true
                margin(10 * dpi)
            }

            DslPlayAudioItem()() {
                itemAudioUri = uri1
                itemAudioDuration = 20 * 1_000
                itemShowDelete = true
                margin(10 * dpi)
            }

            DslRecordAudioItem()() {
                itemTopInsert = 10 * dpi
                itemDecorationColor = Color.RED
                itemFragment = this@AudioRecordDemo
            }

            for (i in 0..10) {
                renderEmptyItem(168 * dpi, randomColor())
            }

            DslPlayAudioItem()() {
                itemAudioUri = uri2
                margin(10 * dpi)
            }

            DslPlayAudioItem()() {
                itemAudioUri = uri2
                itemAudioDuration = 2 * 1_000
                itemRecordMaxDuration = 60 * 1_000
                itemShowDelete = true
                margin(10 * dpi)
            }

            DslPlayAudioItem()() {
                itemAudioUri = uri2
                itemAudioDuration = 20 * 1_000
                itemRecordMaxDuration = 60 * 1_000
                itemShowDelete = true
                margin(10 * dpi)
            }

            DslPlayAudioItem()() {
                itemAudioUri = uri2
                itemAudioDuration = 20 * 1_000
                itemShowDelete = true
                margin(10 * dpi)
            }

            DslRecordAudioItem()() {
                itemTopInsert = 10 * dpi
                itemDecorationColor = Color.RED
                itemFragment = this@AudioRecordDemo
            }
        }

        fContext().requestAudioFocus() { focusChange ->
            L.w("${simpleHash()} 音频焦点回调:$focusChange ${focusChange.audioFocusString()}")
        }.logw()
    }
}