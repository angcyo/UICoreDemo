package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.R
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppOkDownloadItem
import com.angcyo.widget.recycler.noItemAnim
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/26
 */
class OkDownloadDemo : AppDslFragment() {

    companion object {
        val urlList =
            listOf(
                "http://package.mac.wpscdn.cn/mac_wps_pkg/1.9.1/WPS_Office_1.9.1(2994).dmg",
                "http://download.wayto.com.cn/wayto_metro_hk_20181228_1.1.7_58.apk",
                "http://quality-cdn.shouji.sogou.com/wapdl/android/apk/SogouInput_android_v10.3_sweb.apk"
            )

        fun downloadUrl(): String {
            return urlList[nextInt(urlList.size)]
        }
    }

    override fun onInitDslLayout() {
        super.onInitDslLayout()
        _vh.rv(R.id.lib_recycler_view)?.noItemAnim()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderDslAdapter {
            AppOkDownloadItem()() {
                downloadUrl = urlList.first()
            }
            AppOkDownloadItem()() {
                downloadUrl = urlList.first()
            }
            AppOkDownloadItem()() {
                downloadUrl = urlList.getOrNull(1)
            }

            for (i in 0..100) {
                AppOkDownloadItem()() {
                    downloadUrl = downloadUrl()
                }
            }

//            for (url in urlList) {
//                AppOkDownloadItem()() {
//                    downloadUrl = url
//                }
//            }
        }
    }
}