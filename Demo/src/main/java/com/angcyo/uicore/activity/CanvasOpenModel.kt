package com.angcyo.uicore.activity

import androidx.lifecycle.ViewModel
import com.angcyo.viewmodel.vmDataOnce

/**
 * 用来实现[Canvas]的打开文件和导入文件数据转发
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/07/15
 */
class CanvasOpenModel : ViewModel() {

    /**需要打开的数据*/
    val openPendingData = vmDataOnce<CanvasOpenInfo>()

}

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