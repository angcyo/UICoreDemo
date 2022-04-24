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
import com.angcyo.core.component.fileSelector
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.github.window.dslFloatWindow
import com.angcyo.http.ApiKt
import com.angcyo.http.dslHttp
import com.angcyo.item.DslLastDeviceInfoItem
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.style.itemInfoText
import com.angcyo.library.L
import com.angcyo.library.ex.*
import com.angcyo.library.toastQQ
import com.angcyo.library.utils.FileUtils
import com.angcyo.library.utils.RUtils
import com.angcyo.library.utils.checkApkExist
import com.angcyo.uicore.activity.*
import com.angcyo.uicore.base.BaseDemoDslFragment
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.dslitem.AppMenuFooterItem
import com.angcyo.uicore.dslitem.AppMenuHeaderItem
import com.angcyo.uicore.test.coroutineTest
import com.angcyo.uicore.test.rxJavaTest
import com.angcyo.uicore.test.test
import com.angcyo.uicore.test.testHttp
import com.angcyo.widget.base.behavior
import com.angcyo.widget.base.onDoubleTap
import com.angcyo.widget.layout.SliderMenuLayout
import com.angcyo.widget.recycler.allViewHolder
import com.angcyo.widget.recycler.get
import com.angcyo.widget.recycler.initDslAdapter
import com.yhao.floatwindow.IFloatWindowImpl
import org.jsoup.internal.StringUtil.padding

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
    }

    init {
        fragmentLayoutId = R.layout.fragment_main
        goFirst = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CREATE_COUNT++
    }

    override fun onBackPressed(): Boolean {
        return _vh.v<SliderMenuLayout>(R.id.menu_layout)?.requestBackPressed() == true
    }

    lateinit var menuAdapter: DslAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        leftControl()?.appendItem(ico = R.drawable.app_ic_menu, action = {
            gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            padding(0)
        }) {
            _vh.v<SliderMenuLayout>(R.id.menu_layout)?.openMenu()
        }
        _vh.rv(R.id.menu_recycler_view)?.apply {
            behavior()?.also {
                if (it is RefreshContentBehavior) {
                    it.refreshBehaviorConfig = ScaleHeaderRefreshEffectConfig().apply {
                        scaleThreshold = Float.MAX_VALUE
                    }
                }
            }
            initDslAdapter {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        titleControl()?.selectorView?.onDoubleTap {
            _jumpToLockPosition()
            true
        }
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
                throw  e
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
        if (lockDemoPosition >= 0) {
            _vh.postDelay(300) {
                _adapter[lockDemoPosition]?.itemClick?.invoke(_vh.itemView)
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
            renderDemoListItem("FragmentInFragmentDemo", 10.toDpi()) {
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

            renderDemoListItem("DrawableSpanDemo")
            renderDemoListItem("WidgetDemo ArcLoadingView")
            renderDemoListItem("LoadingDemo $GO")
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
            renderDemoListItem("CameraXDemo")
            renderDemoListItem("StyleDemo ThemeStyledAttributes")
            renderDemoListItem("ShortcutDemo")
            renderDemoListItem("DslDrawItemDecorationDemo")
            renderDemoListItem("QrCodeDemo")
            renderDemoListItem("TbsWebDemo $GO")
            renderDemoListItem("IntentDemo")
            renderDemoListItem("LauncherDemo")
            renderDemoListItem("GameRenderEngineDemo")
            renderDemoListItem("JsoupDemo")
            renderDemoListItem("PagerLayoutManagerDemo")
            renderDemoListItem("LinkageSvBehaviorDemo Sv+Sv")
            renderDemoListItem("LinkageSingleBehaviorDemo Sv+Rv")
            renderDemoListItem("LinkageRvBehaviorDemo Rv+Rv")
            renderDemoListItem("LinkageVpBehaviorDemo Rv+Vp $GO")
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
            renderDemoListItem("TakeMediaDemo $GO")
            renderDemoListItem("SensorDemo")
            renderDemoListItem("FocusDemo")
            renderDemoListItem("RabbitMQDemo")
            renderDemoListItem("WaveViewDemo")
            renderDemoListItem("MoveBehaviorDemo")
            renderDemoListItem("AMapDemo $GO")
            renderDemoListItem("LockDemo Speech")
            renderDemoListItem("AccessibilityDemo")
            renderDemoListItem("GithubDemo")
            renderDemoListItem("LayerDemo ILayer Step")
            renderDemoListItem("TbsJsBridgeDemo")
            renderDemoListItem("SkeletonViewDemo")
            renderDemoListItem("OkHttpProgressDemo $GO")
            renderDemoListItem("ExDialogDemo $GO")
            renderDemoListItem("NavigationDemo $GO")
            renderDemoListItem("CalendarDemo $GO")
            renderDemoListItem("BiometricDemo Finger $GO")
            renderDemoListItem("ComponentDemo $GO")
            renderDemoListItem("NfcDemo $GO") {
                dslAHelper {
                    start(NfcHandleActivity::class.java)
                }
            }
            renderDemoListItem("BinderDemo IPC $GO")
            renderDemoListItem("StationDemo $GO")
            renderDemoListItem("NinePatchDemo $GO")
            renderDemoListItem("PathDemo $GO")
            renderDemoListItem("MultiLanguageDemo $GO")
            renderDemoListItem("BluetoothDemo $GO")
            renderDemoListItem("FscBleApiDemo $GO")
            renderDemoListItem("SvgDemo $GO GCode")
            renderDemoListItem("MatrixDemo $GO")
            renderDemoListItem("DrawPathDemo $GO")
            renderDemoListItem("CanvasDemo $GO")

            //设备信息.
            DslLastDeviceInfoItem()() {
                itemClick = {
                    dslFHelper {
                        fileSelector({
                            showFileMd5 = true
                            showFileMenu = true
                            showHideFile = true
                            targetPath =
                                FileUtils.appRootExternalFolder()?.absolutePath ?: storageDirectory
                        })
                    }
                }
            }

            //jump
            onDispatchUpdatesOnce {
                if (!BaseCoreAppCompatActivity.haveLastCrash && savedInstanceState == null) {
                    //自动跳转至指定Demo
                    _jumpToLockPosition()
                }
            }
        }
    }
}