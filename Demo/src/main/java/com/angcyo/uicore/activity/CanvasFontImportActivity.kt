package com.angcyo.uicore.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import com.angcyo.activity.BaseAppCompatActivity
import com.angcyo.canvas.utils.FontManager
import com.angcyo.dsladapter.DslAdapterStatusItem
import com.angcyo.getData
import com.angcyo.library.ex.*
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

    override fun onHandleIntent(intent: Intent, fromNewIntent: Boolean) {
        super.onHandleIntent(intent, fromNewIntent)

        val info = intent.getData<CanvasOpenInfo>()
        if (info == null) {
            finish()
        } else {
            val name = info.url.getFileAttachmentName()
            val typefaceInfo = FontManager.importCustomFont(info.url)
            _vh.rv(R.id.lib_recycler_view)?.renderDslAdapter(false, false, false) {
                if (typefaceInfo == null) {
                    //导入失败
                    setAdapterStatus(
                        DslAdapterStatusItem.ADAPTER_STATUS_ERROR,
                        IllegalStateException("$name\n${_string(R.string.font_import_fail)}")
                    )
                } else {
                    dslAdapterStatusItem.onBindStateLayout = { itemHolder, state ->
                        if (state == DslAdapterStatusItem.ADAPTER_STATUS_ERROR) {
                            itemHolder.img(R.id.lib_image_view)?.apply {
                                setImageResource(R.drawable.lib_ic_succeed)
                                setWidthHeight(80 * dpi, 80 * dpi)
                            }
                        }
                    }
                    //导入成功
                    setAdapterStatus(
                        DslAdapterStatusItem.ADAPTER_STATUS_ERROR,
                        IllegalStateException("$name\n${_string(R.string.font_import_success)}")
                    )
                }
            }
        }

        /*val action = intent.action
        if (action == Intent.ACTION_VIEW) {
            val data = intent.data
            //content://com.estrongs.files/storage/emulated/0/Menlo-BoldItalic.ttf
            //val dataString = intent.dataString
            val info = CanvasFontPopupConfig.importFont(data)
        }*/
    }
}