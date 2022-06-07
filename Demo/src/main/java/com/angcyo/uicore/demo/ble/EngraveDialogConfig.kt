package com.angcyo.uicore.demo.ble

import android.app.Dialog
import android.content.Context
import com.angcyo.canvas.items.renderer.BaseItemRenderer
import com.angcyo.dialog.BaseDialogConfig
import com.angcyo.dialog.configBottomDialog
import com.angcyo.uicore.demo.R
import com.angcyo.uicore.demo.canvas.EngraveLayoutHelper
import com.angcyo.widget.DslViewHolder

/**
 * 雕刻界面对话框
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/06/02
 */
class EngraveDialogConfig(context: Context? = null) : BaseDialogConfig(context) {

    /**雕刻对象*/
    var renderer: BaseItemRenderer<*>? = null

    val engraveLayoutHelper = EngraveLayoutHelper(this)

    init {
        amount = 0f
        dialogLayoutId = R.layout.canvas_engrave_layout
    }

    override fun initDialogView(dialog: Dialog, dialogViewHolder: DslViewHolder) {
        super.initDialogView(dialog, dialogViewHolder)
        engraveLayoutHelper.renderer = renderer
        engraveLayoutHelper.viewHolder = dialogViewHolder
        engraveLayoutHelper.initLayout()
    }
}

/**蓝牙搜索列表对话框*/
fun Context.engraveDialog(render: BaseItemRenderer<*>, config: EngraveDialogConfig.() -> Unit) {
    return EngraveDialogConfig(this).run {
        configBottomDialog(this@engraveDialog)
        cancelable = false //不允许取消
        this.renderer = render
        config()
        show()
    }
}

