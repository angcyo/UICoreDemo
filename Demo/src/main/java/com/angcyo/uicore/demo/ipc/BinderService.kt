package com.angcyo.uicore.demo.ipc

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Parcel
import com.angcyo.core.vmApp
import com.angcyo.library.L

/**
 *
 * https://github.com/yuanhuihui/BinderSample
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/10
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class BinderService : Service() {

    val binder = BinderBinder()

    override fun onCreate() {
        super.onCreate()
        L.w("服务创建!")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        L.w("服务启动:$flags $startId")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    public class BinderBinder : IBinderService.Stub() {

        /*fun addData(data: String): Boolean {
            return vmApp<IpcModel>().dataList.add(data)
        }

        fun getData(): List<String> {
            return vmApp<IpcModel>().dataList
        }*/

        override fun addData(data: IpcData): Boolean {
            return vmApp<IpcModel>().dataList.add(data)
        }

        override fun getData(): MutableList<IpcData> {
            return vmApp<IpcModel>().dataList
        }

        /**此处可用于权限拦截**/
        override fun onTransact(code: Int, data: Parcel, reply: Parcel?, flags: Int): Boolean {
            return super.onTransact(code, data, reply, flags)
        }
    }
}