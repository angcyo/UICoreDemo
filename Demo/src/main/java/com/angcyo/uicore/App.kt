package com.angcyo.uicore

import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.angcyo.core.CoreApplication
import com.angcyo.download.DslDownload
import com.angcyo.library.component.DslNotify
import com.angcyo.tbs.DslTbs
import com.angcyo.uicore.demo.R

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/23
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class App : CoreApplication(), CameraXConfig.Provider {

    override fun onCreate() {
        super.onCreate()

        DslDownload.init(this)
        DslTbs.init(this)

        DslNotify.DEFAULT_NOTIFY_ICON = R.drawable.ic_logo_small
    }

    /**
     * 不重写这个, 混淆后, 会崩溃.
     * 参见:[androidx.camera.core.CameraX.getOrCreateInstance]
     * @returns Camera2 default configuration
     * */
    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}