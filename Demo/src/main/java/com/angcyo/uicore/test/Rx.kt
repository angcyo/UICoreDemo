package com.angcyo.uicore.test

import com.angcyo.http.post
import com.angcyo.http.rx.BaseObserver
import com.angcyo.http.rx.observer

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/06
 */

fun rxJavaTest() {
    post {
        url = "https://www.mxnzp.com/api/tools/system/time"
//        url = "https://blog.csdn.net/sinat_31057219/article/details/101304867/"
//        url = "https://www.baidu.com"
    }.observer(BaseObserver())
}