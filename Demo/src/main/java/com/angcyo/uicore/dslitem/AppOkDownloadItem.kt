package com.angcyo.uicore.dslitem

import com.angcyo.download.DslDownload
import com.angcyo.download.DslListener
import com.angcyo.download.dslDownload
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.L
import com.angcyo.library.app
import com.angcyo.library.ex.installApk
import com.angcyo.library.ex.simpleHash
import com.angcyo.uicore.demo.R
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.progress.DslProgressBar
import com.liulishuo.okdownload.StatusUtil
import com.liulishuo.okdownload.core.cause.EndCause

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/26
 */
class AppOkDownloadItem : DslAdapterItem() {

    var downloadUrl: String? = null

    val listener = DslListener().apply {
        onTaskStart = { downloadTask ->
            L.i(downloadTask)
            updateAdapterItem()
        }

        onTaskProgress = { downloadTask, progress ->
            L.i("进度回调:${this@AppOkDownloadItem.simpleHash()}", downloadTask, progress)
            updateAdapterItem()
        }

        onTaskFinish = { downloadTask, cause, exception ->
            L.i(downloadTask, cause, exception)
            updateAdapterItem()
        }
    }

    init {
        itemLayoutId = R.layout.app_item_ok_download

//        onItemViewRecycled = {
//
//        }
//        onItemViewAttachedToWindow = {
//            L.i("添加监听:${this@AppOkDownloadItem.simpleHash()}")
//            DslDownload.listener(downloadUrl, listener)
//        }
        onItemViewDetachedToWindow = {
            L.i("移除监听:${this@AppOkDownloadItem.simpleHash()}")
            DslDownload.removeListener(downloadUrl, listener)
            //DslDownload.cancel(downloadUrl)
        }

        onItemClick = {

        }
    }

    override fun onItemBind(
        itemHolder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        super.onItemBind(itemHolder, itemPosition, adapterItem)

        DslDownload.listener(downloadUrl, listener)

        itemHolder.tv(R.id.url_view)?.text = "$itemPosition $downloadUrl"

        DslDownload.findTask(downloadUrl)?.apply {
            when (DslDownload.getStatus(this)) {
                StatusUtil.Status.RUNNING -> itemHolder.tv(R.id.button)?.text = "ing..."
                StatusUtil.Status.COMPLETED -> itemHolder.tv(R.id.button)?.text = "finish"
                else -> itemHolder.tv(R.id.button)?.text = "start"
            }

            val taskProgress = DslDownload.getTaskProgress(this)
            itemHolder.v<DslProgressBar>(R.id.progress_bar)
                ?.setProgress(taskProgress)
            L.i("获取进度:${this@AppOkDownloadItem.simpleHash()}", " ", taskProgress, " ", downloadUrl)

            itemHolder.tv(R.id.file_view)?.text = file?.absolutePath
        }

        itemHolder.click(R.id.button) {
            //已经在下载, 则取消下载
            if (DslDownload.getStatus(downloadUrl) == StatusUtil.Status.RUNNING) {
                DslDownload.cancel(downloadUrl)
                return@click
            }
//            if (DslDownload.getStatus(downloadUrl) == StatusUtil.Status.COMPLETED) {
//                if (DslDownload.findTask(downloadUrl)?.file?.absolutePath?.endsWith("apk") == true) {
//                    installApk(app(), DslDownload.findTask(downloadUrl)?.file)
//                    return@click
//                }
//            }
            //下载流程
            dslDownload(downloadUrl) {
                onConfigTask = {
                    it.setPassIfAlreadyCompleted(false)
                }

                onTaskStart = {
                    itemHolder.tv(R.id.button)?.text = "ing..."
                }

                onTaskProgress = { downloadTask, progress ->
                    itemHolder.v<DslProgressBar>(R.id.progress_bar)?.setProgress(progress)
                }

                onTaskFinish = { downloadTask, cause, exception ->
                    if (cause == EndCause.COMPLETED) {
                        itemHolder.v<DslProgressBar>(R.id.progress_bar)?.setProgress(100)

                        //下载完成是个APK, 则安装
                        if (downloadTask.file?.absolutePath?.endsWith("apk") == true) {
                            installApk(app(), DslDownload.findTask(downloadUrl)?.file)
                        }
                    }
                    itemHolder.tv(R.id.button)?.text = "$cause"
                }
            }?.apply {
                itemHolder.tv(R.id.file_view)?.text = file?.absolutePath
            }
        }
    }
}