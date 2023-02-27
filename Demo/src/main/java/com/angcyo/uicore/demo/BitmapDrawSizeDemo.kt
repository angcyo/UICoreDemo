package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.bindItem
import com.angcyo.library.ex.nowTimeString
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.draw.BitmapDrawSizeView
import com.angcyo.widget.base.onTextChange
import com.angcyo.widget.base.setInputText
import com.angcyo.widget.edit.DslEditText
import com.angcyo.widget.progress.DslProgressBar
import com.angcyo.widget.progress.DslSeekBar
import kotlin.math.roundToInt

/**
 * 测试能绘制多大的图片size
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2023/02/27
 */
class BitmapDrawSizeDemo : AppDslFragment() {

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            bindItem(R.layout.demo_bitmap_draw_size) { itemHolder, itemPosition, adapterItem, payloads ->
                val drawSizeView = itemHolder.v<BitmapDrawSizeView>(R.id.bitmap_draw_size_view)
                val widthEdit = itemHolder.v<DslEditText>(R.id.width_edit)
                val heightEdit = itemHolder.v<DslEditText>(R.id.height_edit)

                widthEdit?.onTextChange {
                    it.toString().toFloatOrNull()?.let {
                        drawSizeView?.drawWidth = it.roundToInt()
                    }
                }

                heightEdit?.onTextChange {
                    it.toString().toFloatOrNull()?.let {
                        drawSizeView?.drawHeight = it.roundToInt()
                    }
                }

                drawSizeView?.onExceptionAction = { exception ->
                    itemHolder.tv(R.id.result_text_view)?.text = "测试失败:${exception}"
                }

                itemHolder.v<DslSeekBar>(R.id.width_seek)?.apply {
                    progressTextFormatAction = this@BitmapDrawSizeDemo::formatValueTextAction
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            if (fromUser) {
                                widthEdit?.setInputText("${formatValue(fraction)}")
                            }
                        }
                    }
                }

                itemHolder.v<DslSeekBar>(R.id.height_seek)?.apply {
                    progressTextFormatAction = this@BitmapDrawSizeDemo::formatValueTextAction
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            if (fromUser) {
                                heightEdit?.setInputText("${formatValue(fraction)}")
                            }
                        }
                    }
                }

                itemHolder.v<DslSeekBar>(R.id.dpi_seek)?.apply {
                    progressTextFormatAction = this@BitmapDrawSizeDemo::formatDpiValueTextAction
                    config {
                        onSeekChanged = { value, fraction, fromUser ->
                            if (fromUser) {
                                drawSizeView?.drawDpi = formatDpiValue(fraction)
                            }
                        }
                    }
                }

                itemHolder.click(R.id.test_button) {
                    val exception = drawSizeView?.test()
                    if (exception == null) {
                        itemHolder.tv(R.id.result_text_view)?.text = "测试通过:${nowTimeString()}"
                    }
                }
            }
        }
    }

    fun formatValue(fraction: Float): Int {
        val min = 1000f
        val max = 10000f
        val target = min + (max - min) * fraction
        return target.roundToInt()
    }

    fun formatDpiValue(fraction: Float): Int {
        val min = 1f
        val max = 10f
        val target = min + (max - min) * fraction
        return target.roundToInt()
    }

    fun formatValueTextAction(bar: DslProgressBar): String =
        "${formatValue(bar.progressValue / 100f)}"

    fun formatDpiValueTextAction(bar: DslProgressBar): String =
        "${formatDpiValue(bar.progressValue / 100f)}"
}