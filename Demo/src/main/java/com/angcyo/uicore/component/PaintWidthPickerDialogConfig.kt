package com.angcyo.uicore.component

import android.app.Dialog
import android.content.Context
import com.angcyo.dialog.BaseDialogConfig
import com.angcyo.dialog.configBottomDialog
import com.angcyo.drawable.base.PathDrawable
import com.angcyo.library.L
import com.angcyo.library.ex._string
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.progress.DslBlockSeekBar

/**
 * 画笔大小选择器
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/08/12
 */
class PaintWidthPickerDialogConfig : BaseDialogConfig() {

    /**默认的宽度*/
    var initialWidth: Float = 20f

    /**选中的宽度*/
    var selectedWidth: Float = 0f

    /**最小和最大的宽高*/
    var minPaintWidth: Float = 1f

    var maxPaintWidth: Float = 200f

    /**选中回调, 返回true拦截默认操作*/
    var paintWidthResultAction: (dialog: Dialog, width: Float) -> Boolean =
        { dialog, width ->
            L.i("选中大小->$width px")
            false
        }

    init {
        dialogLayoutId = R.layout.dialog_paint_width_picker_layout
        dialogTitle = _string(R.string.dialog_width_picker)

        positiveButtonListener = { dialog, _ ->
            if (paintWidthResultAction.invoke(dialog, selectedWidth)) {
                //被拦截
            } else {
                dialog.dismiss()
            }
        }
    }

    override fun initDialogView(dialog: Dialog, dialogViewHolder: DslViewHolder) {
        super.initDialogView(dialog, dialogViewHolder)

        val drawable = PathDrawable()
        val view = dialogViewHolder.view(R.id.lib_tip_view)
        val textView = dialogViewHolder.tv(R.id.lib_text_view)
        view?.background = drawable
        dialogViewHolder.v<DslBlockSeekBar>(R.id.lib_seek_bar)?.apply {
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    selectedWidth = _value(value)
                    drawable.textPaint.strokeWidth = selectedWidth / 2
                    textView?.text = "${selectedWidth}px"
                    drawable.addCircle(view, drawable.textPaint.strokeWidth)
                }
            }
            post {
                setProgress(_progress(initialWidth), animDuration = -1)
            }
        }
    }

    fun _progress(value: Float): Int {
        return ((value - minPaintWidth) / (maxPaintWidth - minPaintWidth) * 100).toInt()
    }

    fun _value(progress: Float): Float {
        //
        return minPaintWidth + (maxPaintWidth - minPaintWidth) * progress / 100
    }
}

/** 画笔大小选择 */
fun Context.paintWidthPickerDialog(config: PaintWidthPickerDialogConfig.() -> Unit): Dialog {
    return PaintWidthPickerDialogConfig().run {
        configBottomDialog(this@paintWidthPickerDialog)
        //initialWidth
        config()
        show()
    }
}
