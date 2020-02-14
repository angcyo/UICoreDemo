package com.angcyo.uicore.dslitem

import androidx.camera.view.CameraView
import com.angcyo.camerax.dslitem.DslCameraViewHelper
import com.angcyo.camerax.dslitem.DslCameraViewItem
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.loader.LoaderMedia
import com.angcyo.picker.dslitem.DslPickerImageItem
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/14
 */

class AppCameraViewItem : DslCameraViewItem() {
    init {
        itemLayoutId = R.layout.app_item_camerax_view
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        val cameraView: CameraView? = itemHolder.v(R.id.lib_camera_view)

        val dslCameraView = DslCameraViewHelper()
        dslCameraView.cameraView = cameraView

        //拍照
        itemHolder.click(R.id.do_take_photo) {
            dslCameraView.takePicture { file, exception ->
                if (exception == null) {
                    itemDslAdapter?.changeHeaderItems {
                        it.add(DslPickerImageItem().apply {
                            checkModel = false
                            loaderMedia = LoaderMedia(localPath = file.absolutePath)
                        })
                    }
                }
            }
        }

        //录像
        itemHolder.click(R.id.do_take_video) {
            dslCameraView.startRecording { file, exception ->
                if (exception == null) {
                    itemDslAdapter?.changeHeaderItems {
                        it.add(DslPickerImageItem().apply {
                            checkModel = false
                            loaderMedia = LoaderMedia(localPath = file.absolutePath)
                        })
                    }
                }
            }

            itemHolder.postDelay(5_000) {
                dslCameraView.stopRecording()
            }
        }
    }
}