package com.angcyo.uicore.dslitem

import androidx.core.net.toFile
import com.angcyo.camerax.dslitem.DslCameraXViewItem
import com.angcyo.camerax.dslitem.itemCaptureToDCIM
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

class AppCameraXViewItem : DslCameraXViewItem() {

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
        itemHolder.check(R.id.dcim_cb, true) { buttonView, isChecked ->
            itemCaptureToDCIM = isChecked
        }

        /*val cameraView: PreviewView? = itemHolder.v(R.id.lib_camera_view)

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
        }*/

        //录像
        itemHolder.click(R.id.do_take_video) {
            if (startRecordingCamera { uri, exception ->
                    if (exception == null) {
                        itemDslAdapter?.changeHeaderItems {
                            it.add(DslPickerImageItem().apply {
                                checkModel = false
                                loaderMedia = LoaderMedia(localPath = uri?.toFile()?.absolutePath)
                            })
                        }
                        toast("$uri")
                    } else {
                        toast("${exception.message}")
                    }
                }) {
                toast("开始录像")
            }

            itemHolder.postDelay(5_000) {
                stopRecordingCamera()
            }
        }

        itemHolder.hawkInstallAndRestore(this.simpleClassName())
    }
}