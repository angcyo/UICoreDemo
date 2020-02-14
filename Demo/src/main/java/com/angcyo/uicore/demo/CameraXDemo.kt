package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.base.requestPermission
import com.angcyo.camerax.dslitem.DslCameraViewHelper
import com.angcyo.dsladapter.renderEmptyItem
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.randomColorAlpha
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppCameraViewItem

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/14
 */

class CameraXDemo : AppDslFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        _adapter + AppCameraViewItem().apply {
            cameraLifecycleOwner = this@CameraXDemo
        }

        for (i in 0..5) {
            _adapter.renderEmptyItem(300 * dpi + i * 100 * dpi, randomColorAlpha())
        }

        activity?.requestPermission(DslCameraViewHelper().recordPermissionList)
    }
}