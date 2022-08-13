package com.angcyo.uicore.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import com.angcyo.component.getPhoto
import com.angcyo.crop.CropView
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.Library
import com.angcyo.library.ex.isDebugType
import com.angcyo.library.ex.toBitmap
import com.angcyo.library.model.loadPath
import com.angcyo.picker.dslSinglePickerImage
import com.angcyo.uicore.base.AppDslFragment

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/13
 */
class CropImageDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.demo_crop_image) { itemHolder, itemPosition, adapterItem, payloads ->
                val cropView = itemHolder.v<CropView>(R.id.crop_image_view)
                cropView?.cropDelegate?.updateBitmap(
                    BitmapFactory.decodeResource(resources, R.drawable.face)
                )

                //image
                itemHolder.click(R.id.image_button) {
                    if (isDebugType() && Library.CLICK_COUNT++ % 2 == 0) {
                        dslSinglePickerImage {
                            it?.firstOrNull()?.let { media ->
                                media.loadPath()?.apply {
                                    cropView?.cropDelegate?.updateBitmap(toBitmap()!!)
                                }
                            }
                        }
                    } else {
                        getPhoto {
                            it?.let {
                                cropView?.cropDelegate?.updateBitmap(it)
                            }
                        }
                    }
                }
            }
        }
    }
}