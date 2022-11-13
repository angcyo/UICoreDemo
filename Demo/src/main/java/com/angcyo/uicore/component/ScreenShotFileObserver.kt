package com.angcyo.uicore.component

import android.os.FileObserver
import android.os.Handler
import android.os.Looper
import com.angcyo.library.L
import java.util.concurrent.TimeUnit

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/11/12
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class ScreenShotFileObserver(path: String) : FileObserver(path, ALL_EVENTS) {

    interface ScreenShotLister {
        fun finishScreenShot(path: String?)
        fun beganScreenShot(path: String?)
    }

    /**
     * 监听当次开始截图事件
     */
    private var beganScreenShot = false
    private var screenShotFileName: String? = ""
    private var screenShotDir: String = path
    private var screenShotLister: ScreenShotLister? = null

    /**
     * 由于某些魅族手机保存有延迟且某些魅族系统上只监听到了CREATE事件，短暂延迟后主动数据处理
     */
    private val handler = Handler(Looper.getMainLooper())
    private var run = Runnable {
        L.i("由于某些魅族手机保存有延迟且某些魅族系统上只监听到了CREATE事件，短暂延迟后主动数据处理")
        dealData()
    }

    override fun onEvent(event: Int, path: String?) {
        when (event) {
            CREATE -> {
                L.i("CREATE = $event , s = $path")
                screenShotFileName = path
                beganScreenShot = true
                handler.removeCallbacks(run)
                handler.postDelayed(run, TimeUnit.SECONDS.toMillis(1))
                screenShotLister?.beganScreenShot(screenShotDir.plus(screenShotFileName))
            }
            CLOSE_WRITE -> {
                L.i("CLOSE_WRITE = $event , s = $path")
                screenShotFileName = path
                handler.removeCallbacks(run)
                dealData()
            }
        }
    }

    /**
     * 数据处理
     */
    private fun dealData() {
        beganScreenShot = false
        L.i("screenShotFileName = $screenShotFileName")
        screenShotLister?.finishScreenShot(screenShotDir.plus(screenShotFileName))
    }

    fun setLister(lister: ScreenShotLister) {
        screenShotLister = lister
    }

    fun removeLister() {
        screenShotLister = null
    }
}