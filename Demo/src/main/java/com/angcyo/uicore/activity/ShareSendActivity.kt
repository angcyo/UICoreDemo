package com.angcyo.uicore.activity

import android.content.Intent
import android.os.Bundle
import com.angcyo.base.dslFHelper
import com.angcyo.core.activity.BaseCoreAppCompatActivity
import com.angcyo.core.component.ShareSendFragment
import com.angcyo.core.component.model.DataShareModel
import com.angcyo.core.component.receiveFileDialogConfig
import com.angcyo.core.vmApp
import com.angcyo.library.component.ROpenFileHelper
import com.angcyo.library.ex._color
import com.angcyo.putData
import com.angcyo.putParcelableList
import com.angcyo.server.startFileServer
import com.angcyo.server.stopFileServer
import com.angcyo.uicore.demo.R
import com.angcyo.widget.span.span

/**
 * 分享过来用来发送文件到指定服务器
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2023/04/09
 * Copyright (c) 2020 angcyo. All rights reserved.
 */
class ShareSendActivity : BaseCoreAppCompatActivity() {
    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)

        dslFHelper {
            removeAll()
            show(ShareSendFragment::class) {
                if (this is ShareSendFragment) {
                    receiveFileAction = {
                        startFileServer()
                        vmApp<DataShareModel>().shareServerAddressOnceData.observeOnce { address ->
                            address?.let {
                                receiveFileDialogConfig {
                                    dialogTitle = span {
                                        append("服务地址: ") {
                                            foregroundColor = _color(R.color.colorAccent)
                                        }
                                        append(address)
                                        appendln()
                                        append("发送文件[POST]: ") {
                                            foregroundColor = _color(R.color.colorAccent)
                                        }
                                        append("/uploadFile")
                                        appendln()
                                        append("发送文本[POST]: ") {
                                            foregroundColor = _color(R.color.colorAccent)
                                        }
                                        append("/body")
                                    }
                                    onDismissListener = {
                                        stopFileServer()
                                    }
                                }
                            }
                            address != null
                        }
                    }
                }
                ROpenFileHelper.parseIntentUri(intent)?.let {
                    putParcelableList(it, ShareSendFragment.KEY_URI_LIST)
                }
                putData {
                    putString(ShareSendFragment.KEY_TEXT, intent?.getStringExtra(Intent.EXTRA_TEXT))
                }
            }
        }
    }
}