package com.angcyo.uicore.demo.canvas

import android.content.Context
import android.view.View
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.canvas.core.InchValueUnit
import com.angcyo.canvas.core.MmValueUnit
import com.angcyo.dialog.TargetWindow
import com.angcyo.dialog.popup.ShadowAnchorPopupConfig
import com.angcyo.dsladapter.drawBottom
import com.angcyo.item.DslSwitchInfoItem
import com.angcyo.item.style.itemInfoText
import com.angcyo.library.ex._dimen
import com.angcyo.library.ex._string
import com.angcyo.library.ex.dpi
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.recycler.renderDslAdapter

/**
 * 画图设置弹窗
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/16
 */
class CanvasSettingPopupConfig : ShadowAnchorPopupConfig() {

    var canvasDelegate: CanvasDelegate? = null

    init {
        contentLayoutId = R.layout.canvas_setting_layout
        triangleMinMargin = 24 * dpi
        yoff = -10 * dpi
    }

    override fun initContentLayout(window: TargetWindow, viewHolder: DslViewHolder) {
        super.initContentLayout(window, viewHolder)
        val canvasViewBox = canvasDelegate?.getCanvasViewBox()
        viewHolder.rv(R.id.lib_recycler_view)?.renderDslAdapter {
            DslSwitchInfoItem()() {
                itemInfoText = _string(R.string.canvas_inch_unit)
                itemSwitchChecked = canvasViewBox?.valueUnit is InchValueUnit
                drawBottom(_dimen(R.dimen.lib_line_px), 0, 0)
                itemExtendLayoutId = R.layout.canvas_extent_switch_item
                itemSwitchChangedAction = {
                    canvasViewBox?.updateCoordinateSystemUnit(
                        if (it) {
                            InchValueUnit()
                        } else {
                            MmValueUnit()
                        }
                    )
                }
            }
            DslSwitchInfoItem()() {
                itemInfoText = _string(R.string.canvas_grid)
                itemSwitchChecked = canvasDelegate?.xAxis?.drawGridLine == true
                drawBottom(_dimen(R.dimen.lib_line_px), 0, 0)
                itemExtendLayoutId = R.layout.canvas_extent_switch_item
                itemSwitchChangedAction = {
                    if (it) {
                        canvasDelegate?.xAxis?.drawGridLine = true
                        canvasDelegate?.yAxis?.drawGridLine = true
                    } else {
                        canvasDelegate?.xAxis?.drawGridLine = false
                        canvasDelegate?.yAxis?.drawGridLine = false
                    }
                    canvasDelegate?.refresh()
                }
            }
            DslSwitchInfoItem()() {
                itemInfoText = _string(R.string.canvas_smart_assistant)
                itemSwitchChecked = canvasDelegate?.smartAssistant?.enable == true
                itemExtendLayoutId = R.layout.canvas_extent_switch_item
                itemSwitchChangedAction = {
                    canvasDelegate?.smartAssistant?.enable = it
                }
            }
        }
    }
}

/**Dsl*/
fun Context.canvasSettingWindow(anchor: View?, config: CanvasSettingPopupConfig.() -> Unit): Any {
    val popupConfig = CanvasSettingPopupConfig()
    popupConfig.anchor = anchor
    popupConfig.config()
    return popupConfig.show(this)
}