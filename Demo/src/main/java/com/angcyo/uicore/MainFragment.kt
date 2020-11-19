package com.angcyo.uicore

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.behavior.HideTitleBarBehavior
import com.angcyo.behavior.refresh.RefreshContentBehavior
import com.angcyo.behavior.refresh.ScaleHeaderRefreshEffectConfig
import com.angcyo.core.activity.BaseCoreAppCompatActivity
import com.angcyo.core.component.fileSelector
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.github.window.dslFloatWindow
import com.angcyo.http.ApiKt
import com.angcyo.http.dslHttp
import com.angcyo.item.DslLastDeviceInfoItem
import com.angcyo.item.DslTextInfoItem
import com.angcyo.library.L
import com.angcyo.library.ex.*
import com.angcyo.library.toast
import com.angcyo.library.toastQQ
import com.angcyo.library.utils.FileUtils
import com.angcyo.library.utils.RUtils
import com.angcyo.library.utils.checkApkExist
import com.angcyo.uicore.activity.*
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.dslitem.AppMenuFooterItem
import com.angcyo.uicore.dslitem.AppMenuHeaderItem
import com.angcyo.uicore.test.coroutineTest
import com.angcyo.uicore.test.rxJavaTest
import com.angcyo.uicore.test.test
import com.angcyo.uicore.test.testHttp
import com.angcyo.widget.base.behavior
import com.angcyo.widget.base.onDoubleTap
import com.angcyo.widget.base.padding
import com.angcyo.widget.base.reveal
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

class MainFragment : AppDslFragment() {

    companion object {
        var CREATE_COUNT = 0
        const val GO = "√"
    }

    init {
        fragmentLayoutId = R.layout.fragment_main
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
            initDslAdapter() {
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

    /**锁定Demo的位置, 每次启动时自动跳转到这个Demo*/
    var lockDemoPosition: Int = RecyclerView.NO_POSITION

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
                val response = dslHttp(ApiKt::class.java).post(url, headerMap = App.headerMap)
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

        if (!BaseCoreAppCompatActivity.haveLastCrash) {
            //自动跳转至指定Demo
            _jumpToLockPosition()
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

    fun DslAdapter.renderMainItem(
        text: CharSequence?,
        topInsert: Int = 1 * dpi,
        fragment: Class<out Fragment>? = null,
        init: DslAdapterItem.() -> Unit = {},
        click: ((View) -> Unit)? = null
    ) {
        this + DslTextInfoItem().apply {
            itemInfoText = "${this@renderMainItem.adapterItems.size + 1}.$text"
            itemTopInsert = topInsert
            itemInfoIcon = R.drawable.ic_logo_small
            itemDarkIcon = R.drawable.lib_next
            itemDarkIconColor = getColor(R.color.colorPrimaryDark)

            itemClick = { view ->

                var cls: Class<out Fragment>? = fragment
                val className = "com.angcyo.uicore.demo.${text?.split(" ")?.get(0)}"
                try {
                    if (fragment == null) {
                        cls = Class.forName(className) as? Class<out Fragment>
                    }
                } catch (e: Exception) {
                    //Tip.tip("未找到类:\n$className")
                    if (click == null) {
                        toast("未找到类:\n$className")
                    }
                }

                cls?.let {

//                    dslAHelper {
//                        start(it)
//                    }

                    dslFHelper {
                        show(it)
                    }
                }

                click?.invoke(view)
            }

            this.init()
        }

        if (text?.contains('√', true) == true) {
            if (goFirst && lockDemoPosition >= 0) {
                return
            }
            lockDemoPosition = this.adapterItems.lastIndex
        }
    }

    /**调转首次遇到的[GO], 否则则是最后一次*/
    var goFirst = false

    override fun onInitFragment() {
        super.onInitFragment()
        renderDslAdapter {
            renderMainItem("FragmentInFragmentDemo", 10.toDpi()) {
                dslAHelper {
                    start(FragmentInFragmentActivity::class.java)
                }
            }
            renderMainItem("ViewPagerInFragmentDemo") {
                dslAHelper {
                    start(ViewPagerInFragmentActivity::class.java)
                }
            }
            renderMainItem("ViewPagerInViewPagerDemo") {
                dslAHelper {
                    start(ViewPagerInViewPagerActivity::class.java)
                }
            }
            renderMainItem("ViewPager2InFragmentDemo") {
                dslAHelper {
                    start(ViewPager2InFragmentActivity::class.java)
                }
            }
            renderMainItem("ViewPager2InViewPager2Demo") {
                dslAHelper {
                    start(ViewPager2InViewPager2Activity::class.java)
                }
            }

            renderMainItem("DrawableSpanDemo")
            renderMainItem("WidgetDemo ArcLoadingView")
            renderMainItem("RefreshEffectDemo DslToast")
            renderMainItem("RefreshDemo SwipeMenu")
            renderMainItem("DslAffectDemo")
            renderMainItem("ValueTextWatcherDemo DslSoftInputLayout")
            renderMainItem("DslSoftInputDemo")
            renderMainItem("ViewModelDemo")
            renderMainItem("ViewGroupOverlayDemo")
            renderMainItem("TransitionDemo")
            renderMainItem("GlideImageDemo")
            renderMainItem("RegularPatternDemo")
            renderMainItem("OkDownloadDemo")
            renderMainItem("MediaPickerDemo")
            renderMainItem("DialogDemo")
            renderMainItem("NotifyDemo ContentObserver")
            renderMainItem("CameraXDemo")
            renderMainItem("StyleDemo ThemeStyledAttributes")
            renderMainItem("ShortcutDemo")
            renderMainItem("DslDrawItemDecorationDemo")
            renderMainItem("QrCodeDemo")
            renderMainItem("TbsWebDemo $GO")
            renderMainItem("IntentDemo")
            renderMainItem("LauncherDemo")
            renderMainItem("GameRenderEngineDemo")
            renderMainItem("JsoupDemo")
            renderMainItem("PagerLayoutManagerDemo")
            renderMainItem("LinkageSvBehaviorDemo Sv+Sv")
            renderMainItem("LinkageSingleBehaviorDemo Sv+Rv")
            renderMainItem("LinkageRvBehaviorDemo Rv+Rv")
            renderMainItem("LinkageVpBehaviorDemo Rv+Vp $GO")
            renderMainItem("TitleBarBehaviorDemo")
            renderMainItem("TitleBarDrawableBehaviorDemo")
            renderMainItem("WaveLayerDemo")
            renderMainItem("LineChartDemo")
            renderMainItem("BarChartDemo")
            renderMainItem("PieChartDemo")
            renderMainItem("OtherChartDemo")
            renderMainItem("FloatWindowDemo") {
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
            renderMainItem("ObjectBoxDemo")
            renderMainItem("AudioRecordDemo Player")
            renderMainItem("TakeMediaDemo")
            renderMainItem("SensorDemo")
            renderMainItem("FocusDemo $GO")
            renderMainItem("RabbitMQDemo $GO")
            renderMainItem("WaveViewDemo $GO")
            renderMainItem("MoveBehaviorDemo $GO")
            renderMainItem("AMapDemo $GO")
            renderMainItem("LockDemo Speech $GO")
            renderMainItem("AccessibilityDemo $GO")
            renderMainItem("GithubDemo")
            renderMainItem("LayerDemo ILayer Step $GO")
            renderMainItem("TbsJsBridgeDemo")
            renderMainItem("SkeletonViewDemo")

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
        }
    }
}