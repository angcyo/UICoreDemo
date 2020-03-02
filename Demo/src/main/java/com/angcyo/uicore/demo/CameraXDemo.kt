package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.camerax.dslitem.DslCameraViewHelper
import com.angcyo.dsladapter.renderEmptyItem
import com.angcyo.fragment.requestPermissions
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.randomColorAlpha
import com.angcyo.library.toastQQ
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