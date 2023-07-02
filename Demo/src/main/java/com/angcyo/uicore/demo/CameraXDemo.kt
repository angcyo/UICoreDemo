package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.camerax.dslitem.DslCameraXViewItem
import com.angcyo.camerax.dslitem.itemCameraLifecycleOwner
import com.angcyo.dsladapter.renderEmptyItem
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.randomColorAlpha
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/14
 */

class CameraXDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            DslCameraXViewItem()() {
                itemCameraLifecycleOwner = this@CameraXDemo
            }

            for (i in 0..5) {
                renderEmptyItem(300 * dpi + i * 100 * dpi, randomColorAlpha())
            }
        }
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)
        /*_vh.post {
            activity?.requestPermissions(DslCameraViewHelper().recordPermissionList) {
                if (it) {
                    _adapter.render {
                        AppCameraViewItem()() {
                            itemCameraLifecycleOwner = this@CameraXDemo
                        }
                        
                        DslButtonItem()() {
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
                            renderEmptyItem(300 * dpi + i * 100 * dpi, randomColorAlpha())
                        }
                    }

                } else {
                    toastQQ("未获取到权限")
                }
            }
        }*/
    }
}