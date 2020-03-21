package com.angcyo.uicore

import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.angcyo.DslAHelper
import com.angcyo.base.restore
import com.angcyo.core.CoreApplication
import com.angcyo.core.viewpager.RFragmentAdapter
import com.angcyo.download.DslDownload
import com.angcyo.library.component.DslNotify
import com.angcyo.tbs.DslTbs
import com.angcyo.uicore.demo.*
import com.angcyo.uicore.fragment.RecyclerTextFragment

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
        DslAHelper.mainActivityClass = MainActivity::class.java
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

fun ViewPager.initAdapter(fragmentManager: FragmentManager, count: Int = 5) {
    val fragments = mutableListOf<Fragment>()
    for (i in 0 until count) {
        val f2 = RecyclerTextFragment()

        val f1 = fragmentManager.restore(
            when (i) {
                1 -> RefreshDemo()
                2 -> MediaPickerDemo()
                3 -> NotifyDemo()
                4 -> GlideImageDemo()
                else -> f2
            }
        ).first()

        fragments.add(f2)
    }
    adapter = RFragmentAdapter(fragmentManager, fragments)
}