package com.angcyo.uicore.app

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.angcyo.library.L
import com.angcyo.library.ex.simpleHash

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/14
 */

class AppService : Service() {
    override fun onBind(intent: Intent?): IBinder? {
        L.i("服务:$intent")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        L.i("服务:onCreate")
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
        L.i("服务:$intent $startId")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        L.i("${this.simpleHash()} 服务:$intent $flags $startId")
        return START_STICKY
    }

}