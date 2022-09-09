package com.angcyo.uicore

import android.graphics.DashPathEffect
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.angcyo.DslAHelper
import com.angcyo.base.restore
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bugly.Bugly
import com.angcyo.core.CoreApplication
import com.angcyo.core.component.DebugFragment
import com.angcyo.core.coreApp
import com.angcyo.core.fragment.BaseUI
import com.angcyo.core.viewpager.RFragmentAdapter
import com.angcyo.core.vmApp
import com.angcyo.crash.sight.CrashSight
import com.angcyo.download.DslDownload
import com.angcyo.engrave.model.FscDeviceModel
import com.angcyo.jpush.JPush
import com.angcyo.library.annotation.CallComplianceAfter
import com.angcyo.library.component.DslNotify
import com.angcyo.library.ex.*
import com.angcyo.library.isMainProgress
import com.angcyo.objectbox.DslBox
import com.angcyo.objectbox.laser.pecker.LPBox
import com.angcyo.server.DslAndServer
import com.angcyo.speech.TTS
import com.angcyo.tbs.DslTbs
import com.angcyo.uicore.demo.*
import com.angcyo.uicore.fragment.RecyclerTextFragment
import com.angcyo.websocket.service.bindLogWSServer
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

    companion object {
        //如果需要将url转成二维码, 参考以下链接, 注册app_id和app_secret
        //https://github.com/MZCretin/RollToolsApi#%E8%A7%A3%E9%94%81%E6%96%B0%E6%96%B9%E5%BC%8F
        const val app_id = "cmhxa3hkc25qbnJwcDN4cg=="
        const val app_secret = "UmxoNGMxZHVNRFo0WW5aclJrczRkVTU1YzFORVFUMDk="

        val headerMap: HashMap<String, String> =
            hashMapOf("app_id" to app_id.base64Decoder(), "app_secret" to app_secret.base64Encode())
    }

    override fun onCreate() {
        super.onCreate()

        //FragmentAnimator.onlyTopScale()

        BaseUI.fragmentUI.fragmentCreateBackItem = {
            BaseUI.fragmentUI.onFragmentCreateBackItem(it)?.apply {
                padding(0)
            }
        }

        DslDownload.init(this)

        DslNotify.DEFAULT_NOTIFY_ICON = R.drawable.ic_logo_small
        DslAHelper.mainActivityClass = MainActivity::class.java

        DslBox.default_package_name = BuildConfig.APPLICATION_ID
        DslBox.init(this, debug = false)

        DslAndServer//init

        //LaserPecker
        LPBox.init(this)
    }

    override fun onCreateMain() {
        super.onCreateMain()

        DslTbs.init(this)

        //fsc
        FscBleApiModel.init()
        vmApp<FscDeviceModel>().initDevice()

        //WebSocket
        DebugFragment.addDebugAction {
            name = "LogServer"
            action = {
                coreApp().bindLogWSServer()
            }
        }
    }

    @CallComplianceAfter
    override fun onComplianceAfter() {
        super.onComplianceAfter()
        if (isMainProgress()) {
            TTS.init(
                this,
                1251235618,
                "QUtJRFdWb21NVjBlZGFHYkZUNWRTb3dHdkdwbWpDNDlwQkNi".base64Decoder(),
                "MFdiQ3p5TjFJQ1lpRmtTWjlLdjg5MEdZMUJQcVVic1Y=".base64Decoder()
            )

            JPush.init(this)
            Bugly.init()
            CrashSight.init()
        }
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