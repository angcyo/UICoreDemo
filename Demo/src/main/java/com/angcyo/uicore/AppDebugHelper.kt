package com.angcyo.uicore

import androidx.fragment.app.FragmentActivity
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.bluetooth.fsc.laserpacker.HawkEngraveKeys
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker._deviceSettingBean
import com.angcyo.bluetooth.fsc.laserpacker.bean._pathTolerance
import com.angcyo.canvas2.laser.pecker.manager.FileManagerFragment
import com.angcyo.core.component.CacheFragment
import com.angcyo.core.component.model.CacheInfo
import com.angcyo.core.component.model.CacheModel
import com.angcyo.core.coreApp
import com.angcyo.core.vmApp
import com.angcyo.download.version.VersionChangeFragment
import com.angcyo.item.component.DebugFragment
import com.angcyo.laserpacker.LPDataConstant
import com.angcyo.laserpacker.device.DeviceHelper
import com.angcyo.laserpacker.device.ble.CommandFragment
import com.angcyo.laserpacker.device.ble.ConnectFragment
import com.angcyo.laserpacker.device.ble.EngraveExperimentalFragment
import com.angcyo.laserpacker.device.ble.TransferDataFragment
import com.angcyo.laserpacker.device.wifi.AddWifiDeviceFragment
import com.angcyo.library.annotation.CallPoint
import com.angcyo.library.component.FontManager
import com.angcyo.library.component.RBackground
import com.angcyo.library.component.hawk.LibHawkKeys
import com.angcyo.library.component.hawk.LibLpHawkKeys
import com.angcyo.library.component.lastContext
import com.angcyo.library.utils.Constant
import com.angcyo.library.utils.appFolderPath
import com.angcyo.server.DslAndServer
import com.angcyo.server.bindFileServer
import com.angcyo.websocket.service.bindLogWSServer

/**
 * 调试action
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/11/22
 */
object AppDebugHelper {

    @CallPoint
    fun initDebugAction() {
        DslAndServer//init

        //WebSocket
        DebugFragment.addDebugAction {
            name = "LogWSServer"
            action = { _, _ ->
                coreApp().bindLogWSServer()
            }
        }
        //log file + ws
        DebugFragment.addDebugAction {
            name = "LogServer"
            action = { _, _ ->
                coreApp().apply {
                    bindFileServer()
                    bindLogWSServer()
                }
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
                    start(CommandFragment::class)
                }
            }
        }

        DebugFragment.addDebugAction {
            name = "连接记录"
            action = { _, _ ->
                lastContext.dslAHelper {
                    start(ConnectFragment::class)
                }
            }
        }

        DebugFragment.addDebugAction {
            name = "数据记录"
            action = { _, _ ->
                lastContext.dslAHelper {
                    start(TransferDataFragment::class)
                }
            }
        }

        DebugFragment.addDebugAction {
            name = "更新记录"
            action = { _, _ ->
                lastContext.dslAHelper {
                    start(VersionChangeFragment::class)
                }
            }
        }

        DebugFragment.addDebugAction {
            name = "Wifi配网"
            action = { _, _ ->
                lastContext.dslAHelper {
                    start(AddWifiDeviceFragment::class)
                }
            }
        }

        DebugFragment.addDebugAction {
            name = "LP实验性"
            action = { _, _ ->
                lastContext.dslAHelper {
                    start(EngraveExperimentalFragment::class)
                }
            }
        }

        DebugFragment.addDebugAction {
            name = "LP文件管理"
            action = { _, _ ->
                lastContext.dslAHelper {
                    start(FileManagerFragment::class)
                }
            }
        }

        //---

        DebugFragment.addDebugAction {
            label = "创作功能支持的固件范围"
            des = "格式:x~xx xx~xxx"
            key = LibLpHawkKeys::lpSupportFirmware.name
            type = String::class.java
            defValue = LibLpHawkKeys.lpSupportFirmware.run {
                if (isNullOrBlank()) {
                    LaserPeckerHelper.supportFirmwareRange()
                } else {
                    this
                }
            }
        }

        DebugFragment.addDebugAction {
            label = "支持多文件雕刻的固件范围"
            des = "格式:x~xx xx~xxx"
            key = HawkEngraveKeys::batchEngraveSupportFirmware.name
            type = String::class.java
            defValue = HawkEngraveKeys.batchEngraveSupportFirmware.run {
                if (isNullOrBlank()) {
                    DeviceHelper.batchEngraveSupportFirmware
                } else {
                    this
                }
            }
        }

        DebugFragment.addDebugAction {
            label = "支持自动激光雕刻的固件范围"
            des = "格式:x~xx xx~xxx"
            key = HawkEngraveKeys::autoCncEngraveSupportFirmware.name
            type = String::class.java
            defValue = HawkEngraveKeys.autoCncEngraveSupportFirmware.run {
                if (isNullOrBlank()) {
                    _deviceSettingBean?.autoCncRange
                } else {
                    this
                }
            }
        }

        DebugFragment.addDebugAction {
            label = "支持GCode转换成路径数据的固件范围"
            des = "格式:x~xx xx~xxx"
            key = HawkEngraveKeys::gcodeUsePathDataSupportFirmware.name
            type = String::class.java
            defValue = HawkEngraveKeys.gcodeUsePathDataSupportFirmware.run {
                if (isNullOrBlank()) {
                    _deviceSettingBean?.gcodeUsePathDataRange
                } else {
                    this
                }
            }
        }

        DebugFragment.addDebugAction {
            label = "激活设备过滤"
            des = "蓝牙搜索列表,设备数量大于多少时,显示过滤布局."
            key = HawkEngraveKeys::showDeviceFilterCount.name
            type = Int::class.java
            defValue = HawkEngraveKeys.showDeviceFilterCount
        }

