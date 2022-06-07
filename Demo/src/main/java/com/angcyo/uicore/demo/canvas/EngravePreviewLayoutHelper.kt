package com.angcyo.uicore.demo.canvas

import android.view.MotionEvent
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.angcyo.bluetooth.fsc.IReceiveBeanAction
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerHelper
import com.angcyo.bluetooth.fsc.laserpacker.LaserPeckerModel
import com.angcyo.bluetooth.fsc.laserpacker.command.EngravePreviewCmd
import com.angcyo.bluetooth.fsc.laserpacker.command.ExitCmd
import com.angcyo.bluetooth.fsc.laserpacker.parse.QueryStateParser
import com.angcyo.canvas.CanvasDelegate
import com.angcyo.core.vmApp
import com.angcyo.library.ex._string
import com.angcyo.library.ex.disableParentInterceptTouchEvent
import com.angcyo.library.ex.elseNull
import com.angcyo.library.ex.longFeedback
import com.angcyo.uicore.demo.R
import com.angcyo.widget.image.TouchCompatImageView
import com.angcyo.widget.progress.DslSeekBar

/**
 * 雕刻预览布局相关操作
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/06/01
 */
class EngravePreviewLayoutHelper(val fragment: Fragment) : BaseEngraveLayoutHelper() {

    /**支架的最大移动步长*/
    val BRACKET_MAX_STEP: Int = 65535//130, 65535

    val laserPeckerModel = vmApp<LaserPeckerModel>()

    init {
        layoutId = R.layout.canvas_engrave_preview_layout
        //模式改变监听
        laserPeckerModel.deviceStateData.observe(fragment) {
            val mode = it?.mode
            if (mode == QueryStateParser.WORK_MODE_ENGRAVE_PREVIEW) {
                if (it.workState == 7) {
                    //显示中心模式
                    viewHolder?.tv(R.id.preview_button)?.text =
                        _string(R.string.preview_continue)
                } else {
                    viewHolder?.tv(R.id.preview_button)?.text =
                        _string(R.string.print_v2_package_preview_over)
                }
            } else if (mode == QueryStateParser.WORK_MODE_IDLE) {
                viewHolder?.tv(R.id.preview_button)?.text =
                    _string(R.string.preview_continue)
            }
        }
    }

