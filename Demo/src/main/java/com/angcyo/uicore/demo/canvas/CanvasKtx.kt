package com.angcyo.uicore.demo.canvas

import android.app.Dialog
import androidx.activity.result.ActivityResultCaller
import com.angcyo.dialog.hideLoading
import com.angcyo.dialog.loading
import com.angcyo.drawable.loading.TGStrokeLoadingDrawable
import com.angcyo.library.ex.dp
import com.angcyo.library.ex.setBgDrawable
import com.angcyo.library.ex.toColorInt
import com.angcyo.library.toastQQ
import com.angcyo.uicore.demo.R
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/05/20
 */

/**
 * TGStrokeLoadingDrawable 加载样式的loading
 * [cancel] 是否允许被取消*/
fun ActivityResultCaller.strokeLoading(
    cancel: Boolean = false,
    showErrorToast: Boolean = false,
    action: (cancel: AtomicBoolean, loadEnd: (data: Any?, error: Throwable?) -> Unit) -> Unit
): Dialog? {
    val isCancel = AtomicBoolean(false)
    val dialog = loading(layoutId = R.layout.canvas_loading_layout, config = {
        cancelable = cancel
        onDialogInitListener = { dialog, dialogViewHolder ->
            val loadingDrawable = TGStrokeLoadingDrawable().apply {
                loadingOffset = 6 * dp
                loadingWidth = 6 * dp
                indeterminateSweepAngle = 1f
                loadingBgColor = "#ffffff".toColorInt()
                loadingColor = loadingBgColor
            }
            dialogViewHolder.view(R.id.lib_loading_view)?.setBgDrawable(loadingDrawable)
        }
    }) { dialog ->
        isCancel.set(true)
        action(isCancel) { _, _ ->
            //no op
        }
    }

    isCancel.set(false)
    action(isCancel) { data, error ->
        if (error != null) {
            //失败
            if (showErrorToast) {
                toastQQ(error.message)
            }
            hideLoading(error.message)
        } else {
            hideLoading()
        }
    }

    return dialog
}