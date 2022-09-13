package com.angcyo.uicore.activity

import android.content.Context
import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import com.angcyo.base.dslAHelper
import com.angcyo.library.ex.openApp
import com.angcyo.uicore.MainActivity
import com.angcyo.uicore.demo.CanvasDemo
import com.angcyo.viewmodel.vmDataOnce

/**
 * 用来实现[Canvas]的打开文件和导入文件数据转发
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/07/15
 */
class CanvasOpenModel : ViewModel() {

    /**需要打开的数据*/
    val openPendingData = vmDataOnce<CanvasOpenInfo>()

    /**使用创作打开一个图片/GCode/SVG*/
    fun open(context: Context, path: String, type: String = CanvasOpenActivity.JPG) {
        if (openPendingData.hasObservers()) {
            //有监听者
            context.openApp()
        } else {
            //无监听者
            context.dslAHelper {
                startFragment(MainActivity::class.java, CanvasDemo::class.java)
            }
        }
        openPendingData.postValue(CanvasOpenInfo(type, path))
    }
}

@Keep
data class CanvasOpenInfo(
    /**需要打开的数据类型
     * [CanvasOpenActivity.JPG]
     * [CanvasOpenActivity.GCODE]
     * [CanvasOpenActivity.SVG]
     */
    val type: String,
    /**数据路径, 目前支持本地路径*/
    val url: String
)