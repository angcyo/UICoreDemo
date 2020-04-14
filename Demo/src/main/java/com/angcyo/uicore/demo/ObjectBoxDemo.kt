package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.coroutine.onBack
import com.angcyo.dsladapter.dslItem
import com.angcyo.library.LTime
import com.angcyo.library.ex.nowTimeString
import com.angcyo.objectbox.boxOf
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.entity.BoxEntity
import io.objectbox.Box

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/14
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class ObjectBoxDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            dslItem(R.layout.demo_object_box) {

                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->

                    fun doIt(back: Box<BoxEntity>.() -> Long, main: (Long) -> Unit) {
                        itemHolder.tv(R.id.lib_text_view)?.text = "..."
                        launchLifecycle {
                            main(onBack {
                                LTime.tick()
                                boxOf(BoxEntity::class.java).run(back)
                            }.await())
                        }
                    }

                    itemHolder.click(R.id.query_button) {
                        doIt({
                            count()
                        }) {
                            itemHolder.tv(R.id.lib_text_view)?.text =
                                "数据:${it}条,耗时:${LTime.time()}"
                        }
                    }

                    itemHolder.click(R.id.insert_button) {
                        doIt({
                            val entityList = mutableListOf<BoxEntity>()
                            for (i in 1..10_000) {
                                entityList.add(BoxEntity())
                            }
                            put(entityList)
                            0
                        }) {
                            itemHolder.tv(R.id.lib_text_view)?.text = "插入1W条数据,耗时:${LTime.time()}"
                        }
                    }

                    itemHolder.click(R.id.update_button) {
                        doIt({
                            query().build().find().run {
                                forEach {
                                    it.entityAddTime = nowTimeString()
                                }
                                put(this)
                                size.toLong()
                            }
                        }) {
                            itemHolder.tv(R.id.lib_text_view)?.text =
                                "更新${it}条数据,耗时:${LTime.time()}"
                        }
                    }

                    itemHolder.click(R.id.delete_button) {
                        doIt({
                            removeAll()
                            0
                        }) {
                            itemHolder.tv(R.id.lib_text_view)?.text =
                                "删除所有数据,耗时:${LTime.time()}"
                        }
                    }
                }
            }
        }
    }
}