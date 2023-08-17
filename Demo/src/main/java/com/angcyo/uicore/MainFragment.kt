package com.angcyo.uicore

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.behavior.HideTitleBarBehavior
import com.angcyo.behavior.refresh.RefreshContentBehavior
import com.angcyo.behavior.refresh.ScaleHeaderRefreshEffectConfig
import com.angcyo.core.activity.BaseCoreAppCompatActivity
import com.angcyo.core.dslitem.DslLastDeviceInfoItem
import com.angcyo.download.giteeVersionChange
import com.angcyo.download.version.VersionChangeFragment
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.exoplayer.SingleExoPlayerActivity
import com.angcyo.github.window.dslFloatWindow
import com.angcyo.http.ApiKt
import com.angcyo.http.dslHttp
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.component.DebugFragment
import com.angcyo.item.style.itemInfoText
import com.angcyo.laserpacker.device.engraveLoadingAsync
import com.angcyo.library.L
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.isDebuggerConnected
import com.angcyo.library.ex.isListEmpty
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.padding
import com.angcyo.library.ex.reveal
import com.angcyo.library.ex.sync
import com.angcyo.library.ex.toDpi
import com.angcyo.library.ex.urlIntent
import com.angcyo.library.getAppVersionName
import com.angcyo.library.toastQQ
import com.angcyo.library.utils.RUtils
import com.angcyo.library.utils.checkApkExist
import com.angcyo.server.bindAndServer
import com.angcyo.uicore.activity.FragmentInFragmentActivity
import com.angcyo.uicore.activity.NfcHandleActivity
import com.angcyo.uicore.activity.ViewPager2InFragmentActivity
import com.angcyo.uicore.activity.ViewPager2InViewPager2Activity
import com.angcyo.uicore.activity.ViewPagerInFragmentActivity
import com.angcyo.uicore.activity.ViewPagerInViewPagerActivity
import com.angcyo.uicore.base.BaseDemoDslFragment
import com.angcyo.uicore.demo.BuildConfig
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.dslitem.AppMenuFooterItem
import com.angcyo.uicore.dslitem.AppMenuHeaderItem
import com.angcyo.uicore.test.coroutineTest
import com.angcyo.uicore.test.rxJavaTest
import com.angcyo.uicore.test.test
import com.angcyo.uicore.test.testHttp
import com.angcyo.widget.base.behavior
import com.angcyo.widget.layout.SliderMenuLayout
import com.angcyo.widget.recycler.allViewHolder
import com.angcyo.widget.recycler.get
import com.angcyo.widget.recycler.initDslAdapter
import com.yhao.floatwindow.IFloatWindowImpl

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2019/12/24
 * Copyright (c) 2019 ShenZhen O&M Cloud Co., Ltd. All rights reserved.
 */

class MainFragment : BaseDemoDslFragment() {

    companion object {
        var CREATE_COUNT = 0
        var CLICK_COUNT = 0

        /**是否跳转到指定位置*/
        var JUMP_TO_LOCK_POSITION = true
    }

    /**自动跳转*/
    var autoJump: Boolean = true

    init {
        fragmentLayoutId = R.layout.fragment_main
        goFirst = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CREATE_COUNT++

        if (BuildConfig.DEBUG) {
            bindAndServer()
        }
    }

    override fun onDoubleTitleLayout(): Boolean {
        /*if (isDebug()) {
            dslAHelper {
                start(TestActivity::class.java)
            }
            return true
        }*/
        _jumpToLockPosition()
        return true
    }

    override fun onBackPressed(): Boolean {
        return _vh.v<SliderMenuLayout>(R.id.menu_layout)?.requestBackPressed() == true
    }

