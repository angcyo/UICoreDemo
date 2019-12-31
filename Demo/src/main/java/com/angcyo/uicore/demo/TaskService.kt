package com.angcyo.uicore.demo

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.os.SystemClock
import android.util.Log

/**
 * 周期性任务调度系统
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/17
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
open class TaskService : Service() {

    companion object {
        const val TAG = "TaskService"
        const val ACTION = "TaskService.Alarm"
        const val KEY_SCHEDULE_DELAY_MILLISECONDS = "key_schedule_delay_milliseconds"

        fun start(context: Context, scheduleDelayMilliseconds: Long = 5 * 1000) {
            context.startService(Intent(context, TaskService::class.java).apply {
                putExtra(KEY_SCHEDULE_DELAY_MILLISECONDS, scheduleDelayMilliseconds)
            })
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, TaskService::class.java))
        }

        fun _start(context: Context) {
            context.startService(Intent(context, TaskService::class.java))
        }
    }

    /**触发Alarm广播*/
    lateinit var pendingIntent: PendingIntent
    /**Alarm广播接收*/
    lateinit var alarmReceiver: BroadcastReceiver

    /**调度延迟时间间隔, 毫秒*/
    var scheduleDelayMilliseconds: Long = 5 * 1000

    override fun onCreate() {
        pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            Intent(ACTION),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        alarmReceiver = AlarmReceiver()
        registerReceiver(alarmReceiver, IntentFilter(ACTION))
    }

    override fun onDestroy() {
        super.onDestroy()
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        unregisterReceiver(alarmReceiver)
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "$intent $flags $startId")
        intent?.apply {
            if (hasExtra(KEY_SCHEDULE_DELAY_MILLISECONDS)) {
                scheduleDelayMilliseconds =
                    getLongExtra(KEY_SCHEDULE_DELAY_MILLISECONDS, scheduleDelayMilliseconds)
            }

            if (scheduleDelayMilliseconds >= 0) {
                _schedule(scheduleDelayMilliseconds)
            }
        }
        return START_STICKY
    }


    /**
     * 安排一个任务
     * @param delayInMilliseconds 需要延迟多久
     * */
    fun _schedule(delayInMilliseconds: Long) {
        val nextAlarmInMilliseconds = (System.currentTimeMillis() + delayInMilliseconds)
        Log.d(TAG, "Schedule next alarm at $nextAlarmInMilliseconds")

        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= 23) { // In SDK 23 and above, dosing will prevent setExact, setExactAndAllowWhileIdle will force
            // the device to run this task whilst dosing.
            Log.d(TAG, "Alarm scheule using setExactAndAllowWhileIdle, next: $delayInMilliseconds")
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarmInMilliseconds,
                pendingIntent
            )
        } else if (Build.VERSION.SDK_INT >= 19) {
            Log.d(TAG, "Alarm scheule using setExact, delay: $delayInMilliseconds")
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP, nextAlarmInMilliseconds,
                pendingIntent
            )
        } else {
            alarmManager[AlarmManager.RTC_WAKEUP, nextAlarmInMilliseconds] = pendingIntent
        }
    }

    /**重写此方法, 执行任务*/
    open fun onTaskRun() {

    }

    /**定时广播接收器*/
    inner class AlarmReceiver : BroadcastReceiver() {
        var wakelock: WakeLock? = null
        val wakeLockTag = SystemClock.elapsedRealtime().toString()

        override fun onReceive(context: Context, intent: Intent?) {
            val pm = this@TaskService.getSystemService(POWER_SERVICE) as PowerManager
            wakelock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, wakeLockTag)

            //需要权限[android.permission.WAKE_LOCK]
            wakelock?.acquire(10 * 60 * 1000L /*10 minutes*/)

            _start(context)

            wakelock?.release()

            onTaskRun()
        }
    }
}
