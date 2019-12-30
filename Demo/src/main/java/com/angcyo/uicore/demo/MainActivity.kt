package com.angcyo.uicore.demo

import android.Manifest
import com.angcyo.base.dslFHelper
import com.angcyo.core.activity.BasePermissionsActivity
import com.angcyo.core.activity.PermissionBean

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/17
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class MainActivity : BasePermissionsActivity() {

    init {
//        permissions.add(
//            PermissionBean(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                R.drawable.permission_sd,
//                "SD卡存储权限"
//            )
//        )
//        permissions.add(
//            PermissionBean(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                R.drawable.permission_gps,
//                "GPS权限"
//            )
//        )
//        permissions.add(
//            PermissionBean(
//                Manifest.permission.CAMERA,
//                R.drawable.permission_camera,
//                "摄像头权限"
//            )
//        )
//        permissions.add(
//            PermissionBean(
//                Manifest.permission.RECORD_AUDIO,
//                R.drawable.permission_record,
//                "麦克风权限"
//            )
//        )
    }

    override fun onPermissionGranted() {
        super.onPermissionGranted()
        dslFHelper {
            removeAll()
            restore(MainFragment())
        }
    }
}
