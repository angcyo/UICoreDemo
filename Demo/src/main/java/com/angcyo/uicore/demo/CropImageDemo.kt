package com.angcyo.uicore.demo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.angcyo.component.getPhoto
import com.angcyo.crop.CropOverlay
import com.angcyo.crop.CropView
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.Library
import com.angcyo.library.ex.isDebugType
import com.angcyo.library.ex.toBitmap
import com.angcyo.library.ex.toDC
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

                val cropDelegate = cropView?.cropDelegate
                if (cropDelegate?._bitmap == null) {
                    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.face)
                    cropDelegate?.updateBitmap(bitmap)
                }

                imageView?.clickIt {
                    imageView.isGone = true
                }

                //
                itemHolder.click(R.id.reset_button) {
                    cropDelegate?.reset()
                    adapterItem.updateAdapterItem()
                }
                //image
                itemHolder.click(R.id.image_button) {
                    if (isDebugType() && Library.CLICK_COUNT++ % 2 == 0) {
                        dslSinglePickerImage {
                            it?.firstOrNull()?.let { media ->
                                media.loadPath()?.apply {
                                    cropDelegate?.updateBitmap(toBitmap()!!)
                                }
                            }
                        }
                    } else {
                        getPhoto {
                            it?.let {
                                cropDelegate?.updateBitmap(it)
                            }
                        }
                    }
                }
                //裁剪
                itemHolder.click(R.id.crop_button) {
                    cropDelegate?.crop()?.let {
                        imageView?.isVisible = true
                        imageView?.setImageBitmap(it)
                    }
                }
                //剪切框的操作模式
                itemHolder.clickAndInit(R.id.clip_move_button, {
                    (it as? TextView)?.text =
                        "ClipMove ${cropDelegate?.overlay?.enableClipMoveMode.toDC()}"
                }) {
                    cropDelegate?.overlay?.let {
                        it.enableClipMoveMode = !it.enableClipMoveMode
                    }
                }
                //type
                itemHolder.clickAndInit(R.id.type_button, {
                    (it as? TextView)?.text = when (cropDelegate?.overlay?.clipType) {
                        CropOverlay.TYPE_CIRCLE -> "circle"
                        else -> "round"
                    }
                }) {
                    cropDelegate?.overlay?.updateClipType(
                        when (cropDelegate.overlay.clipType) {
                            CropOverlay.TYPE_CIRCLE -> CropOverlay.TYPE_ROUND
                            else -> CropOverlay.TYPE_CIRCLE
                        }
                    )
                }
                //比例
                itemHolder.click(R.id.free_button) {//自由比例
                    cropDelegate?.overlay?.updateClipRatio(null)
                }
                itemHolder.click(R.id.origin_ratio_button) {//原始比例
                    cropDelegate?.overlay?.setBitmapRatio()
                }
                itemHolder.click(R.id.ratio_11_button) {
                    cropDelegate?.overlay?.updateClipRatio(1f)
                }
                itemHolder.click(R.id.ratio_169_button) {
                    cropDelegate?.overlay?.updateClipRatio(16f / 9)
                }
                itemHolder.click(R.id.ratio_34_button) {
                    cropDelegate?.overlay?.updateClipRatio(3f / 4)
                }
                //
                itemHolder.clickAndInit(R.id.flip_horizontal_button, {
                    (it as? TextView)?.text =
                        "水平翻转 ${cropDelegate?.flipHorizontal.toDC()}"
                }) {
                    cropDelegate?.apply {
                        flipHorizontal = !flipHorizontal
                        refresh()
                    }
                }
                itemHolder.clickAndInit(R.id.flip_vertical_button, {
                    (it as? TextView)?.text =
                        "垂直翻转 ${cropDelegate?.flipHorizontal.toDC()}"
                }) {
                    cropDelegate?.apply {
                        flipVertical = !flipVertical
                        refresh()
                    }
                }
                itemHolder.clickAndInit(R.id.rotate_button, {
                    (it as? TextView)?.text = "旋转 ${cropDelegate?.rotate} °"
                }) {
                    cropDelegate?.apply {
                        updateRotate(rotate + 90)
                        refresh()
                    }
                    adapterItem.updateAdapterItem()
                }
            }
        }
    }
}