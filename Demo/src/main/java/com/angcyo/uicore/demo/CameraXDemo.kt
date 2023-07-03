package com.angcyo.uicore.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import com.angcyo.base.dslAHelper
import com.angcyo.camerax.dslitem.itemCameraLifecycleOwner
import com.angcyo.camerax.dslitem.itemCaptureCameraPhotoAction
import com.angcyo.camerax.dslitem.itemCaptureToDCIM
import com.angcyo.camerax.recordVideoCameraX
import com.angcyo.dsladapter.renderEmptyItem
import com.angcyo.dsladapter.updateOrInsertItem
import com.angcyo.item.DslButtonItem
import com.angcyo.item.DslImageItem
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemButtonText
import com.angcyo.item.style.itemLoadUri
import com.angcyo.item.style.itemText
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.fileSizeString
import com.angcyo.library.ex.logInfo
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.randomColor
import com.angcyo.library.ex.randomColorAlpha
import com.angcyo.library.ex.readBitmap
import com.angcyo.library.ex.toUri
import com.angcyo.library.toastQQ
import com.angcyo.pager.dslPager
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppCameraXViewItem
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/14
 */

class CameraXDemo : AppDslFragment() {

    @SuppressLint("UnsafeOptInUsageError")
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            AppCameraXViewItem()() {
                itemCaptureToDCIM = true
                itemCameraLifecycleOwner = this@CameraXDemo
                itemCaptureCameraPhotoAction = { uri, exception ->
                    exception?.let {
                        toastQQ(it.message)
                    }
                    uri?.let {
                        _adapter.changeHeaderItems {
                            it.add(DslImageItem().apply {
                                itemLoadUri = uri
                                itemClick = {
                                    dslPager {
                                        addMedia(itemLoadUri)
                                    }
                                }
                            })
                        }
                        updateResultText(uri.readBitmap()?.logInfo())
                    }
                }
                initItemCameraControllerIfNeed {
                    LifecycleCameraController(fContext()).apply {
                        setEnabledUseCases(CameraController.IMAGE_CAPTURE or CameraController.VIDEO_CAPTURE)
                        cameraSelector = cameraItemConfig.itemCameraSelector
                    }
                }
            }
            //---
            DslButtonItem()() {
                itemButtonText = "拍照or摄像"
                itemClick = {
                    dslAHelper {
                        recordVideoCameraX { path ->
                            if (path.isNullOrEmpty()) {
                                updateResultText("录制取消")
                            } else {
                                updateResultText("$path ${path.fileSizeString()}")
                                _adapter.changeHeaderItems {
                                    it.add(DslImageItem().apply {
                                        itemLoadUri = path.toUri()
                                        itemClick = {
                                            dslPager {
                                                addMedia(itemLoadUri)
                                            }
                                        }
                                    })
                                }
                            }
                        }
                    }
                }
            }
            //---
            for (i in 0..5) {
                renderEmptyItem(300 * dpi + i * 100 * dpi, randomColorAlpha())
            }
        }
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
    }

    /**更新返回的文本提示信息*/
    fun updateResultText(text: CharSequence?) {
        _adapter.updateOrInsertItem<DslTextItem>(
            "result",
            2
        ) { item ->
            item.itemText = span {
                append(nowTimeString()) {
                    foregroundColor = randomColor()
                }
                appendLine()
                append(text)
            }
            item
        }
    }
}