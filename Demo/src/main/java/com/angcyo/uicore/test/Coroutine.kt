package com.angcyo.uicore.test

import com.angcyo.coroutine.*
import com.angcyo.library.L
import kotlinx.coroutines.Dispatchers

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/06
 */

fun coroutineTest() {
    //backTest()
    //blockTest()
}

/**串行协程线程调度测试*/
fun blockTest() {
    launchGlobal {

        val i = withBlock {
            L.i("run....1..1")
            sleep()
            1 / 0
            L.i("run....1..1end")
            1
        }

        val i1 = withBlock {
            L.i("run....1..2")
            sleep(400)
            L.i("run....1..2end")
            2
        }

        val i2 = withBlock {
            L.i("run....1..3")
            sleep(500)
            L.i("run....1..3end")
            3
        }

        L.i("all end1->$i $i1 $i2")

        withMain {
            L.i("all end2->$i $i1 $i2")
        }
    }
}

/**并发协程测试*/
fun backTest() {
    launchGlobal(Dispatchers.Main + CoroutineErrorHandler { context, exception ->
        L.e("自定义捕捉异常:$exception")
    }) {

        val i = try {
            onBack {
                L.i("run....1..1")
                sleep()
                //1 / 0
                L.i("run....1..1end")
                1
            }
        } catch (e: Exception) {
            L.e("1...${e.message}")
            null
        }

        val i1 = onBack {
            L.i("run....1..2")
            sleep(400)
            L.i("run....1..2end")
            2
        }

        val i2 = onBack {
            L.i("run....1..3")
            sleep(500)
            L.i("run....1..3end")
            3
        }

        val j = try {
            i?.await()
            //i?.cancel()
        } catch (e: Exception) {
            L.e("2...${e.message}")
        }
        val j1 = i1.await()
        val j2 = i2.await()

        L.i("all end1->${j} ${i1.await()} ${i2.await()}")

        withMain {
            L.i("all end2->$j $j1 $j2")
            //L.i("all end3->${i?.await()} ${i1.await()} ${i2.await()}")
        }
    }
}