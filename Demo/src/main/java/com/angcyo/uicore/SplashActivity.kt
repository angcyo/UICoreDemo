package com.angcyo.uicore

import android.os.Bundle
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.base.dslAHelper
import com.angcyo.core.component.ComplianceCheck
import com.angcyo.noAnim

/**
 * 启动页, 进行合规检查
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/19
 */
class SplashActivity : BaseAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ComplianceCheck.check {
            //同意合规, 在此弹出合规对话框
            ComplianceCheck.agree()//用户同意之后, 调用此方法
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        dslAHelper {
            finishSelf = true
            start(MainActivity::class.java) {
                noAnim()
            }
        }
    }
}