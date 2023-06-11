package com.angcyo.uicore.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.angcyo.library.L
import com.angcyo.library.component.DslNotify
import com.angcyo.library.ex.elseBlank
import com.angcyo.library.ex.size
import com.angcyo.library.ex.urlIntent
import com.angcyo.uicore.demo.R
import com.angcyo.widget.span.span

/**
 * 每日一个成语, 桌面小部件/微件
 *
 *
 * [https://developer.android.com/guide/topics/appwidgets?hl=zh-cn](https://developer.android.com/guide/topics/appwidgets?hl=zh-cn)
 *
 * [http://chengyu.t086.com/](http://chengyu.t086.com/)
 *
 * [http://chengyu.t086.com/cy/paihang.html](http://chengyu.t086.com/cy/paihang.html)
 *
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2023/06/11
 */
class IdiomAppWidgetProvider : AppWidgetProvider() {

    /**触发顺序: 1*/
    override fun onEnabled(context: Context) {
        L.d("启用小部件")
    }

    /**触发顺序: 2*/
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        L.d("更新小部件:", appWidgetIds)
        IdiomHelper.init()

        updateLayout(context, appWidgetManager, appWidgetIds) {
            setOnClickPendingIntent(
                R.id.lib_image_view,
                DslNotify.pendingBroadcast(
                    context,
                    Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).apply {
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds)
                    })
            )
        }

        IdiomHelper.fetchIdiomInfo {
            updateLayout(context, appWidgetManager, appWidgetIds) {
                setTextViewText(R.id.lib_title_view, it?.name)
                setTextViewText(R.id.lib_tip_view, it?.pronounce)
                setTextViewText(R.id.lib_body_view, it?.des)
                setTextViewText(
                    R.id.lib_des_view,
                    it?.sample.elseBlank(it?.provenance).elseBlank(it?.synonym)
                )
                setTextViewText(R.id.statistics_view, span {
                    append("今日学习:${IdiomHelper.todayIdiomList.size()}")
                    append(" 总学习:${IdiomHelper.allIdiomList.size()}")
                })

                //click
                it?.url?.let { url ->
                    setOnClickPendingIntent(
                        R.id.lib_root_layout,
                        DslNotify.pendingActivity(context, url.urlIntent())
                    )
                }
            }
        }
    }

    /**触发顺序: 3*/
    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        L.d("删除小部件:", appWidgetIds)
    }

    /**触发顺序: 4*/
    override fun onDisabled(context: Context) {
        L.d("禁用小部件")
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        L.d("小部件配置改变:", appWidgetId, newOptions)
    }

    override fun onRestored(context: Context, oldWidgetIds: IntArray, newWidgetIds: IntArray) {
        L.d("恢复小部件:", oldWidgetIds, newWidgetIds)
    }

    //region ---更新控件内容---

    /**请求更新小部件*/
    fun requestUpdate(context: Context) {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val appWidgetIds = appWidgetManager.getAppWidgetIds(
            ComponentName(context, IdiomAppWidgetProvider::class.java)
        )
        onUpdate(context, appWidgetManager, appWidgetIds)
    }

    /**更新控件*/
    fun updateLayout(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
        action: RemoteViews.() -> Unit
    ) {
        val remoteView = RemoteViews(
            context.packageName,
            R.layout.appwidget_idiom
        )
        remoteView.action()
        appWidgetManager.updateAppWidget(appWidgetIds, remoteView)
    }

    //endregion ---更新控件内容---

}