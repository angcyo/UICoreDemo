package com.angcyo.uicore.demo.ipc

import android.app.ActivityManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import com.angcyo.core.activity.BaseCoreAppCompatActivity
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.simpleClassName
import com.angcyo.library.ex.toBitmap
import com.angcyo.uicore.demo.R

/**
 * https://github.com/ysy950803/SmallApp
 *
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/31
 */
open class SmallActivity : BaseCoreAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //https://github.com/ysy950803/SmallApp
        // 动态地给小程序Activity设置名称和图标，下面代码只是举例，实际信息肯定是动态获取的
        // 由于iconRes这个构造参数的API 28才加入的，所以建议区分版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val iconRes = R.drawable.face // 这里应该是小程序图标的资源索引
            setTaskDescription(ActivityManager.TaskDescription(simpleClassName(), iconRes))
        } else {
            val iconBmp: Bitmap? = _drawable(R.drawable.face, this)?.toBitmap() // 这里应该是小程序图标的bitmap
            setTaskDescription(ActivityManager.TaskDescription(simpleClassName(), iconBmp))
        }
    }

    /*override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)
        dslFHelper {
            restore(BinderDemo::class)
        }
    }*/
}