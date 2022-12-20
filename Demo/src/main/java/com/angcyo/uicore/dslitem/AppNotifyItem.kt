package com.angcyo.uicore.dslitem

import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.graphics.drawable.IconCompat
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.core.component.layoutQQ
import com.angcyo.core.component.layoutWX
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.component.DslNotify
import com.angcyo.library.component.dslNotify
import com.angcyo.library.component.dslRemoteView
import com.angcyo.library.ex._color
import com.angcyo.library.ex.anim
import com.angcyo.library.ex.nowTime
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.toastQQ
import com.angcyo.library.toastWX
import com.angcyo.uicore.MainActivity
import com.angcyo.uicore.app.AppBroadcastReceiver
import com.angcyo.uicore.app.AppService
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.string
import com.angcyo.widget.spinner
import kotlin.random.Random.Default.nextInt

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
            BitmapFactory.decodeResource(itemHolder.context.resources, R.drawable.image_manhua_nv)

        //默认配置

        fun config(notify: DslNotify) {
            notify.apply {

                channelEnableLights = itemHolder.isChecked(R.id.lights_cb)
                channelEnableVibration = itemHolder.isChecked(R.id.vibration_cb)
                notifyShowWhen = itemHolder.isChecked(R.id.show_when_cb)

                if (itemHolder.isChecked(R.id.small_ico_cb)) {
                    notifySmallIcon = R.mipmap.fast_green
                }

                notifyNumber = nextInt(0, 10)

                notifyOngoing = itemHolder.isChecked(R.id.ongoing_cb)

                if (itemHolder.isChecked(R.id.large_icon_cb)) {
                    notifyLargeIcon = bitmap
                }

                if (itemHolder.isChecked(R.id.big_large_icon_cb)) {
                    styleBigLargeIcon = bitmap
                }

                notifyTitle = "${itemHolder.tv(R.id.notify_title_view).string()} ${nowTimeString()}"
                notifyText =
                    "${itemHolder.tv(R.id.notify_message_view).string()} ${nowTimeString()}"

                notifyColor = _color(R.color.colorPrimary)

                channelName = itemHolder.tv(R.id.notify_channel_view).string()

                channelDescription = itemHolder.tv(R.id.notify_channel_des_view).string()

                if (itemHolder.isChecked(R.id.sub_text_cb)) {
                    notifyInfo = "notifyInfo"
                    notifySubText = "notifySubText"
                }

                itemHolder.spinner(R.id.defaults_spinner)?.getSelectedData<String>()?.run {
                    notifyDefaults = split(" ")[1].toInt()
                }

                itemHolder.spinner(R.id.priority_spinner)?.getSelectedData<String>()?.run {
                    notifyPriority = split(" ")[1].toInt()
                }

                itemHolder.spinner(R.id.visibility_spinner)?.getSelectedData<String>()?.run {
                    notifyVisibility = split(" ")[1].toInt()
                }

                itemHolder.spinner(R.id.importance_spinner)?.getSelectedData<String>()?.run {
                    channelImportance = split(" ")[1].toInt()
                }

                if (itemHolder.isChecked(R.id.click_cb)) {
                    notifyContentIntent =
                        DslNotify.pendingActivity(itemHolder.context, MainActivity::class.java)
                }

                if (itemHolder.isChecked(R.id.full_screen_intent_cb)) {
                    notifyFullScreenIntent = DslNotify.pendingActivity(itemHolder.context, Intent())
                }

                if (itemHolder.isChecked(R.id.progress_indeterminate_cb)) {
                    notifyProgressIndeterminate = true
                }

                if (itemHolder.isChecked(R.id.action_cb)) {
                    notifyActions = listOf(
                        DslNotify.action(
                            "action1",
                            DslNotify.pendingActivity(itemHolder.context, MainActivity::class.java),
                            android.R.drawable.ic_media_pause
                        ),
                        DslNotify.action(
                            "action2",
                            DslNotify.pendingBroadcast(
                                itemHolder.context,
                                Intent(AppBroadcastReceiver.ACTION_DEMO)
                            ),
                            android.R.drawable.ic_media_play
                        ),
                        DslNotify.action(
                            "action3",
                            DslNotify.pendingService(itemHolder.context, AppService::class.java),
                            android.R.drawable.ic_media_next
                        ),
                        DslNotify.action(
                            "action4",
                            DslNotify.pendingService(itemHolder.context, Intent()),
                            android.R.drawable.ic_media_previous
                        ),
                        DslNotify.action(
                            "action5",
                            DslNotify.pendingService(itemHolder.context, Intent()),
                            android.R.drawable.ic_media_ff
                        ),
                        DslNotify.action(
                            "action5",
                            DslNotify.pendingService(itemHolder.context, Intent()),
                            android.R.drawable.ic_media_rew
                        )
                    )
                }

                if (itemHolder.isChecked(R.id.custom_view)) {
                    notifyContentView = dslRemoteView { layoutId = R.layout.layout_notify_custom }
                }
                if (itemHolder.isChecked(R.id.custom_content_view)) {
                    notifyCustomContentView =
                        dslRemoteView { layoutId = R.layout.layout_notify_custom_conent }
                }
                if (itemHolder.isChecked(R.id.custom_big_content_view)) {
                    notifyCustomBigContentView =
                        dslRemoteView { layoutId = R.layout.layout_notify_custom_big }
                }
                if (itemHolder.isChecked(R.id.custom_heads_up_view)) {
                    notifyCustomHeadsUpContentView =
                        dslRemoteView { layoutId = R.layout.layout_notify_custom_heads_up }
                }
            }
        }

        //初始化

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

        itemHolder.spinner(R.id.importance_spinner)
            ?.setStrings(
                listOf(
                    "IMPORTANCE_DEFAULT ${NotificationManagerCompat.IMPORTANCE_DEFAULT}",
                    "IMPORTANCE_UNSPECIFIED ${NotificationManagerCompat.IMPORTANCE_UNSPECIFIED}",
                    "IMPORTANCE_NONE ${NotificationManagerCompat.IMPORTANCE_NONE}",
                    "IMPORTANCE_LOW ${NotificationManagerCompat.IMPORTANCE_LOW}",
                    "IMPORTANCE_MIN ${NotificationManagerCompat.IMPORTANCE_MIN}",
                    "IMPORTANCE_HIGH ${NotificationManagerCompat.IMPORTANCE_HIGH}",
                    "IMPORTANCE_MAX ${NotificationManagerCompat.IMPORTANCE_MAX}"
                )
            )

        //事件

        itemHolder.click(R.id.notify_normal) {
            dslNotify {
                config(this)
            }
        }

        itemHolder.click(R.id.notify_progress) {
            val id = 0x8899
            anim(0, 100) {
                onAnimatorConfig = {
                    it.duration = 3_000
                }
                onAnimatorUpdateValue = { value, _ ->
                    dslNotify {
                        config(this)
                        notifyId = id
                        notifyProgress = value as Int

                        notifyWhen = 0
                    }
                }
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
                                                itemHolder.context,
                                                Icon.createWithResource(
                                                    itemHolder.context,
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
                            Person.Builder().setName("MName4").setBot(true).setImportant(true)
                                .build()
                        )
                    )
            }
        }

        itemHolder.click(R.id.notify_media) {
            dslNotify {
                config(this)
                notifyActions = listOf(
                    DslNotify.action(
                        "action1",
                        DslNotify.pendingActivity(itemHolder.context, MainActivity::class.java),
                        android.R.drawable.ic_media_pause
                    ),
                    DslNotify.action(
                        "action2",
                        DslNotify.pendingBroadcast(
                            itemHolder.context,
                            Intent(AppBroadcastReceiver.ACTION_DEMO)
                        ),
                        android.R.drawable.ic_media_play
                    ),
                    DslNotify.action(
                        "action3",
                        DslNotify.pendingService(itemHolder.context, AppService::class.java),
                        android.R.drawable.ic_media_next
                    )
                )
                styleMediaShowActions = listOf(0, 1, 2)
            }
        }

        //自动保存值
        itemHolder.hawkInstallAndRestore("notify")

        //toast layout 控制
        itemHolder.click(R.id.toast_qq_layout) {
            layoutQQ(nowTimeString())
        }
        itemHolder.click(R.id.toast_wx_layout) {
            layoutWX(nowTimeString())
        }

        itemHolder.click(R.id.toast_qq) {
            toastQQ(nowTimeString())
        }
        itemHolder.click(R.id.toast_wx) {
            toastWX(nowTimeString())
        }
    }
}