        DebugFragment.addDebugAction {
            label = "传输数据大小限制"
            des = "限制最大发送给机器的数据量大小(字节)"
            key = HawkEngraveKeys::maxTransferDataSize.name
            type = Long::class.java
            defValue = HawkEngraveKeys.maxTransferDataSize
        }

        DebugFragment.addDebugAction {
            label = "外部打开文件大小限制"
            des = "限制最大打开的文件大小(字节)"
            key = HawkEngraveKeys::openFileDataSize.name
            type = Long::class.java
            defValue = HawkEngraveKeys.openFileDataSize
        }

        DebugFragment.addDebugAction {
            label = "透明颜色阈值"
            des = "当不透明度小于此值时, 视为透明颜色"
            key = LibHawkKeys::alphaThreshold.name
            type = Int::class.java
            defValue = LibHawkKeys.alphaThreshold
        }

        DebugFragment.addDebugAction {
            label = "灰度颜色阈值"
            des = "当色值大于这个值时,视为白色,否则就是黑色"
            key = LibHawkKeys::grayThreshold.name
            type = Int::class.java
            defValue = LibHawkKeys.grayThreshold
        }

        DebugFragment.addDebugAction {
            label = "最大允许雕刻数量"
            des = "可以同时勾选多少个元素雕刻"
            key = HawkEngraveKeys::maxEngraveItemCountLimit.name
            type = Int::class.java
            defValue = HawkEngraveKeys.maxEngraveItemCountLimit
        }

        DebugFragment.addDebugAction {
            label = "弧形公差(mm)"
            des = "矢量拟合公差,差值内的弧形视为直线."
            key = LibHawkKeys::pathTolerance.name
            type = Float::class.java
            defValue = _pathTolerance
        }

        DebugFragment.addDebugAction {
            label = "路径采样步长(mm)"
            des = "每隔多少mm采样一个点"
            key = LibHawkKeys::pathAcceptableErrorMM.name
            type = Float::class.java
            defValue = LibHawkKeys.pathAcceptableErrorMM
        }

        DebugFragment.addDebugAction {
            label = "Wifi发送延迟字节数(bytes)"
            des = "每发送多少字节后,进行延迟."
            key = LibLpHawkKeys::wifiSendDelayByteCount.name
            type = Long::class.java
            defValue = LibLpHawkKeys.wifiSendDelayByteCount
            useNewNumberKeyboardDialog = true
        }

        DebugFragment.addDebugAction {
            label = "Wifi发送延迟时长(毫秒)"
            des = "延迟时长"
            key = LibLpHawkKeys::wifiSendDelay.name
            type = Long::class.java
            defValue = LibLpHawkKeys::wifiSendDelay
            useNewNumberKeyboardDialog = true
        }

        DebugFragment.addDebugAction {
            label = "支架自动回升"
            des = "切片浮雕雕刻完成后, 支持自动回升到开始的位置."
            key = HawkEngraveKeys::autoPickUp.name
            type = Boolean::class.java
            defValue = HawkEngraveKeys.autoPickUp
        }

        DebugFragment.addDebugAction {
            label = "使用调试配置信息"
            des = "使用调试device_config/setting_config配置信息, 用于测试"
            key = HawkEngraveKeys::useDebugConfig.name
            type = Boolean::class.java
            defValue = HawkEngraveKeys.useDebugConfig
        }

        /*DebugFragment.addDebugAction {
            label = "激活传输数据时的索引检查"
            des = "关闭后,所有数据直接传输,不检查机器是否已存在数据."
            key = HawkEngraveKeys::enableTransferIndexCheck.name
            type = Boolean::class.java
            defValue = HawkEngraveKeys.enableTransferIndexCheck
        }

        DebugFragment.addDebugAction {
            label = "激活GCode G2/G3指令输出"
            des = "矢量图形转GCode算法时,是否激活G2/G3指令"
            key = LibHawkKeys::enableVectorArc.name
            type = Boolean::class.java
            defValue = LibHawkKeys.enableVectorArc
        }

        DebugFragment.addDebugAction {
            label = "激活第三方GCode数据全转换"
            des = "激活后,将会重新生成第三方GCode数据,而不是在原始数据基础上修改."
            key = HawkEngraveKeys::enableGCodeTransform.name
            type = Boolean::class.java
            defValue = HawkEngraveKeys.enableGCodeTransform
        }*/
    }

    @CallPoint
    fun initCacheAction() {
        //cache config
        val context = lastContext
        vmApp<CacheModel>().apply {

            addCacheInfo(CacheInfo("内部缓存", null, context.cacheDir.absolutePath))
            context.externalCacheDir?.let {
                addCacheInfo(CacheInfo("外部缓存", null, it.absolutePath))
            }
            addCacheInfo(
                CacheInfo(
                    "应用数据-请慎重删除",
                    "包含程序运行的一些数据",
                    appFolderPath()
                )
            )
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
                    appFolderPath(LPDataConstant.ENGRAVE_FILE_FOLDER)
                )
            )
            addCacheInfo(
                CacheInfo(
                    "矢量缓存",
                    "生成的矢量文件缓存数据",
                    appFolderPath(LPDataConstant.VECTOR_FILE_FOLDER)
                )
            )
            addCacheInfo(
                CacheInfo(
                    "字体缓存",
                    "导入的自定义字体",
                    FontManager.defaultCustomFontFolder
                )
            )
            /*addCacheInfo(
                CacheInfo(
                    "LaserPecker外部缓存",
                    "卸载重新可恢复的数据",
                    sdDocumentFolderPath(LPBox.DB_NAME)
                )
            )*/
        }
    }

}