package com.angcyo.uicore.activity

import android.content.Intent
import android.os.Bundle
import com.angcyo.base.dslFHelper
import com.angcyo.core.activity.BaseCoreAppCompatActivity
import com.angcyo.core.component.ShareSendFragment
import com.angcyo.library.component.ROpenFileHelper
import com.angcyo.putData
import com.angcyo.putParcelableList

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