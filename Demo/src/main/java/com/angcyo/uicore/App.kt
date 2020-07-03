package com.angcyo.uicore

import android.graphics.DashPathEffect
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.angcyo.DslAHelper
import com.angcyo.base.restore
import com.angcyo.core.CoreApplication
import com.angcyo.core.fragment.BaseUI
import com.angcyo.core.viewpager.RFragmentAdapter
import com.angcyo.download.DslDownload
import com.angcyo.jpush.JPush
import com.angcyo.library.component.DslNotify
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.randomColor
import com.angcyo.objectbox.DslBox
import com.angcyo.speech.TTS
import com.angcyo.tbs.DslTbs
import com.angcyo.uicore.demo.*
import com.angcyo.uicore.fragment.RecyclerTextFragment
import com.angcyo.widget.base.padding
import io.objectbox.Box
import kotlin.random.Random

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

        BaseUI.fragmentUI.fragmentCreateBackItem = {
            BaseUI.fragmentUI.onFragmentCreateBackItem(it)?.apply {
                padding(0)
            }
        }

        DslDownload.init(this)
        DslTbs.init(this)

        DslNotify.DEFAULT_NOTIFY_ICON = R.drawable.ic_logo_small
        DslAHelper.mainActivityClass = MainActivity::class.java

        DslBox.default_package_name = BuildConfig.APPLICATION_ID
        DslBox.init(this, debug = false)

        TTS.init(
            this,
            1251235618,
            "AKIDWVomMV0edaGbFT5dSowGvGpmjC49pBCb",
            "0WbCzyN1ICYiFkSZ9Kv890GY1BPqUbsV"
        )

        JPush.init(this)
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

fun colors(count: Int = 5): List<Int> {
    val result = mutableListOf<Int>()
    for (i in 0 until count) {
        result.add(randomColor())
    }
    return result
}

fun value(min: Int = 0, max: Int = 100) = Random.nextInt(min, max)

fun dash(lineLength: Float = 2 * dp, spaceLength: Float = 3 * dp, phase: Float = 0f) =
    DashPathEffect(floatArrayOf(lineLength, spaceLength), phase)

/**快速获取[Box]*/
fun <T> boxOf(
    entityClass: Class<T>,
    packageName: String = BuildConfig.APPLICATION_ID,
    action: Box<T>.() -> Unit = {}
): Box<T> {
    val box = DslBox.getBox(packageName, entityClass)
    box.action()
    return box
}