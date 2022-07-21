package com.angcyo.uicore.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.angcyo.base.dslAHelper
import com.angcyo.core.component.file.writeErrorLog
import com.angcyo.core.vmApp
import com.angcyo.library.L
import com.angcyo.library.ex.*
import com.angcyo.library.libCacheFile
import com.angcyo.library.toast
import com.angcyo.library.utils.ImageTypeUtil
import com.angcyo.putData
import com.angcyo.uicore.MainActivity
import com.angcyo.uicore.activity.firmware.FirmwareUpdateFragment
import com.angcyo.uicore.demo.CanvasDemo

/**
 * 使用[CanvasDemo]打开文件
 *
 * 支持的后缀列表
 * [.jpg] [.jpeg] [.png]
 * [.gcode]
 * [.svg]
 *
 * 支持字体导入
 * [.ttf] [.otf] [.ttc]
 *
 * 支持固件升级
 * [.lpbin]
 *
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/07/15
 */
class CanvasOpenActivity : Activity() {

    companion object {

        /**创作支持的数据类型*/
        const val JPG = "jpg"
        const val JPEG = "jpeg"
        const val PNG = "png"
        const val GCODE = "gcode"
        const val SVG = "svg"

        /**字体*/
        const val FONT = "font"

        /**固件*/
        const val FIRMWARE = "firmware"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val info = parseOpenInfo(intent)
        if (info == null) {
            toast("not support!")
            finish()
        } else if (info.type == FONT) {
            //导入的字体数据
            dslAHelper {
                finishSelf = true
                start(CanvasFontImportActivity::class.java) {
                    putData(info)
                }
            }
        } else if (info.type == FIRMWARE) {
            //固件升级的数据
            dslAHelper {
                finishSelf = true
                start(FirmwareUpdateActivity::class.java) {
                    putData(info)
                }
            }
        } else if (info.type == JPG || info.type == GCODE || info.type == SVG) {
            //创作数据
            val openPendingData = vmApp<CanvasOpenModel>().openPendingData
            if (openPendingData.hasObservers()) {
                //有监听者
                openApp()
            } else {
                //无监听者
                dslAHelper {
                    startFragment(MainActivity::class.java, CanvasDemo::class.java)
                }
            }
            openPendingData.postValue(info)
            finish()
        } else {
            toast("not support!")
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    /**解析数据*/
    fun parseOpenInfo(intent: Intent): CanvasOpenInfo? {
        val action = intent.action

        val data = when (action) {
            Intent.ACTION_VIEW -> intent.data
            Intent.ACTION_SEND -> intent.extras?.getParcelable(Intent.EXTRA_STREAM)
            else -> null
        }
        L.i("解析:$action $data")

        if (data != null) {
            val path = data.getPathFromUri()
            if (path == null) {
                "路径为空,无法打开".writeErrorLog()
                return null
            } else {
                val name = path.lastName()
                val ext = path.extName()
                val newPath = libCacheFile(name).absolutePath
                data.saveTo(newPath)//转存文件

                L.i("处理文件:$path ->$newPath")

                val info = if (path.isFontType()) {
                    //需要导入字体
                    CanvasOpenInfo(FONT, newPath)
                } else if (path.endsWith(FirmwareUpdateFragment.FIRMWARE_EXT)) {
                    //固件升级
                    CanvasOpenInfo(FIRMWARE, newPath)
                } else {
                    when (ext) {
                        JPG, JPEG, PNG -> CanvasOpenInfo(JPG, newPath)
                        GCODE -> CanvasOpenInfo(GCODE, newPath)
                        SVG -> CanvasOpenInfo(SVG, newPath)
                        else -> if (ImageTypeUtil.getImageType(data.inputStream()) == null) {
                            null
                        } else {
                            //图片数据
                            CanvasOpenInfo(JPG, newPath)
                        }
                    }
                }
                return info
            }
        }
        return null
    }
}