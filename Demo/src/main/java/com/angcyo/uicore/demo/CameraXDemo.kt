package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.base.dslAHelper
import com.angcyo.camerax.dslitem.DslCameraViewHelper
import com.angcyo.camerax.recordVideoCameraX
import com.angcyo.dsladapter.renderEmptyItem
import com.angcyo.dsladapter.updateOrInsertItem
import com.angcyo.fragment.requestPermissions
import com.angcyo.item.DslButtonItem
import com.angcyo.item.DslImageItem
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemButtonText
import com.angcyo.item.style.itemLoadUri
import com.angcyo.item.style.itemText
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.fileSizeString
import com.angcyo.library.ex.randomColorAlpha
import com.angcyo.library.ex.toUri
import com.angcyo.library.toastQQ
import com.angcyo.pager.dslPager
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppCameraViewItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/14
 */

class CameraXDemo : AppDslFragment() {

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
        _vh.post {
            activity?.requestPermissions(DslCameraViewHelper().recordPermissionList) {
                if (it) {
                    _adapter + AppCameraViewItem().apply {
                        cameraLifecycleOwner = this@CameraXDemo
                    }

                    _adapter + DslButtonItem().apply {
                        itemButtonText = "拍照or摄像"
                        itemClick = {
                            dslAHelper {
                                recordVideoCameraX { path ->
                                    if (path.isNullOrEmpty()) {
                                        _adapter.updateOrInsertItem<DslTextItem>(
                                            "result",
                                            2
                                        ) { item ->
                                            item.itemText = "录制取消"
                                            item
                                        }
                                    } else {
                                        _adapter.updateOrInsertItem<DslTextItem>(
                                            "result",
                                            2
                                        ) { item ->
                                            item.itemText = "$path ${path.fileSizeString()}"
                                            item
                                        }
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

                    for (i in 0..5) {
                        _adapter.renderEmptyItem(300 * dpi + i * 100 * dpi, randomColorAlpha())
                    }

                } else {
                    toastQQ("未获取到权限")
                }
            }
        }
    }
}