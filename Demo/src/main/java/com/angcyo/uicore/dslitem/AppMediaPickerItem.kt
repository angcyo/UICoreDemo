package com.angcyo.uicore.dslitem

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.loader.LoaderConfig
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.each

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
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

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
            loaderConfig.mediaLoaderType = 0
            itemHolder.group(R.id.flow_layout)?.each {
                if (it.isSelected) {
                    it.tag?.toString()?.toIntOrNull()?.apply {
                        loaderConfig.mediaLoaderType = loaderConfig.mediaLoaderType or this
                    }
                }
            }
            onStartPicker(loaderConfig)
        }
    }
}