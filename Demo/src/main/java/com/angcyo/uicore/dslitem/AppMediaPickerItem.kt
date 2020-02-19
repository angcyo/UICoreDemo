package com.angcyo.uicore.dslitem

import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.component.hawkSave
import com.angcyo.drawable.text.DslTextDrawable
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex._color
import com.angcyo.library.ex.simpleClassName
import com.angcyo.loader.LoaderConfig
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.each
import com.angcyo.widget.base.setLeftIco
import com.angcyo.widget.base.string

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/30
 */

class AppMediaPickerItem : DslAdapterItem() {

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

        //输入框
        itemHolder.ev(R.id.max_selector_limit)?.run {
            setLeftIco(DslTextDrawable().apply {
                text = "最多选中"
                textColor = _color(R.color.colorPrimaryDark)
            })
        }
        itemHolder.ev(R.id.min_file_size)?.run {
            setLeftIco(DslTextDrawable().apply {
                text = "文件最小(kb)"
                textColor = _color(R.color.colorPrimaryDark)
            })
        }
        itemHolder.ev(R.id.max_file_size)?.run {
            setLeftIco(DslTextDrawable().apply {
                text = "文件最大(kb)"
                textColor = _color(R.color.colorPrimaryDark)
            })
        }

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

            //enable
            loaderConfig.enableOrigin = itemHolder.isChecked(R.id.origin_cb)
            loaderConfig.enableImageEdit = itemHolder.isChecked(R.id.edit_cb)

            //保存变量
            itemHolder.hawkSave(this.simpleClassName())

            //回调
            onStartPicker(loaderConfig)
        }

        itemHolder.hawkInstallAndRestore(this.simpleClassName())
    }
}