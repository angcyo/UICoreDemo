package com.angcyo.uicore.activity

import android.content.Context
import android.content.Intent
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import android.os.Bundle
import com.angcyo.dsladapter.dslItem
import com.angcyo.getOriginIntent
import com.angcyo.library.L
import com.angcyo.library.component.RNfc
import com.angcyo.library.component._delay
import com.angcyo.library.component.toLog
import com.angcyo.library.ex.connect
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.randomColor
import com.angcyo.library.ex.size
import com.angcyo.library.toastQQ
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.R
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/08
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class NfcInfoDemo : AppDslFragment(), NfcAdapter.CreateNdefMessageCallback {

    init {
        fragmentTitle = "NfcInfoDemo"
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (RNfc.isNfcEnable()) {
            RNfc.nfcAdapter?.setNdefPushMessageCallback(this, requireActivity())
        }
    }

    override fun onDetach() {
        super.onDetach()
        /*if (RNfc.isNfcEnable()) {
            RNfc.nfcAdapter?.setNdefPushMessageCallback(null, null)
        }*/
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        arguments?.getOriginIntent()?.let {
            updateInfo(it)
        }
    }

    fun updateInfo(intent: Intent?) {
        if (view == null) {
            if (isAdded) {
                _delay {
                    updateInfo(intent)
                }
            }
            return
        }

        renderDslAdapter(true) {
            dslItem(R.layout.nfc_layout) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.tv(R.id.state_view)?.text = span {
                        append("NFC硬件:${RNfc.isNfcSupport()} 激活:${RNfc.isNfcEnable()}")
                    }

                    if (RNfc.isNfcEnable()) {
                        itemHolder.tv(R.id.tip_view)?.text = span {
                            append("请将NFC标签, 靠近设备NFC读卡区域!")
                            appendln()
                            append(nowTimeString()) {
                                foregroundColor = randomColor()
                            }
                        }
                        if (intent != null) {

                            itemHolder.tv(R.id.info_view)?.text = span {
                                append("${intent.extras ?: "--"}")
                                appendln()
                                appendln()
                                append("action:${intent.action ?: "--"}")
                                appendln()
                                append("id:${RNfc.byte2HexString(intent.getByteArrayExtra(NfcAdapter.EXTRA_ID))}")
                                appendln()
                                appendln()
                                append(RNfc.getNfcTag(intent).toLog())
                            }

                            itemHolder.tv(R.id.message_view)?.text = span {
                                append("Ndef消息数量:${RNfc.getNfcNdefMessages(intent).size()}")
                                appendln()
                                append(RNfc.getNfcNdefStringMessages(intent)?.connect("\n"))
                            }
                        }
                    }

                    itemHolder.click(R.id.nfc_button) {
                        RNfc.startNfcSetting(requireActivity())
                    }

                    itemHolder.click(R.id.message_button) {
                        RNfc.nfcAdapter?.setNdefPushMessage(
                            ndefMessage("button"),
                            requireActivity()
                        )
                    }

                    itemHolder.click(R.id.close_dispatch_button) {
                        RNfc.disableNfcForegroundDispatch(requireActivity())
                    }
                    itemHolder.click(R.id.close_message_button) {
                        RNfc.disableNfcForegroundNdefPush(requireActivity())
                    }
                }
            }
        }
    }

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)
        RNfc.enableNfcForegroundDispatch(requireActivity())
        RNfc.enableNfcForegroundNdefPush(requireActivity(), ndefMessage("push"))
    }

    override fun onFragmentHide() {
        super.onFragmentHide()
        RNfc.disableNfcForegroundDispatch(requireActivity())
        RNfc.disableNfcForegroundNdefPush(requireActivity())
    }

    override fun createNdefMessage(event: NfcEvent?): NdefMessage {
        L.w(event)
        toastQQ("createNdefMessage:${nowTimeString()}")
        return ndefMessage("event")
    }

    fun ndefMessage(message: String): NdefMessage {
        val text = "from:${message}\n\n"
        "Beam me up, Android!\n\n" +
                "Beam Time: " + nowTimeString()
        return NdefMessage(
            arrayOf(
                NdefRecord.createMime(
                    "application/vnd.com.example.android.beam",
                    text.toByteArray()
                ),
                NdefRecord.createTextRecord("UTF-8", text)
            )
            /**
             * The Android Application Record (AAR) is commented out. When a device
             * receives a push with an AAR in it, the application specified in the AAR
             * is guaranteed to run. The AAR overrides the tag dispatch system.
             * You can add it back in to guarantee that this
             * activity starts when receiving a beamed message. For now, this code
             * uses the tag dispatch system.
             *///,NdefRecord.createApplicationRecord("com.example.android.beam")
        )
    }

}