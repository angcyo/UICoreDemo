package com.angcyo.uicore.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.dsladapter.DslAdapterStatusItem
import com.angcyo.engrave.canvas.CanvasFontPopupConfig
import com.angcyo.uicore.demo.R
import com.angcyo.widget.recycler.renderDslAdapter

/**
 * 字体导入[Activity]
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/06/08
 */
class CanvasFontImportActivity : BaseAppCompatActivity() {

    init {
        activityLayoutId = R.layout.lib_recycler_layout
    }

    override fun onCreateAfter(savedInstanceState: Bundle?) {
        super.onCreateAfter(savedInstanceState)
        _vh.rv(R.id.lib_recycler_view)?.apply {
            setBackgroundColor(Color.WHITE)
            renderDslAdapter {
                setAdapterStatus(DslAdapterStatusItem.ADAPTER_STATUS_LOADING)
            }
        }
    }

    override fun onHandleIntent(intent: Intent, fromNew: Boolean) {
        super.onHandleIntent(intent, fromNew)
        val action = intent.action
        if (action == Intent.ACTION_VIEW) {
            val data = intent.data
            //content://com.estrongs.files/storage/emulated/0/Menlo-BoldItalic.ttf
            //val dataString = intent.dataString
            val info = CanvasFontPopupConfig.importFont(data)

            _vh.rv(R.id.lib_recycler_view)?.renderDslAdapter(false, false) {
                if (info == null) {
                    //导入失败
                    setAdapterStatus(
                        DslAdapterStatusItem.ADAPTER_STATUS_ERROR,
                        IllegalStateException("字体导入失败")
                    )
                } else {
                    //导入成功
                    setAdapterStatus(
                        DslAdapterStatusItem.ADAPTER_STATUS_ERROR,
                        IllegalStateException("字体导入成功")
                    )
                }
            }
        } else {
            finish()
        }
    }
}