package com.angcyo.uicore.dslitem

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.component.DslNotify
import com.angcyo.library.component.dslNotify
import com.angcyo.library.ex.nowTime
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.MainActivity
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.string
import com.angcyo.widget.spinner

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/13
 */

class AppNotifyItem : DslAdapterItem() {
    init {
        itemLayoutId = R.layout.app_item_notify
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem, payloads)

        val bitmap =
            BitmapFactory.decodeResource(itemHolder.content.resources, R.drawable.image_manhua_nv)

        fun config(notify: DslNotify) {
            notify.apply {

                notifyOngoing = itemHolder.cb(R.id.ongoing_cb)?.isChecked == true

                if (itemHolder.cb(R.id.large_icon_cb)?.isChecked == true) {
                    notifyLargeIcon = bitmap
                }

                if (itemHolder.cb(R.id.big_large_icon_cb)?.isChecked == true) {
                    styleBigLargeIcon = bitmap
                }

                notifyTitle = "${itemHolder.tv(R.id.notify_title_view).string()} ${nowTimeString()}"
                notifyText =
                    "${itemHolder.tv(R.id.notify_message_view).string()} ${nowTimeString()}"

                notifyInfo = "notifyInfo"
                notifySubText = "notifySubText"

                itemHolder.spinner(R.id.defaults_spinner)?.getSelectedData<String>()?.run {
                    notifyDefaults = split(" ")[1].toInt()
                }

                itemHolder.spinner(R.id.priority_spinner)?.getSelectedData<String>()?.run {
                    notifyPriority = split(" ")[1].toInt()
                }

                itemHolder.spinner(R.id.visibility_spinner)?.getSelectedData<String>()?.run {
                    notifyVisibility = split(" ")[1].toInt()
                }

                if (itemHolder.cb(R.id.click_cb)?.isChecked == true) {
                    notifyContentIntent =
                        DslNotify.pendingActivity(itemHolder.content, MainActivity::class.java)
                }

                if (itemHolder.cb(R.id.full_screen_intent_cb)?.isChecked == true) {
                    notifyFullScreenIntent = DslNotify.pendingActivity(itemHolder.content, Intent())
                }

                if (itemHolder.cb(R.id.action_cb)?.isChecked == true) {
                    notifyActions = listOf(
                        DslNotify.action(
                            "action1",
                            DslNotify.pendingActivity(itemHolder.content, Intent())
                        ),
                        DslNotify.action(
                            "action2",
                            DslNotify.pendingActivity(itemHolder.content, Intent())
                        ),
                        DslNotify.action(
                            "action3",
                            DslNotify.pendingActivity(itemHolder.content, Intent()),
                            android.R.drawable.sym_action_call
                        )
                    )
                }
            }
        }

        itemHolder.spinner(R.id.defaults_spinner)
            ?.setStrings(
                listOf(
                    "DEFAULT_ALL ${NotificationCompat.DEFAULT_ALL}",
                    "DEFAULT_SOUND ${NotificationCompat.DEFAULT_SOUND}",
                    "DEFAULT_VIBRATE ${NotificationCompat.DEFAULT_VIBRATE}",
                    "DEFAULT_LIGHTS ${NotificationCompat.DEFAULT_LIGHTS}"
                )
            )

        itemHolder.spinner(R.id.priority_spinner)
            ?.setStrings(
                listOf(
                    "PRIORITY_DEFAULT ${NotificationCompat.PRIORITY_DEFAULT}",
                    "PRIORITY_LOW ${NotificationCompat.PRIORITY_LOW}",
                    "PRIORITY_MIN ${NotificationCompat.PRIORITY_MIN}",
                    "PRIORITY_HIGH ${NotificationCompat.PRIORITY_HIGH}",
                    "PRIORITY_MAX ${NotificationCompat.PRIORITY_MAX}"
                )
            )

        itemHolder.spinner(R.id.visibility_spinner)
            ?.setStrings(
                listOf(
                    "VISIBILITY_PRIVATE ${NotificationCompat.VISIBILITY_PRIVATE}",
                    "VISIBILITY_PUBLIC ${NotificationCompat.VISIBILITY_PUBLIC}",
                    "VISIBILITY_SECRET ${NotificationCompat.VISIBILITY_SECRET}"
                )
            )

        itemHolder.click(R.id.notify_normal) {
            dslNotify {
                config(this)
            }
        }

        itemHolder.click(R.id.notify_normal_intent) {
            dslNotify {
                config(this)
            }
        }

        itemHolder.click(R.id.notify_big_text) {
            dslNotify {
                config(this)
                styleBigText = "big:$notifyText"
                styleBigContentTitle = "big:$notifyTitle"
                styleBigSummaryText = "styleBigSummaryText"
            }
        }
        itemHolder.click(R.id.notify_big_picture) {
            dslNotify {
                config(this)
                styleBigPicture = bitmap
            }
        }
        itemHolder.click(R.id.notify_inbox) {
            dslNotify {
                config(this)
                styleLineList = listOf(
                    "1:$notifyText",
                    "2:$notifyText",
                    "3:$notifyText",
                    "4:$notifyText",
                    "5:$notifyText",
                    "6:$notifyText",
                    "7:$notifyText",
                    "8:$notifyText",
                    "9:$notifyText",
                    "0:$notifyText"
                )
            }
        }
        itemHolder.click(R.id.notify_message) {
            dslNotify {
                config(this)
                stylePerson = Person.Builder().setName("PName").build()
                styleMessageList =
                    listOf(
                        NotificationCompat.MessagingStyle.Message(
                            "MessageText1",
                            nowTime(),
                            Person.Builder().setName("MName1").setBot(true).build()
                        ),
                        NotificationCompat.MessagingStyle.Message(
                            "MessageText2",
                            nowTime(),
                            Person.Builder().setName("MName2").setImportant(true).build()
                        ),
                        NotificationCompat.MessagingStyle.Message(
                            "MessageText3",
                            nowTime(),
                            Person.Builder().setName("MName3")
                                .apply {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        setIcon(
                                            IconCompat.createFromIcon(
                                                Icon.createWithResource(
                                                    itemHolder.content,
                                                    R.drawable.image_manhua_nv
                                                )
                                            )
                                        )
                                    }
                                }
                                .build()
                        ),
                        NotificationCompat.MessagingStyle.Message(
                            "MessageText3",
                            nowTime(),
                            Person.Builder().build()
                        ),
                        NotificationCompat.MessagingStyle.Message(
                            "MessageText4",
                            nowTime(),
                            Person.Builder().setName("MName4").setBot(true).setImportant(true).build()
                        )
                    )
            }
        }

        itemHolder.click(R.id.notify_media) {
            dslNotify {
                config(this)
                //styleBigPicture = bitmap
            }
        }
    }
}