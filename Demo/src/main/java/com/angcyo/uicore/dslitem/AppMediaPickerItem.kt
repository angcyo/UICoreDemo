package com.angcyo.uicore.dslitem

import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex.have
import com.angcyo.library.ex.hawkGet
import com.angcyo.library.ex.hawkPut
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

    companion object {
        const val MEDIA_LOADER_TYPE = "media_loader_type"
    }

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

        MEDIA_LOADER_TYPE.hawkGet()?.apply {
            if (this.isNotBlank()) {
                val type = toInt()

                itemHolder.selected(R.id.image_button, type.have(1))
                itemHolder.selected(R.id.video_button, type.have(2))
                itemHolder.selected(R.id.audio_button, type.have(4))
            }
        }

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
            MEDIA_LOADER_TYPE.hawkPut("${loaderConfig.mediaLoaderType}")
            onStartPicker(loaderConfig)
        }
    }
}