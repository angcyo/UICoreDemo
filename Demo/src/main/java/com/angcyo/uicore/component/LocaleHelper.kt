package com.angcyo.uicore.component

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.LocaleList
import android.text.TextUtils
import com.angcyo.library.L
import java.util.*

/**
 * https://github.com/yinhaide/HDRocket/
 * 多国语言适配
 * 使用方法
 * （1）Application 重写attachBaseContext()，传入 super.attachBaseContext(LocaleHelper.getContext(context));;
 * （2）Application 重写onConfigurationChanged()，传入 LocaleHelper.init(context);
 * （3）Application 重写onCreate()，传入 LocaleHelper.init(context);
 * （4）Activity 重写attachBaseContext()，传入 super.attachBaseContext(LocaleHelper.getContext(context));
 * （5）Service 重写attachBaseContext()，传入 super.attachBaseContext(LocaleHelper.getContext(context));
 * （6）LocaleHelper.setLocale(Context context, Locale locale);//在需要切换语言的地方保存语言
 * （7）MainActivity.reStart(this);//重启APP到主页面,可选
 * Created by haide.yin(haide.yin@tcl.com) on 2019/3/26 14:32.
 */
object LocaleHelper {
    private const val LANGUAGE_KEY = "LANGUAGE"
    private const val COUNTRY_KEY = "COUNTRY"
    private const val ISFROMEAPP_KEY = "IS_FROM_APP"
    /**
     * 初始化，设置上次选择的语言
     *
     * @param context 上下文
     */
    fun init(context: Context) {
        val locale = getLocale(context)
        val lang = locale.language
        val cn = locale.country
        //SharePreUtil.getInstance().putString(context, LANGUAGE_KEY, lang)
        //SharePreUtil.getInstance().putString(context, COUNTRY_KEY, cn)
        updateConfiguration(context, lang, cn)
        if (context is Activity) {
            updateConfiguration(context.getApplicationContext(), lang, cn)
        }
    }

    /**
     * 更新上下文语言配置
     *
     * @param context 上下文
     * @return 新的上下文
     */
    fun attachBaseContext(context: Context): Context {
        return if (Build.VERSION.SDK_INT >= 24) {
            val locale = getLocale(context)
            val resources = context.resources
            val configuration = resources.configuration
            configuration.setLocale(locale)
            configuration.setLocales(LocaleList(locale))
            context.createConfigurationContext(configuration)
        } else {
            context
        }
    }

    /**
     * 切换指定的语言
     *
     * @param context 上下文
     * @param locale  语言Locale
     */
    fun setLocale(context: Context, locale: Locale) {
        L.v("LocaleHelper::setLocale-->locale:$locale")
        val cn = locale.country
        val lang = locale.language
        //SharePreUtil.getInstance().putString(context, LANGUAGE_KEY, lang)
        //SharePreUtil.getInstance()
        //    .putString(context, COUNTRY_KEY, cn ?: "")
        //SharePreUtil.getInstance().putBoolean(context, ISFROMEAPP_KEY, true)
        updateConfiguration(context, lang, cn)
        if (context is Activity) {
            updateConfiguration(context.getApplicationContext(), lang, cn)
        }
    }

    /**
     * 得到本地语言，APP存储的优先，其次是系统选择的语言
     *
     * @param context 上下文
     * @return 当前语言
     */
    fun getLocale(context: Context?): Locale {
        var locale = if (Build.VERSION.SDK_INT >= 24) {
            LocaleList.getDefault()[0]
        } else {
            Locale.getDefault()
        }
        L.v("LocaleHelper::getLocale-->locale:$locale")
        val isAppLang: Boolean = false
        //SharePreUtil.getInstance().getBoolean(context, ISFROMEAPP_KEY, false)
        return if (isAppLang) {
            val lang: String = locale.language
            //SharePreUtil.getInstance().getString(context, LANGUAGE_KEY, locale.language)
            val cn: String = locale.country
            //SharePreUtil.getInstance().getString(context, COUNTRY_KEY, locale.country)
            locale = if (TextUtils.isEmpty(cn)) {
                Locale(lang)
            } else {
                Locale(lang, cn.toUpperCase())
            }
            locale
        } else {
            locale
        }
    }

    /**
     * 设置新的语言，更新配置信息
     *
     * @param context 上下文
     * @param lang    语言Locale.getlanguage()
     * @param cn      国家Locale.getCountry()
     */
    private fun updateConfiguration(context: Context, lang: String, cn: String?) {
        val locale: Locale = if (TextUtils.isEmpty(cn)) {
            Locale(lang)
        } else {
            Locale(lang, cn!!.toUpperCase())
        }
        val resources = context.resources
        val configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale)
        } else {
            configuration.locale = locale
        }
        val dm = resources.displayMetrics
        resources.updateConfiguration(configuration, dm)
    }
}