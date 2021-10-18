package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.amqp.DslRabbitMQ
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.DslTextItem
import com.angcyo.item.style.itemInfoText
import com.angcyo.item.style.itemText
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.string
import com.angcyo.widget.recycler.ScrollHelper.Companion.SCROLL_TYPE_BOTTOM

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/05/15
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class RabbitMQDemo : AppDslFragment() {

    val dslRabbitMQ: DslRabbitMQ by lazy {
        DslRabbitMQ().apply {
            exchange = "topicExchange"

            action {
                errorAction = { reason, error ->
                    _adapter + DslTextInfoItem().apply {
                        itemInfoText = reason
                        itemDarkText = nowTimeString()
                    }
                }
                messageAction = { msg, consumerTag, envelope, properties ->
                    _adapter + DslTextInfoItem().apply {
                        itemInfoText = "收到消息:$msg"
                        itemDarkText = nowTimeString()
                    }
                    _adapter.onDispatchUpdatesOnce {
                        _recycler.scrollHelper.lockPositionByDraw {
                            scrollType = SCROLL_TYPE_BOTTOM
                        }
                    }
                }
            }
        }
    }

    init {
        fragmentLayoutId = R.layout.fragment_rabbitmq
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            DslTextItem()() {
                itemText = nowTimeString()
            }
        }

        _vh.click(R.id.connect_button) {
            dslRabbitMQ.connect(_vh.ev(R.id.url_edit).string())
        }

        _vh.click(R.id.connect_queue_button) {
            dslRabbitMQ._subscribe(
                _vh.ev(R.id.queue_edit).string(),
                _vh.ev(R.id.route_edit).string()
            )
        }

        _vh.click(R.id.send_button) {
            val key = _vh.ev(R.id.route_edit).string()
            val msg = _vh.ev(R.id.edit_text).string()
            dslRabbitMQ._publish(key, msg)
            _adapter + DslTextInfoItem().apply {
                itemInfoText = "发送消息:$msg"
                itemDarkText = key
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dslRabbitMQ.disconnect()
    }
}