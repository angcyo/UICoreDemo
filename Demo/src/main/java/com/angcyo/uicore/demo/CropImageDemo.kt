package com.angcyo.uicore.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.angcyo.component.getPhoto
import com.angcyo.crop.CropOverlay
import com.angcyo.crop.CropView
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.Library
import com.angcyo.library.ex.isDebugType
import com.angcyo.library.ex.toBitmap
import com.angcyo.library.model.loadPath
import com.angcyo.picker.dslSinglePickerImage
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.clickIt

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/13
 */
class CropImageDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        renderDslAdapter {
            bindItem(R.layout.demo_crop_image) { itemHolder, itemPosition, adapterItem, payloads ->
                val imageView = itemHolder.img(R.id.image_view)
                val cropView = itemHolder.v<CropView>(R.id.crop_image_view)
                cropView?.cropDelegate?.updateBitmap(
                    BitmapFactory.decodeResource(resources, R.drawable.face)
                )

                imageView?.clickIt {
                    imageView.isGone = true
                }

                //
                itemHolder.click(R.id.reset_button) {
                    cropView?.cropDelegate?.reset()
                }
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
                //裁剪
                itemHolder.click(R.id.crop_button) {
                    cropView?.cropDelegate?.crop()?.let {
                        imageView?.isVisible = true
                        imageView?.setImageBitmap(it)
                    }
                }
                //type
                itemHolder.tv(R.id.type_button)?.text =
                    when (cropView?.cropDelegate?.overlay?.clipType) {
                        CropOverlay.TYPE_CIRCLE -> "circle"
                        else -> "round"
                    }
                itemHolder.click(R.id.type_button) {
                    cropView?.cropDelegate?.overlay?.clipType =
                        when (cropView?.cropDelegate?.overlay?.clipType) {
                            CropOverlay.TYPE_CIRCLE -> CropOverlay.TYPE_ROUND
                            else -> CropOverlay.TYPE_CIRCLE
                        }

                    //upload
                    itemHolder.tv(R.id.type_button)?.text =
                        when (cropView?.cropDelegate?.overlay?.clipType) {
                            CropOverlay.TYPE_CIRCLE -> "circle"
                            else -> "round"
                        }
                }
                //比例
                itemHolder.click(R.id.origin_ratio_button) {
                    cropView?.cropDelegate?.overlay?.clipRatio = null
                }
                itemHolder.click(R.id.ratio_11_button) {
                    cropView?.cropDelegate?.overlay?.clipRatio = 1f
                }
                itemHolder.click(R.id.ratio_169_button) {
                    cropView?.cropDelegate?.overlay?.clipRatio = 16f / 9
                }
                itemHolder.click(R.id.ratio_34_button) {
                    cropView?.cropDelegate?.overlay?.clipRatio = 3f / 4
                }
            }
        }
    }
}