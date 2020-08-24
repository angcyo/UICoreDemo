package com.angcyo.uicore.test

import com.angcyo.http.post
import com.angcyo.http.rx.BaseFlowableSubscriber
import com.angcyo.http.rx.BaseObserver
import com.angcyo.http.rx.flowableToMain
import com.angcyo.http.rx.observer
import com.angcyo.uicore.App
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/06
 */

fun rxJavaTest() {
    //testPost()
    //testFlowable()
}

fun testPost() {
    post {
        header = App.headerMap
        url = "https://www.mxnzp.com/api/tools/system/time"
//        url = "https://blog.csdn.net/sinat_31057219/article/details/101304867/"
//        url = "https://www.baidu.com"
    }.observer(BaseObserver())
}

fun testFlowable() {
    Flowable.interval(1, 1, TimeUnit.SECONDS)
        .compose(flowableToMain())
        .observer(BaseFlowableSubscriber())
}