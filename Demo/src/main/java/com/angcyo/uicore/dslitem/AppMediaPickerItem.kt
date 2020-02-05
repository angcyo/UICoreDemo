package com.angcyo.uicore.dslitem

import com.angcyo.drawable.text.DslTextDrawable
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._color
import com.angcyo.library.ex.have
import com.angcyo.library.ex.hawkGet
import com.angcyo.library.ex.hawkPut
import com.angcyo.loader.LoaderConfig
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.each
import com.angcyo.widget.base.setInputText
import com.angcyo.widget.base.setLeftIco
import com.angcyo.widget.base.string

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/30
 */

class AppMediaPickerItem : DslAdapterItem() {

    companion object {
        const val MEDIA_LOADER_TYPE = "media_loader_type"
        const val MEDIA_MAX_LIMIT = "MEDIA_MAX_LIMIT"
        const val MEDIA_MIN_SIZE = "MEDIA_MIN_SIZE"
        const val MEDIA_MAX_SIZE = "MEDIA_MAX_SIZE"
        const val MEDIA_SIZE_MODEL = "MEDIA_SIZE_MODEL"
    }

    init {
        itemLayoutId = R.layout.app_item_media_picker
    }

    var onStartPicker: (LoaderConfig) -> Unit = {}

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        //恢复选项
        MEDIA_LOADER_TYPE.hawkGet()?.apply {
            if (this.isNotBlank()) {
                val type = toInt()

                itemHolder.selected(R.id.image_button, type.have(1))
                itemHolder.selected(R.id.video_button, type.have(2))
                itemHolder.selected(R.id.audio_button, type.have(4))
            }
        }

        //输入框
        itemHolder.ev(R.id.max_selector_limit)?.run {
            setLeftIco(DslTextDrawable().apply {
                text = "最多选中"
                textColor = _color(R.color.colorPrimaryDark)
            })
            setInputText(MEDIA_MAX_LIMIT.hawkGet())
        }
        itemHolder.ev(R.id.min_file_size)?.run {
            setLeftIco(DslTextDrawable().apply {
                text = "文件最小(kb)"
                textColor = _color(R.color.colorPrimaryDark)
            })
            setInputText(MEDIA_MIN_SIZE.hawkGet())
        }
        itemHolder.ev(R.id.max_file_size)?.run {
            setLeftIco(DslTextDrawable().apply {
                text = "文件最大(kb)"
                textColor = _color(R.color.colorPrimaryDark)
            })
            setInputText(MEDIA_MAX_SIZE.hawkGet())
        }
        itemHolder.cb(R.id.size_model_cb)?.isChecked = !MEDIA_SIZE_MODEL.hawkGet().isNullOrBlank()

        //点击事件
        itemHolder.click(R.id.image_button) {
            it.isSelected = !it.isSelected
        }
        itemHolder.click(R.id.video_button) {
            it.isSelected = !it.isSelected
        }
        itemHolder.click(R.id.audio_button) {
            it.isSelected = !it.isSelected
        }
        itemHolder.click(R.id.do_it_button) {
            val loaderConfig = LoaderConfig()

            //type
            loaderConfig.mediaLoaderType = 0
            itemHolder.group(R.id.flow_layout)?.each {
                if (it.isSelected) {
                    it.tag?.toString()?.toIntOrNull()?.apply {
                        loaderConfig.mediaLoaderType = loaderConfig.mediaLoaderType or this
                    }
                }
            }
            //limit
            loaderConfig.maxSelectorLimit =
                itemHolder.ev(R.id.max_selector_limit).string().toIntOrNull() ?: 9
            loaderConfig.limitFileMinSize =
                itemHolder.ev(R.id.min_file_size).string().toLongOrNull()?.run { this * 1024 } ?: -1
            loaderConfig.limitFileMaxSize =
                itemHolder.ev(R.id.max_file_size).string().toLongOrNull()?.run { this * 1024 } ?: -1
            loaderConfig.limitFileSizeModel =
                if (itemHolder.cb(R.id.size_model_cb)?.isChecked == true) LoaderConfig.SIZE_MODEL_MEDIA else LoaderConfig.SIZE_MODEL_SELECTOR

            //保存变量
            MEDIA_LOADER_TYPE.hawkPut("${loaderConfig.mediaLoaderType}")
            MEDIA_MAX_LIMIT.hawkPut(itemHolder.ev(R.id.max_selector_limit).string())
            MEDIA_MIN_SIZE.hawkPut(itemHolder.ev(R.id.min_file_size).string())
            MEDIA_MAX_SIZE.hawkPut(itemHolder.ev(R.id.max_file_size).string())
            MEDIA_SIZE_MODEL.hawkPut(if (itemHolder.cb(R.id.size_model_cb)?.isChecked == true) "check" else null)

            //回调
            onStartPicker(loaderConfig)
        }
    }
}