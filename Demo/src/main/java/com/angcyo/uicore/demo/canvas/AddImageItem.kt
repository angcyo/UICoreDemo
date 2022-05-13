package com.angcyo.uicore.demo.canvas

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.angcyo.canvas.CanvasView
import com.angcyo.canvas.utils.addPictureBitmapRenderer
import com.angcyo.component.getPhoto
import com.angcyo.dsladapter.item.IFragmentItem
import com.angcyo.library.ex.toBitmap
import com.angcyo.library.model.loadPath
import com.angcyo.picker.dslSinglePickerImage
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/04/18
 */
class AddImageItem(val canvasView: CanvasView) : CanvasControlItem(), IFragmentItem {

    override var itemFragment: Fragment? = null

    var itemFragmentManager: FragmentManager? = null

    init {
        itemIco = R.drawable.canvas_image_ico
        itemText = "图片"

        itemClick = {
            (itemFragmentManager ?: itemFragment?.parentFragmentManager)?.apply {
                canvasView.context.dslSinglePickerImage(this) {
                    it?.firstOrNull()?.let { media ->
                        media.loadPath()?.apply {
//                        canvasView.addDrawableRenderer(toBitmap())
//                            canvasView.addBitmapRenderer(toBitmap())
                            canvasView.addPictureBitmapRenderer(toBitmap())
                        }
                    }
                }
                /*canvasView.context.getPhoto(this) {
                    it?.let { canvasView.addPictureBitmapRenderer(it) }
                }*/
            }
        }
    }
}