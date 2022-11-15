package com.angcyo.uicore

import android.graphics.DashPathEffect
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.ViewPager
import com.angcyo.DslAHelper
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.base.restore
import com.angcyo.bluetooth.fsc.FscBleApiModel
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.parse.toLaserPeckerVersionName
import com.angcyo.bugly.Bugly
import com.angcyo.canvas.laser.pecker.mode.CanvasOpenModel
import com.angcyo.canvas.utils.CanvasConstant
import com.angcyo.core.CoreApplication
import com.angcyo.core.component.CacheFragment
import com.angcyo.core.component.model.CacheInfo
import com.angcyo.core.component.model.CacheModel
import com.angcyo.core.coreApp
import com.angcyo.core.fragment.BaseUI
import com.angcyo.core.viewpager.RFragmentAdapter
import com.angcyo.core.vmApp
import com.angcyo.crash.sight.CrashSight
import com.angcyo.download.DslDownload
import com.angcyo.engrave.auto.AutoEngraveHelper
import com.angcyo.engrave.ble.CommandActivity
import com.angcyo.engrave.ble.DeviceSettingFragment
import com.angcyo.engrave.model.FscDeviceModel
import com.angcyo.http.gitee.Gitee
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.component.DebugFragment
import com.angcyo.item.style.itemInfoText
import com.angcyo.jpush.JPush
import com.angcyo.library.annotation.CallComplianceAfter
import com.angcyo.library.component.FontManager
import com.angcyo.library.component.LibHawkKeys
import com.angcyo.library.component.RBackground
import com.angcyo.library.component.lastContext
import com.angcyo.library.ex.*
import com.angcyo.library.getAppString
import com.angcyo.library.isMainProgress
import com.angcyo.library.utils.Constant
import com.angcyo.library.utils.appFolderPath
import com.angcyo.library.utils.storage.sdDocumentFolderPath
import com.angcyo.library.utils.storage.sdDownloadFolderPath
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

        //LaserPecker
        LPBox.init(this)

        CanvasOpenModel.OPEN_ACTIVITY_CLASS = MainActivity::class.java
        CanvasOpenModel.OPEN_ACTIVITY_FRAGMENT_CLASS = CanvasDemo::class.java
    }

    override fun onCreateMain() {
        super.onCreateMain()

        //url
        Gitee.BASE = "https://gitcode.net/angcyo/file/-/raw/master/UICoreDemo"

        DslTbs.init(this)

        //fsc
        FscBleApiModel.init()
        vmApp<FscDeviceModel>().initDevice()

        //website
        AutoEngraveHelper.init()

        //固件升级
        DeviceSettingFragment.createFirmwareUpdateItemAction = { fragment, adapter ->
            vmApp<LaserPeckerModel>().productInfoData.value?.softwareVersion?.run {
                DslTextInfoItem().apply {
                    itemInfoText = "固件版本"
                    itemDarkText = toLaserPeckerVersionName()
                }
            }
        }
    }

    override fun initCoreApplication() {
        super.initCoreApplication()

        DslAndServer//init

        //WebSocket
        DebugFragment.addDebugAction {
            name = "LogWSServer"
            action = { _, _ ->
                coreApp().bindLogWSServer()
            }
        }

        //cache
        DebugFragment.addDebugAction {
            name = "缓存管理"
            action = { _, _ ->
                RBackground.lastActivityRef?.get()?.apply {
                    if (this is FragmentActivity) {
                        dslFHelper {
                            show(CacheFragment::class)
                        }
                    }
                }
            }
        }

        DebugFragment.addDebugAction {
            label = "图片压缩质量"
            des = "图片压缩的最小保持文件大小(kb)"
            key = LibHawkKeys::minKeepSize.name
            type = Int::class.java
            defValue = LibHawkKeys.minKeepSize
        }

        DebugFragment.addDebugAction {
            name = "指令记录"
            action = { _, _ ->
                lastContext.dslAHelper {
                    start(CommandActivity::class)
                }
            }
        }

        DebugFragment.addDebugAction {
            label = "滑台重复图片间距"
            des = "为滑台重复图片间距。单位mm,保留一位小数。"
            key = LibHawkKeys::lastSlipSpace.name
            type = Float::class.java
            defValue = LibHawkKeys.lastSlipSpace
        }

        DebugFragment.addDebugAction {
            label = "固件支持范围"
            des = "格式:x~xx xx~xxx"
            key = LibHawkKeys::lpSupportFirmware.name
            type = String::class.java
            defValue = LibHawkKeys.lpSupportFirmware.run {
                if (isNullOrBlank()) {
                    getAppString("lp_support_firmware")
                } else {
                    this
                }
            }
        }

        //---

        //font
        val fontsFolderPath =
            sdDownloadFolderPath("${LPBox.DB_NAME}/${FontManager.DEFAULT_FONT_FOLDER_NAME}")
        FontManager.defaultCustomFontFolder = fontsFolderPath

        //cache config
        vmApp<CacheModel>().apply {

            addCacheInfo(CacheInfo("内部缓存", null, cacheDir.absolutePath))
            externalCacheDir?.let {
                addCacheInfo(CacheInfo("外部缓存", null, it.absolutePath))
            }
            addCacheInfo(CacheInfo("应用数据-请慎重删除", "包含程序运行的一些数据", appFolderPath()))
            addCacheInfo(
                CacheInfo(
                    "网络缓存",
                    "网络请求产生的日志",
                    appFolderPath(Constant.HTTP_FOLDER_NAME)
                )
            )
            addCacheInfo(
                CacheInfo(
                    "崩溃缓存",
                    "程序崩溃产生的日志",
                    appFolderPath(Constant.CRASH_FOLDER_NAME)
                )
            )
            addCacheInfo(
                CacheInfo(
                    "图片压缩缓存",
                    "压缩图片时产生的缓存",
                    appFolderPath(Constant.LUBAN_FOLDER_NAME)
                )
            )
            addCacheInfo(
                CacheInfo(
                    "日志缓存",
                    "程序运行时产生的一些日志",
                    appFolderPath(Constant.LOG_FOLDER_NAME)
                )
            )
            /*DEFAULT_FILE_PRINT_PATH?.let {
                addCacheInfo(CacheInfo("日志文件", "程序运行的日志数据", it))
            }*/

            addCacheInfo(
                CacheInfo(
                    "雕刻缓存",
                    "雕刻过程中产生的缓存数据",
                    appFolderPath(CanvasConstant.ENGRAVE_FILE_FOLDER)
                )
            )
            addCacheInfo(
                CacheInfo(
                    "矢量缓存",
                    "生成的矢量文件缓存数据",
                    appFolderPath(CanvasConstant.VECTOR_FILE_FOLDER)
                )
            )
            addCacheInfo(CacheInfo("字体缓存", "导入的自定义字体", FontManager.defaultCustomFontFolder))
            addCacheInfo(
                CacheInfo(
                    "LaserPecker外部缓存",
                    "卸载重新可恢复的数据",
                    sdDocumentFolderPath(LPBox.DB_NAME)
                )
            )
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

/**随机文本
 * [line] 需要获取的行数*/
fun getRandomText(
    line: Int = Random.nextInt(1, 3),
    count: Int = Random.nextInt(0, 3)
) = buildString {
    for (i in 0 until line) {
        if (i == 0) {
            append("angcyo")
        } else {
            appendLine()
        }
        append(randomString(count))
    }
}