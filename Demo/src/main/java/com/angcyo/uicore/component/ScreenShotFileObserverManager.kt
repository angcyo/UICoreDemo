package com.angcyo.uicore.component

import android.os.Environment
import com.angcyo.library.L
import java.io.File

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/11/12
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
object ScreenShotFileObserverManager {

    private var screenShotFileObserver: ScreenShotFileObserver? = null

    /**
     * 截屏依据中的截屏文件夹判断关键字
     */
    private val KEYWORDS = listOf(
        "screenshots", "screenshot", "screen_shot", "screen-shot", "screen shot",
        "screencapture", "screen_capture", "screen-capture", "screen capture",
        "screencap", "screen_cap", "screen-cap", "screen cap", "截屏"
    )

    /**
     * 截屏文件夹根路径
     */
    private val SCREENSHOT_ROOT_PATH: String =
        Environment.getExternalStorageDirectory().path + File.separator + Environment.DIRECTORY_DCIM + File.separator

    /**
     * 截屏文件夹路径
     */
    private var screenshot_path = SCREENSHOT_ROOT_PATH

    /**
     * 注册内容监听
     */
    fun registerScreenShotFileObserver(lister: ScreenShotFileObserver.ScreenShotLister) {
        //L.i("registerScreenShotFileObserver")
        //L.i("screenshot_root_path = $SCREENSHOT_ROOT_PATH")
        //L.i("screenshot_path = $screenshot_path")
        screenShotFileObserver = screenShotFileObserver ?: let {
            for (keyword in KEYWORDS) {
                val s = SCREENSHOT_ROOT_PATH.plus(keyword)
                //L.i("搜查 keyword = $keyword")
                //L.i("s = $s")
                if (File(s).exists()) {
                    screenshot_path = s.plus(File.separator)// 找到了截图文件夹
                    L.i("找到了截图文件夹 path = $screenshot_path")
                    break
                }
            }
            ScreenShotFileObserver(screenshot_path)
        }
        screenShotFileObserver?.setLister(lister)
        screenShotFileObserver?.startWatching()
    }

    /**
     * 注销内容监听
     */
    fun unregisterScreenShotFileObserver() {
        L.i("unregisterScreenShotFileObserver")
        screenShotFileObserver?.removeLister()
        screenShotFileObserver?.stopWatching()
    }
}