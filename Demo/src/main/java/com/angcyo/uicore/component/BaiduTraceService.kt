package com.angcyo.uicore.component

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.angcyo.baidu.trace.DslBaiduTrace
import com.angcyo.core.component.model.BatteryModel
import com.angcyo.core.vmCore
import com.angcyo.library.L
import com.angcyo.library.utils.Device
import com.baidu.trace.model.OnCustomAttributeListener

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/12
 */
class BaiduTraceService : Service() {

    companion object {
        const val FLAG_KEY = "FLAG_KEY"

        const val FLAG_START = 0
        const val FLAG_RESUME = 1
        const val FLAG_DESTROY = 2

        fun start(context: Context, flag: Int) {
            context.startService(Intent(context, BaiduTraceService::class.java).apply {
                putExtra(FLAG_KEY, flag)
            })
        }
    }

    val dslBaiduTrace = DslBaiduTrace().apply {
        serviceId = 207762
        entityName = "${Build.MODEL.replace(" ", "_")}-${Device.androidId}"
        autoTraceStart = true

        customAttributeListener = object : OnCustomAttributeListener {
            override fun onTrackAttributeCallback(): MutableMap<String, String>? {
                val result = hashMapOf<String, String>()
                result["test1"] = "test1"
                result["energy1"] = "${vmCore<BatteryModel>().load(applicationContext).level}"
                result["energy"] = result["energy1"]!!
                L.v("onTrackAttributeCallback1:${result["energy1"]}")
                return result
            }

            //locTime - 回调时定位点的时间戳（毫秒）
            override fun onTrackAttributeCallback(locTime: Long): MutableMap<String, String>? {
                val result = hashMapOf<String, String>()
                result["test2"] = "test2"
                result["energy2"] = "${vmCore<BatteryModel>().load(applicationContext).level}"
                L.v("onTrackAttributeCallback2:${result["energy2"]}")
                return result
            }
        }
    }


    /**
     * [flags] 是系统传入 有如下三种值：
     * 1，通过startService启动时，flags为0；
     * 2，onStartCommand返回为START_STICKY_COMPATIBILITY或者START_STICKY并且服务异常杀死后由系统启动；flags为START_FLAG_REDELIVERY=1：
     * 3，onStartCommand返回为START_REDELIVER_INTENT并且服务异常杀死后由系统启动；
     * flags为START_FLAG_REDELIVERY=2：
     * */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.apply {
            when (getIntExtra(FLAG_KEY, FLAG_START)) {
                FLAG_RESUME -> dslBaiduTrace.updateEntity {
                    columns = dslBaiduTrace.customAttributeListener?.onTrackAttributeCallback()
                }
                FLAG_DESTROY -> stopSelf()
                else -> dslBaiduTrace.startTrace(this@BaiduTraceService)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        dslBaiduTrace.stopTrace()
    }
}