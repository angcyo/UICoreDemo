package com.angcyo.uicore

import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import com.angcyo.base.dslFHelper
import com.angcyo.base.find
import com.angcyo.core.activity.BasePermissionsActivity
import com.angcyo.core.component.model.LanguageModel
import com.angcyo.download.giteeVersionUpdate
import com.angcyo.haveTargetFragment
import com.angcyo.library.L
import com.angcyo.library.component.DslShortcut
import com.angcyo.library.component.MultiFingeredHelper
import com.angcyo.library.component.RSoundPool
import com.angcyo.library.component._delay
import com.angcyo.library.component.dslShortcut
import com.angcyo.library.component.lastContext
import com.angcyo.library.ex.getWifiSSID
import com.angcyo.library.ex.isBuildDebug
import com.angcyo.library.getAppString
import com.angcyo.library.utils.RUtils
import com.angcyo.library.utils.checkApkExist
import com.angcyo.uicore.activity.NfcInfoDemo
import com.angcyo.uicore.component.BaiduTraceService
import com.angcyo.uicore.component.ScreenShotFileObserver
import com.angcyo.uicore.component.ScreenShotFileObserverManager
import com.angcyo.uicore.demo.BuildConfig
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.test.Test

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/17
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */
class MainActivity : BasePermissionsActivity() {

    init {
//        permissions.add(
//            PermissionBean(
//                Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                R.drawable.permission_sd,
//                "SD卡存储权限"
//            )
//        )
//        permissions.add(
//            PermissionBean(
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                R.drawable.permission_gps,
//                "GPS权限"
//            )
//        )
//        permissions.add(
//            PermissionBean(
//                Manifest.permission.CAMERA,
//                R.drawable.permission_camera,
//                "摄像头权限"
//            )
//        )
//        permissions.add(
//            PermissionBean(
//                Manifest.permission.RECORD_AUDIO,
//                R.drawable.permission_record,
//                "麦克风权限"
//            )
//        )

        doubleBackTime = 1_000
        activityPadLayoutId = R.layout.lib_activity_main_pad_layout
    }

    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslShortcut(this) {
            //清空之前, 防止max崩溃
            shortcutAction = DslShortcut.ACTION_TYPE_REMOVE_ALL_SHORTCUT
        }

        dslShortcut(this) {
            shortcutAction = DslShortcut.ACTION_TYPE_DYNAMIC_SHORTCUT
            shortcutLabel = "CSDN博客"
            shortcutIconId = R.drawable.ic_logo_small
            shortcutIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://angcyo.blog.csdn.net"))
        }

//        dslShortcut(this) {
//            shortcutAction = DslShortcut.ACTION_TYPE_DYNAMIC_SHORTCUT
//            shortcutLabel = "官网"
//            shortcutIconId = R.drawable.ic_logo_small
//            shortcutIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.angcyo.com"))
//        }

        dslShortcut(this) {
            shortcutAction = DslShortcut.ACTION_TYPE_DYNAMIC_SHORTCUT
            shortcutLabel = "扫一扫"
            shortcutIconId = R.drawable.app_ic_scan_frame
            shortcutIntent = Intent(this@MainActivity, AppScanActivity::class.java)
        }