    /**显示预览布局, 并且发送预览指令*/
    override fun showLayout(viewGroup: ViewGroup, canvasDelegate: CanvasDelegate?) {
        super.showLayout(viewGroup, canvasDelegate)

        //init
        viewHolder?.v<DslSeekBar>(R.id.brightness_seek_bar)?.apply {
            setProgress((LaserPeckerHelper.lastPwrProgress * 100).toInt())
            config {
                onSeekChanged = { value, fraction, fromUser ->
                    LaserPeckerHelper.lastPwrProgress = fraction
                    if (laserPeckerModel.isEngravePreviewMode()) {
                        startPreviewCmd(canvasDelegate)
                    } else if (laserPeckerModel.isEngravePreviewShowCenterMode()) {
                        showPreviewCenterCmd()
                    } else if (!laserPeckerModel.isIdleMode()) {
                        exitCmd { bean, error ->
                            queryDeviceStateCmd()
                        }
                    }
                }
            }
        }
        viewHolder?.v<TouchCompatImageView>(R.id.bracket_up_view)?.touchAction = { view, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                view.disableParentInterceptTouchEvent()
                view.longFeedback()
                bracketUpCmd { bean, error ->

                }
            } else if (event.actionMasked == MotionEvent.ACTION_UP ||
                event.actionMasked == MotionEvent.ACTION_CANCEL
            ) {
                view.disableParentInterceptTouchEvent(false)
                bracketStopCmd()
            }
        }
        viewHolder?.v<TouchCompatImageView>(R.id.bracket_down_view)?.touchAction = { view, event ->
            if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                view.disableParentInterceptTouchEvent()
                view.longFeedback()
                bracketDownCmd { bean, error ->

                }
            } else if (event.actionMasked == MotionEvent.ACTION_UP ||
                event.actionMasked == MotionEvent.ACTION_CANCEL
            ) {
                view.disableParentInterceptTouchEvent(false)
                bracketStopCmd()
            }
        }
        viewHolder?.click(R.id.bracket_stop_view) {
            bracketStopCmd()
        }
        viewHolder?.click(R.id.close_layout_view) {
            hideLayout()
        }
        viewHolder?.click(R.id.centre_button) {
            if (laserPeckerModel.isEngravePreviewShowCenterMode()) {
                showPreviewCenterCmd()
            } else {
                exitCmd { bean, error ->
                    showPreviewCenterCmd()
                }
            }
        }
        viewHolder?.click(R.id.preview_button) {
            if (laserPeckerModel.isEngravePreviewShowCenterMode()) {
                //继续预览
                exitCmd { bean, error ->
                    startPreviewCmd(canvasDelegate)
                }
            } else {
                //结束预览
                hideLayout()
            }
            /*if (laserPeckerModel.isEngravePreviewMode()) {
                exitCmd { bean, error ->
                    queryDeviceStateCmd()
                }
            } else if (laserPeckerModel.isIdleMode()) {
                startPreviewCmd(canvasDelegate)
            } else {
                exitCmd { bean, error ->
                    queryDeviceStateCmd()
                }
            }*/
        }

        //cmd
        startPreviewCmd(canvasDelegate)
    }

    /**隐藏预览布局, 并且停止预览*/
    override fun hideLayout() {
        super.hideLayout()
        exitCmd { bean, error ->
            queryDeviceStateCmd()
        }
    }

    //region ------command------

    /**查询设备状态*/
    fun queryDeviceStateCmd() {
        vmApp<LaserPeckerModel>().queryDeviceState()
    }

    /**开始预览*/
    fun startPreviewCmd(canvasDelegate: CanvasDelegate?) {
        canvasDelegate?.getSelectedRenderer()?.let { renderer ->
            renderer.getRotateBounds().let { bounds ->
                val cmd = EngravePreviewCmd.previewRange(
                    bounds.left.toInt(),
                    bounds.top.toInt(),
                    bounds.width().toInt(),
                    bounds.height().toInt()
                )
                LaserPeckerHelper.sendCommand(cmd) { bean, error ->
                    queryDeviceStateCmd()
                }
            }
        }.elseNull {
            queryDeviceStateCmd()
        }
    }

    /**停止预览*/
    fun stopPreviewCmd() {
        val cmd = EngravePreviewCmd.previewStop()
        LaserPeckerHelper.sendCommand(cmd) { bean, error ->
            queryDeviceStateCmd()
        }
    }

    /**支架上升*/
    fun bracketUpCmd(action: IReceiveBeanAction) {
        val cmd = EngravePreviewCmd.previewBracketUp(BRACKET_MAX_STEP)
        LaserPeckerHelper.sendCommand(cmd) { bean, error ->
            action(bean, error)
        }
    }

    /**支架下降*/
    fun bracketDownCmd(action: IReceiveBeanAction) {
        val cmd = EngravePreviewCmd.previewBracketDown(BRACKET_MAX_STEP)
        LaserPeckerHelper.sendCommand(cmd) { bean, error ->
            action(bean, error)
        }
    }

    /**停止支架*/
    fun bracketStopCmd() {
        val cmd = EngravePreviewCmd.previewBracketStop()
        LaserPeckerHelper.sendCommand(cmd) { bean, error ->

        }
    }

    /**显示中心*/
    fun showPreviewCenterCmd() {
        val cmd = EngravePreviewCmd.previewShowCenter()
        LaserPeckerHelper.sendCommand(cmd) { bean, error ->
            queryDeviceStateCmd()
        }
    }

    /**退出工作模式*/
    fun exitCmd(action: IReceiveBeanAction) {
        val cmd = ExitCmd()
        LaserPeckerHelper.sendCommand(cmd) { bean, error ->
            action(bean, error)
        }
    }

    //endregion

}