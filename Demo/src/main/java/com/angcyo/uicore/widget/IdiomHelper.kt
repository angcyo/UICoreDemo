package com.angcyo.uicore.widget

import com.angcyo.http.base.fromJson
import com.angcyo.http.base.listType
import com.angcyo.http.base.toJson
import com.angcyo.jsoup.dslJsoup
import com.angcyo.library.ex.readText
import com.angcyo.library.ex.writeText
import com.angcyo.library.libCacheFile
import org.jsoup.nodes.Element

/**
 * 获取成语数据
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2023/06/11
 */
object IdiomHelper {

    private const val BASE = "http://chengyu.t086.com"
    val allIdiomList = mutableListOf<IdiomInfoBean>()
    val todayIdiomList = mutableListOf<IdiomInfoBean>()
    val cacheFile = libCacheFile("idiom.json")

    /**初始化*/
    fun init() {
        allIdiomList.clear()
        cacheFile.readText()?.fromJson<List<IdiomInfoBean>>(listType(IdiomInfoBean::class))?.let {
            allIdiomList.addAll(it)
        }
    }

    /**保存数据*/
    fun save(info: IdiomInfoBean) {
        allIdiomList.add(info)
        cacheFile.writeText(allIdiomList.toJson() ?: "", false)
    }

    /**获取一个推荐的成语*/
    fun fetchIdiomInfo(action: (IdiomInfoBean?) -> Unit) {
        //action(IdiomInfo())
        if (todayIdiomList.isEmpty()) {
            allIdiomList.lastOrNull()?.let(action)
        }

        dslJsoup("${BASE}/cy/paihang.html") {
            //获取成语列表
            val elementList = select("div[class=listw] a")

            //随机获取一个
            var targetElement: Element? = null
            while (true) {
                targetElement = elementList.random()
                val name = targetElement.text()

                if (allIdiomList.find { it.name == name } != null) {
                    //已经存在, 继续获取
                    continue
                } else {
                    break
                }
            }

            //获取成语描述
            if (targetElement == null) {
                action(null)
            } else {
                val name = targetElement.text()
                val url = BASE + targetElement.attr("href")

                dslJsoup(url) {
                    val tbodyElement = select("div[class=mainbar] tbody")
                    action(IdiomInfoBean(
                        name,
                        tbodyElement.select("tr:nth-child(2) td:nth-child(2)").text(),
                        tbodyElement.select("tr:nth-child(3) td:nth-child(2)").text(),
                        tbodyElement.select("tr:nth-child(4) td:nth-child(2)").text(),
                        tbodyElement.select("tr:nth-child(5) td:nth-child(2)").text(),
                        tbodyElement.select("tr:nth-child(6) td:nth-child(2)").text(),
                        url
                    ).apply {
                        todayIdiomList.add(this)
                        save(this)
                    })
                }
            }
        }
    }
}

data class IdiomInfoBean(
    /**成语*/
    var name: String = "",
    /**发音*/
    var pronounce: String = "",
    /**描述, 释义*/
    var des: String = "",
    /**出处*/
    var provenance: String = "",
    /**示例*/
    var sample: String = "",
    /**近义词*/
    var synonym: String = "",
    /**页面详情*/
    var url: String = "",

    /**时间*/
    val time: Long = System.currentTimeMillis()
)