        if (checkApkExist("com.tencent.mobileqq")) {
            dslShortcut(this) {
                shortcutAction = DslShortcut.ACTION_TYPE_DYNAMIC_SHORTCUT
                shortcutLabel = "QQ咨询"
                shortcutIconId = R.drawable.ic_logo_small
                shortcutIntent = RUtils.chatQQIntent(this@MainActivity)
            }

            dslShortcut(this) {
                shortcutAction = DslShortcut.ACTION_TYPE_DYNAMIC_SHORTCUT
                shortcutLabel = "QQ入群学习"
                shortcutIconId = R.drawable.ic_logo_small
                shortcutIntent = RUtils.joinQQGroupIntent(this@MainActivity)
            }
        } else {
            dslShortcut(this) {
                shortcutAction = DslShortcut.ACTION_TYPE_DYNAMIC_SHORTCUT
                shortcutLabel = "Github"
                shortcutIconId = R.drawable.ic_logo_small
                shortcutIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/angcyo"))
            }
            dslShortcut(this) {
                shortcutAction = DslShortcut.ACTION_TYPE_DYNAMIC_SHORTCUT
                shortcutLabel = "掘金"
                shortcutIconId = R.drawable.ic_logo_small
                shortcutIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://juejin.im/user/576a151b2e958a00699c11f0")
                )
            }
        }
    }

    override fun onPermissionGranted(savedInstanceState: Bundle?) {
        super.onPermissionGranted(savedInstanceState)
        if (savedInstanceState == null) {
            dslFHelper {
                removeAll()
                restore(MainFragment())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*BaiduTraceService.start(this, BaiduTraceService.FLAG_START)
        //dslBaiduTrace.startTrace(applicationContext)

        RSoundPool().apply {
            //playDefaultRingtone(RingtoneManager.TYPE_RINGTONE)
        }*/
    }

    override fun onStart() {
        super.onStart()
        ScreenShotFileObserverManager.registerScreenShotFileObserver(object :
            ScreenShotFileObserver.ScreenShotLister {
            override fun finishScreenShot(path: String?) {
                L.i("finishScreenShot path = $path")
            }

            override fun beganScreenShot(path: String?) {
                L.i("beganScreenShot path = $path")
            }
        })
    }

    override fun onResume() {
        super.onResume()
        giteeVersionUpdate()
        L.i(buildString {
            append("Wifi:${getWifiSSID()}")
            append(" 本机ip:${getAppString("local_ip")}")
            append(" ${getAppString("versionCode")}/${getAppString("versionName")}")
        })
        //densityAdapter(750, 2f)
        //densityRestore()
        //densityAdapterFrom(2183)
        /*BaiduTraceService.start(this, BaiduTraceService.FLAG_RESUME)*/

        //val b1 = RBackground.isCreatedActivity(MainActivity::class)
        //val b2 = RBackground.isLastActivity(MainActivity::class)

        try {
            //PathTest.test()
            //HttpTest.test()

            Test.test()

            /*if (BuildConfig.BUILD_TYPE.isBuildDebug()) {
                val soundPool: RSoundPool = RSoundPool().apply {
                    init(lastContext)
                    loadDefaultRingtone(RingtoneManager.TYPE_RINGTONE)
                }
                _delay(1000L) {
                    soundPool.playDefaultRingtone(RingtoneManager.TYPE_RINGTONE)
                }
            }*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStop() {
        super.onStop()
        ScreenShotFileObserverManager.unregisterScreenShotFileObserver()
    }

    override fun onDestroy() {
        super.onDestroy()
        BaiduTraceService.start(this, BaiduTraceService.FLAG_DESTROY)
    }

    override fun handleTargetIntent(intent: Intent, fromNewIntent: Boolean) {
        super.handleTargetIntent(intent, fromNewIntent)
        dslFHelper { fm.find(NfcInfoDemo::class)?.updateInfo(intent) }
        MainFragment.JUMP_TO_LOCK_POSITION = !intent.haveTargetFragment()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (LanguageModel.isLanguageConfigurationChanged(newConfig)) {
            //recreate()
        }
    }

    val pinchGestureDetector = MultiFingeredHelper.PinchGestureDetector().apply {
        onPinchAction = {
            L.w("$gesturePointerCount 指捏合...")
        }
    }

    val expandGestureDetector = MultiFingeredHelper.ExpandGestureDetector().apply {
        onExpandAction = {
            L.w("$gesturePointerCount 指散开...")
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        pinchGestureDetector.onTouchEvent(ev)
        expandGestureDetector.onTouchEvent(ev)
        return super.dispatchTouchEvent(ev)
    }
}