    lateinit var menuAdapter: DslAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //left
        leftControl()?.appendItem(ico = R.drawable.app_ic_menu, action = {
            gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            padding(0)
        }) {
            _vh.v<SliderMenuLayout>(R.id.menu_layout)?.openMenu()
        }
        //right
        rightControl()?.appendItem(ico = R.drawable.ic_android_debug) {
            dslFHelper {
                show(DebugFragment::class)
            }
        }
        //menu
        _vh.rv(R.id.menu_recycler_view)?.apply {
            behavior()?.also {
                if (it is RefreshContentBehavior) {
                    it.refreshBehaviorConfig = ScaleHeaderRefreshEffectConfig().apply {
                        scaleThreshold = Float.MAX_VALUE
                    }
                }
            }
            initDslAdapter {
                renderMenu()
            }
        }
    }

    /**渲染菜单*/
    fun DslAdapter.renderMenu() {
        render {

            menuAdapter = this
            AppMenuHeaderItem()()

            val insert = 10 * dpi
            val subInsert = 2 * dpi
            DslTextInfoItem()() {
                itemTopInsert = insert
                itemInfoText = "扫一扫"
                itemClick = {
                    dslAHelper {
                        start(Intent(context, AppScanActivity::class.java))
                    }
                }
            }

            DslTextInfoItem()() {
                itemTopInsert = insert
                itemInfoText = "QQ咨询"
                itemClick = {
                    if (fContext().checkApkExist("com.tencent.mobileqq")) {
                        dslAHelper {
                            start(RUtils.chatQQIntent(context)!!)
                        }
                    } else {
                        toastQQ("请安装QQ")
                    }
                }
            }

            DslTextInfoItem()() {
                itemTopInsert = subInsert
                itemInfoText = "QQ入群学习"
                itemClick = {
                    if (fContext().checkApkExist("com.tencent.mobileqq")) {
                        dslAHelper {
                            start(RUtils.joinQQGroupIntent(context)!!)
                        }
                    } else {
                        toastQQ("请安装QQ")
                    }
                }
            }

            DslTextInfoItem()() {
                itemTopInsert = insert
                itemInfoText = "CSDN博客"
                itemClick = {
                    dslAHelper {
                        start("https://angcyo.blog.csdn.net".urlIntent())
                    }
                }
            }

            DslTextInfoItem()() {
                itemTopInsert = subInsert
                itemInfoText = "Github"
                itemClick = {
                    dslAHelper {
                        start("https://github.com/angcyo".urlIntent())
                    }
                }
            }

            DslTextInfoItem()() {
                itemTopInsert = subInsert
                itemInfoText = "掘金"
                itemClick = {
                    dslAHelper {
                        start("https://juejin.im/user/576a151b2e958a00699c11f0".urlIntent())
                    }
                }
            }

            DslTextInfoItem()() {
                itemTopInsert = subInsert
                itemInfoText = "官网"
                itemClick = {
                    dslAHelper {
                        start("https://www.angcyo.com".urlIntent())
                    }
                }
            }

            AppMenuFooterItem()()
        }
    }

    override fun onCreateBehavior(child: View): CoordinatorLayout.Behavior<*>? {
        if (child.id == R.id.lib_title_wrap_layout) {
            return HideTitleBarBehavior(fContext()).apply {
                enableOverScroll = true
                enableEdgeScroll = enableOverScroll
            }
        }
        return super.onCreateBehavior(child)
    }

    override fun onFragmentFirstShow(bundle: Bundle?) {
        super.onFragmentFirstShow(bundle)

        launchLifecycle {
            val url = "https://www.mxnzp.com/api/tools/system/time"
            L.i("开始请求:")
            try {
                val response = dslHttp(ApiKt::class.java)?.post(url, headerMap = App.headerMap)
                L.i(response)
            } catch (e: Exception) {
                L.e("捕捉异常:$e")
                throw e
            }
        }

        //揭露动画
        if (CREATE_COUNT % 2 != 0) {
            //偶数次
            _vh.itemView.reveal {
                animator?.duration = 700
            }
        }
    }

    fun _jumpToLockPosition() {
        var position = lastJumpPosition
        if (position < 0) {
            position = lockDemoPosition
        }
        if (position >= 0) {
            _vh.postDelay(300) {
                _adapter[position]?.itemClick?.invoke(_vh.itemView)
            }
        }
    }

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)

        //1毫米等于多少像素
        val dm: DisplayMetrics = resources.displayMetrics
        val mmPixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, 1f, dm) //21.176456

        //1英寸等于多少像素, 1英寸=2.54厘米=25.4毫米
        val inPixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_IN, 1f, dm) //537.882

        coroutineTest()
        rxJavaTest()
        test()
        testHttp()

        _vh.rv(R.id.lib_recycler_view)?.apply {
            allViewHolder().forEachIndexed { index, dslViewHolder ->
                //L.i("$index ${dslViewHolder.adapterPosition}")
            }

            L.i("first:${this[0, true]?.adapterPosition} last:${this[-1, true]?.adapterPosition}")
        }
    }

    override fun onInitFragment(savedInstanceState: Bundle?) {
        super.onInitFragment(savedInstanceState)
        renderDslAdapter {
            renderDemo(savedInstanceState)
        }
    }

    /**渲染demo*/
    fun DslAdapter.renderDemo(savedInstanceState: Bundle?) {

        renderDemoListItem("Version", 10.toDpi(), init = {
            itemDarkText = getAppVersionName()
        }) {
            engraveLoadingAsync({
                //版本更新记录
                sync<Unit> { countDownLatch, atomicReference ->
                    giteeVersionChange() { list, error ->
                        if (!list.isListEmpty()) {
                            VersionChangeFragment.versionChangeList = list
                            dslFHelper {
                                show(VersionChangeFragment::class)
                            }
                        }
                        countDownLatch.countDown()
                    }
                }
            })
        }

        renderDemoListItem("FragmentInFragmentDemo") {
            dslAHelper {
                start(FragmentInFragmentActivity::class.java)
            }
        }
        renderDemoListItem("ViewPagerInFragmentDemo") {
            dslAHelper {
                start(ViewPagerInFragmentActivity::class.java)
            }
        }
        renderDemoListItem("ViewPagerInViewPagerDemo") {
            dslAHelper {
                start(ViewPagerInViewPagerActivity::class.java)
            }
        }
        renderDemoListItem("ViewPager2InFragmentDemo") {
            dslAHelper {
                start(ViewPager2InFragmentActivity::class.java)
            }
        }
        renderDemoListItem("ViewPager2InViewPager2Demo") {
            dslAHelper {
                start(ViewPager2InViewPager2Activity::class.java)
            }
        }

        renderDemoListItem("DrawableDemo")
        renderDemoListItem("DrawableSpanDemo")
        renderDemoListItem("WidgetDemo ArcLoadingView")
        renderDemoListItem("LoadingDemo")
        renderDemoListItem("LoadingDemo2")
        renderDemoListItem("HttpDemo")
        renderDemoListItem("WifiP2PDemo")
        renderDemoListItem("UdpDemo")
        renderDemoListItem("WebSocketDemo")
        renderDemoListItem("RefreshEffectDemo DslToast")
        renderDemoListItem("RefreshDemo SwipeMenu")
        renderDemoListItem("DslAffectDemo")
        renderDemoListItem("ValueTextWatcherDemo DslSoftInputLayout")
        renderDemoListItem("DslSoftInputDemo")
        renderDemoListItem("ViewModelDemo")
        renderDemoListItem("ViewGroupOverlayDemo")
        renderDemoListItem("TransitionDemo")
        renderDemoListItem("GlideImageDemo")
        renderDemoListItem("RegularPatternDemo")
        renderDemoListItem("OkDownloadDemo")
        renderDemoListItem("MediaPickerDemo")
        renderDemoListItem("DialogDemo")
        renderDemoListItem("NotifyDemo ContentObserver")
        renderDemoListItem("CameraXDemo $GO")
        renderDemoListItem("StyleDemo ThemeStyledAttributes")
        renderDemoListItem("ShortcutDemo")
        renderDemoListItem("DslDrawItemDecorationDemo")
        renderDemoListItem("QrCodeDemo")
        renderDemoListItem("TbsWebDemo")
        renderDemoListItem("IntentDemo")
        renderDemoListItem("LauncherDemo")
        renderDemoListItem("GameRenderEngineDemo")
        renderDemoListItem("JsoupDemo")
        renderDemoListItem("PagerLayoutManagerDemo")
        renderDemoListItem("LinkageSvBehaviorDemo Sv+Sv")
        renderDemoListItem("LinkageSingleBehaviorDemo Sv+Rv")
        renderDemoListItem("LinkageRvBehaviorDemo Rv+Rv")
        renderDemoListItem("LinkageVpBehaviorDemo Rv+Vp")
        renderDemoListItem("TitleBarBehaviorDemo")
        renderDemoListItem("TitleBarDrawableBehaviorDemo")
        renderDemoListItem("WaveLayerDemo")
        renderDemoListItem("LineChartDemo")
        renderDemoListItem("BarChartDemo")
        renderDemoListItem("PieChartDemo")
        renderDemoListItem("OtherChartDemo")
        renderDemoListItem("FloatWindowDemo") {
            fContext().dslFloatWindow {
                floatLayoutId = R.layout.float_window_layout

                initFloatLayout = { holder ->
                    holder.clickItem {
                        toastQQ("click...${nowTimeString()}")
                    }
                }
            }.apply {
                show()
                if (this is IFloatWindowImpl) {
                    this.resetPosition(getView(), false)
                }
            }
        }
        renderDemoListItem("ObjectBoxDemo")
        renderDemoListItem("AudioRecordDemo Player")
        renderDemoListItem("TakeMediaDemo")
        renderDemoListItem("SensorDemo")
        renderDemoListItem("FocusDemo")
        renderDemoListItem("RabbitMQDemo")
        renderDemoListItem("WaveViewDemo")
        renderDemoListItem("MoveBehaviorDemo")
        renderDemoListItem("AMapDemo")
        renderDemoListItem("LockDemo Speech")
        renderDemoListItem("AccessibilityDemo")
        renderDemoListItem("GithubDemo")
        renderDemoListItem("LayerDemo ILayer Step")
        renderDemoListItem("TbsJsBridgeDemo")
        renderDemoListItem("SkeletonViewDemo")
        renderDemoListItem("OkHttpProgressDemo")
        renderDemoListItem("ExDialogDemo")
        renderDemoListItem("NavigationDemo")
        renderDemoListItem("CalendarDemo")
        renderDemoListItem("BiometricDemo Finger")
        renderDemoListItem("ComponentDemo")
        renderDemoListItem("NfcDemo") {
            dslAHelper {
                start(NfcHandleActivity::class.java)
            }
        }
        renderDemoListItem("BinderDemo IPC")
        renderDemoListItem("StationDemo")
        renderDemoListItem("NinePatchDemo")
        renderDemoListItem("PathDemo")
        renderDemoListItem("MultiLanguageDemo")
        renderDemoListItem("BluetoothDemo")
        renderDemoListItem("FscBleApiDemo")
        renderDemoListItem("SvgDemo GCode")
        renderDemoListItem("MatrixDemo")
        renderDemoListItem("MatrixSkewDemo")
        renderDemoListItem("GroupScaleDemo")
        renderDemoListItem("DrawPathDemo")
        renderDemoListItem("DrawTextDemo")
        renderDemoListItem("PaintDemo")
        renderDemoListItem("TextSizeDemo")
        renderDemoListItem("ColorChannelDemo")
        renderDemoListItem("RectScaleDemo")
        renderDemoListItem("CropImageDemo")
        renderDemoListItem("DoodleDemo")
        renderDemoListItem("BitmapDrawSizeDemo")
        //renderDemoListItem("CanvasDemo")
        renderDemoListItem("Canvas2Demo $GO")
        renderDemoListItem("ExoPlayerDemo") {
            SingleExoPlayerActivity.play(
                "https://laserpecker-prod.oss-cn-hongkong.aliyuncs.com/test/%E9%A3%9E%E4%B9%A620230512-100632.mp4", //"http://8.218.75.39:8888/down/1tRJELfNObh9.mp4", //"http://8.218.75.39:8888/down/9vG6hZhEz5NO.MP4",
                //"https://laserpecker-prod.oss-cn-hongkong.aliyuncs.com/pecker/doc/0508-1.ass",//"https://laserpecker-prod.oss-cn-hongkong.aliyuncs.com/pecker/doc/L3_subtitle.ass",
                "https://laserpecker-prod.oss-cn-hongkong.aliyuncs.com/test/095939qyym3q3zn5w5wyzv.vtt",
                "测试字幕"
            )
        }
        renderDemoListItem("MagnifierDemo")
        renderDemoListItem("WebViewDemo")
        renderDemoListItem("CameraXPreviewDemo")
        renderDemoListItem("SingleMatrixViewDemo")
        renderDemoListItem("CurveTextDemo")

        //https://github.com/sinawangnan7/CurrentActivity
        //renderDemoListItem("CurrentActivityDemo $GO")

        //position
        //lockDemoPosition = 83 - 1 //canvas

        //设备信息.
        DslLastDeviceInfoItem()()

        //jump
        onDispatchUpdatesOnce {
            if (!BaseCoreAppCompatActivity.haveLastCrash && savedInstanceState == null) {
                //自动跳转至指定Demo
                if (autoJump && JUMP_TO_LOCK_POSITION) {
                    if (isDebuggerConnected()) {
                        //debug
                    } else {
                        _jumpToLockPosition()
                    }
                }
            }
        }
    }
}