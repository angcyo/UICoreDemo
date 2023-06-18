package com.angcyo.uicore.dslitem

import androidx.camera.view.PreviewView
import com.angcyo.camerax.control.DslCameraViewHelper
import com.angcyo.camerax.dslitem.DslCameraViewItem
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.ex.simpleClassName
import com.angcyo.library.model.LoaderMedia
import com.angcyo.library.toast
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

        val cameraView: PreviewView? = itemHolder.v(R.id.lib_camera_view)

        val dslCameraViewHelper = DslCameraViewHelper()
        dslCameraViewHelper.cameraView = cameraView

        //拍照
        itemHolder.click(R.id.do_take_photo) {
            dslCameraViewHelper.saveToDCIM = itemHolder.isChecked(R.id.dcim_cb)
            dslCameraViewHelper.takePicture { file, exception ->
                if (exception == null) {
                    itemDslAdapter?.changeHeaderItems {
                        it.add(DslPickerImageItem().apply {
                            checkModel = false
                            loaderMedia = LoaderMedia(localPath = file.absolutePath)
                        })
                    }
                }
                toast("$file")
            }
        }

        //录像
        itemHolder.click(R.id.do_take_video) {
            dslCameraViewHelper.saveToDCIM = itemHolder.isChecked(R.id.dcim_cb)
            dslCameraViewHelper.startRecording { file, exception ->
                if (exception == null) {
                    itemDslAdapter?.changeHeaderItems {
                        it.add(DslPickerImageItem().apply {
                            checkModel = false
                            loaderMedia = LoaderMedia(localPath = file.absolutePath)
                        })
                    }
                }
                toast("$file")
            }

            itemHolder.postDelay(5_000) {
                dslCameraViewHelper.stopRecording()
            }
        }

        itemHolder.hawkInstallAndRestore(this.simpleClassName())
    }
}