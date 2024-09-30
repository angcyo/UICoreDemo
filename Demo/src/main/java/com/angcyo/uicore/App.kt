package com.angcyo.uicore

import android.graphics.DashPathEffect
import android.util.Log
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.angcyo.DslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.base.restore
import com.angcyo.bluetooth.fsc.CameraApiModel
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.parse.toLaserPeckerVersionName
import com.angcyo.bugly.Bugly
import com.angcyo.core.CoreApplication
import com.angcyo.core.Debug
import com.angcyo.core.component.ScreenShotModel
import com.angcyo.core.fragment.BaseUI
import com.angcyo.core.viewpager.RFragmentAdapter
import com.angcyo.core.vmApp
import com.angcyo.crash.sight.CrashSight
import com.angcyo.dialog.other.singleImageDialog
import com.angcyo.download.DslDownload
import com.angcyo.http.DslHttp
import com.angcyo.http.gitee.Gitee
import com.angcyo.http.rx.doMain
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.component.DebugFragment
import com.angcyo.item.style.itemInfoText
import com.angcyo.jpush.JPush
import com.angcyo.laserpacker.device.DeviceHelper
import com.angcyo.laserpacker.device.ble.CommandFragment
import com.angcyo.laserpacker.device.ble.ConnectFragment
import com.angcyo.laserpacker.device.ble.DeviceSettingFragment
import com.angcyo.laserpacker.device.ble.TransferDataFragment
import com.angcyo.laserpacker.device.model.FscDeviceModel
import com.angcyo.library.annotation.CallComplianceAfter
import com.angcyo.library.component.RBackground
import com.angcyo.library.component.lastContext
import com.angcyo.library.component.pad.IPadAdaptive
import com.angcyo.library.ex.*
import com.angcyo.library.isMainProgress
import com.angcyo.objectbox.DslBox
import com.angcyo.quickjs.QuickJSEngine
import com.angcyo.quickjs.api.Api
import com.angcyo.speech.TTS
import com.angcyo.tbs.DslTbs
import com.angcyo.uicore.demo.*
import com.angcyo.uicore.demo.draw.DrawTextView
import com.angcyo.uicore.fragment.RecyclerTextFragment
import io.objectbox.Box
import kotlin.random.Random

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/23
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class App : CoreApplication(), CameraXConfig.Provider, IPadAdaptive {

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

        BaseUI.fragmentUI.apply {
            fragmentCreateBefore = { fragment, fragmentConfig, savedInstanceState ->
                fragmentConfig.isLightStyle = false
                fragmentConfig.showTitleLineView = false
            }
            fragmentCreateBackItem = {
                BaseUI.fragmentUI.onFragmentCreateBackItem(it)?.apply {
                    padding(0)
                }
            }
        }

        DslDownload.init(this)

        //DslNotify.DEFAULT_NOTIFY_ICON = R.drawable.ic_logo_small
        DslAHelper.mainActivityClass = MainActivity::class.java

        DslBox.default_package_name = BuildConfig.APPLICATION_ID
        DslBox.init(this, debug = false)

        LPAppHelper.initLPApp(this)

        //js
        QuickJSEngine.initAndListen()

        //js api
        Api.injectApiAction { _, appJs ->
            appJs.registerJavaMethod({ _, _ ->
                doMain {
                    lastContext.singleImageDialog(_drawable(R.drawable.all_in2))
                }
            }, "showReceiveCode")
        }
    }

    override fun onCreateMain() {
        super.onCreateMain()

        //url
        Gitee.BASE = "https://gitee.com/angcyo/file/raw/master/UICoreDemo"
        Gitee.BASE_BACKUP = "https://gitlab.com/angcyo/file/-/raw/master/UICoreDemo"

        DslTbs.init(this)

        //fsc
        //FscBleApiModel.NO_BLE = false //2023-7-31 LP5 使用ble扫描
        FscBleApiModel.init()
        vmApp<FscDeviceModel>().initDevice()

        //website
        //AutoEngraveHelper.init()

        //固件升级
        DeviceSettingFragment.createFirmwareUpdateItemAction = { fragment, adapter ->
            vmApp<LaserPeckerModel>().productInfoData.value?.softwareVersion?.run {
                DslTextInfoItem().apply {
                    itemInfoText = _string(R.string.firmware_version)
                    itemDarkText = toLaserPeckerVersionName()
                    configInfoTextStyle {
                        textSize = _dimen(R.dimen.text_sub_size).toFloat()
                    }
                    itemClick = {}//清空事件
                }
            }
        }

        //保护罩固件升级
        DeviceSettingFragment.createCoverFirmwareUpdateItemAction = { fragment, adapter ->
            DslTextInfoItem().apply {
                itemInfoText = _string(R.string.cover_firmware_version)
                itemDarkText = vmApp<CameraApiModel>().coverVersionData.value?.hostVersionStr
                configInfoTextStyle {
                    textSize = _dimen(R.dimen.text_sub_size).toFloat()
                }
                itemClick = {}//清空事件
                vmApp<CameraApiModel>().fetchCoverVersion { data, error ->
                    if (data != null) {
                        itemDarkText =
                            vmApp<CameraApiModel>().coverVersionData.value?.hostVersionStr
                        updateAdapterItem()
                    }
                }
            }
        }

        //截图分享
        vmApp<ScreenShotModel>().apply {
            startListen()
            screenShotPathData.observeForever {
                if (!it.isNullOrBlank() && !RBackground.isBackground()) {
                    DeviceHelper.showEngraveScreenShotShare(it)
                }
            }
        }

        //test
        DslHttp.uploadFileAction = { filePath, callback ->
            callback(
                "https://upcdn.io/FW25b4F/raw/uploads/scribble-diffusion/1.0.0/2023-04-22/scribble_input_3qb16Np3.png",
                null
            )
        }
    }

    override fun initCoreApplication() {
        super.initCoreApplication()
        AppDebugHelper.initDebugAction()
        AppDebugHelper.initCacheAction()

        //debug
        Debug.onShowFragmentAction = { fragmentActivity, show ->
            when (show) {
                "debug" -> fragmentActivity.dslFHelper {
                    show(DebugFragment::class)
                }

                "connect" -> fragmentActivity.dslFHelper {
                    show(ConnectFragment::class)
                }

                "command" -> fragmentActivity.dslFHelper {
                    show(CommandFragment::class)
                }

                "transferData" -> fragmentActivity.dslFHelper {
                    show(TransferDataFragment::class)
                }
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
     * [androidx.camera.lifecycle.ProcessCameraProvider.getOrCreateCameraXInstance]
     *
     * 在此处触发:
     * [androidx.camera.core.CameraX.CameraX]
     * [androidx.camera.core.CameraX.getConfigProvider]
     *
     * @returns Camera2 default configuration
     * */
    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            //.setCameraExecutor(myExecutor)
            //.setSchedulerHandler(mySchedulerHandler)
            //.setAvailableCamerasLimiter()
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()
        //return Camera2Config.defaultConfig()
    }

    override fun enablePadAdaptive(): Boolean = true
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

/**随机文本
 * [line] 需要获取的行数*/
fun getRandomText(
    line: Int = Random.nextInt(1, 3),
    count: Int = Random.nextInt(0, 3)
) = buildString {
    for (i in 0 until line) {
        if (i == 0) {
            append(DrawTextView.DEF)
        } else {
            appendLine()
        }
        append(randomString(count))
    }
}