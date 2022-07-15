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
import com.angcyo.library.utils.fileNameUUID
import com.angcyo.uicore.MainActivity
import com.angcyo.uicore.demo.CanvasDemo

/**
 * 使用[CanvasDemo]打开文件
 *
 * 支持的后缀列表
 * [.jpg] [.jpeg] [.png]
 * [.gcode]
 * [.svg]
 *
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/07/15
 */
class CanvasOpenActivity : Activity() {

    companion object {
        const val JPG = "jpg"
        const val JPEG = "jpeg"
        const val PNG = "png"
        const val GCODE = "gcode"
        const val SVG = "svg"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action
        if (action == Intent.ACTION_VIEW) {
            val data = intent.data
            val path = data?.getPathFromUri()
            if (path == null) {
                "路径为空,无法打开".writeErrorLog()
            } else {
                val openPendingData = vmApp<CanvasOpenModel>().openPendingData
                val ext = path.extName()
                val newPath = libCacheFile(fileNameUUID(ext)).absolutePath
                data.saveTo(newPath)//转存文件

                L.i("打开文件:$path ->$newPath")

                val info = when (ext) {
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
                if (info == null) {
                    "不支持的后缀:$ext".writeErrorLog()
                    toast("not support!")
                } else {
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
                }
            }
        } else {
            toast("not support!")
        }
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }
}