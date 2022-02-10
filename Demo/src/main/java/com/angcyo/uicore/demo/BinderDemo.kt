package com.angcyo.uicore.demo

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import com.angcyo.base.dslAHelper
import com.angcyo.core.vmApp
import com.angcyo.dsladapter.dslItem
import com.angcyo.dsladapter.itemViewHolder
import com.angcyo.library.app
import com.angcyo.library.ex.*
import com.angcyo.library.getProcessName
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.demo.ipc.*
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/10
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class BinderDemo : AppDslFragment() {

    var binderService: IBinderService? = null

    val binderServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            binderService = IBinderService.Stub.asInterface(service)

            _adapter.adapterItems.firstOrNull()?.itemViewHolder()?.apply {
                tv(R.id.tip_view2)?.text = span {
                    append("onServiceConnected:${nowTimeString()}") {
                        foregroundColor = randomColor()
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            binderService = null
        }
    }

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            dslItem(R.layout.binder_layout) {
                itemBindOverride = { itemHolder, itemPosition, adapterItem, payloads ->
                    itemHolder.tv(R.id.tip_view)?.text = span {
                        append("当前包名:${app().packageName}")
                        appendln()
                        append("PID:${Process.myPid()}")
                        appendln()
                        append("当前进程1:${getProcessName(app())}")
                        appendln()
                        append("当前进程2:${app().processName()}")
                    }

                    fun showTip2(string: String) {
                        itemHolder.tv(R.id.tip_view2)?.text = span {
                            append("${string}:${nowTimeString()}") {
                                foregroundColor = randomColor()
                            }
                        }
                    }

                    itemHolder.click(R.id.add_data_button) {
                        vmApp<IpcModel>().dataList.add(
                            IpcData(
                                nowTime(),
                                "来自${getProcessName(app())}进程的数据:${nowTimeString()}"
                            )
                        )
                        getData(itemHolder)
                    }
                    itemHolder.click(R.id.add_remote_data_button) {
                        if (binderService == null) {
                            showTip2("服务未连接")
                        } else {
                            binderService?.addData(
                                IpcData(
                                    nowTime(),
                                    "来自${getProcessName(app())}进程的数据:${nowTimeString()}"
                                )
                            )
                        }
                    }

                    itemHolder.click(R.id.get_data_button) {
                        getData(itemHolder)
                    }
                    itemHolder.click(R.id.get_remote_data_button) {
                        if (binderService == null) {
                            showTip2("服务未连接")
                        } else {
                            showData(itemHolder, binderService?.getData())
                        }
                    }

                    itemHolder.click(R.id.start_service_button) {
                        activity?.startService(Intent(app(), BinderService::class.java))
                    }
                    itemHolder.click(R.id.connect_service_button) {
                        if (binderService == null) {
                            activity?.bindService(
                                Intent(app(), BinderService::class.java),
                                binderServiceConnection,
                                Context.BIND_AUTO_CREATE
                            )
                            if (binderService != null) {
                                showTip2("服务连接成功")
                            }
                        } else {
                            showTip2("服务已连接")
                        }
                    }
                    itemHolder.click(R.id.start_process_button) {
                        dslAHelper {
                            start(IpcActivity::class)
                        }
                    }
                }
            }
        }
    }

    fun getData(holder: DslViewHolder) {
        showData(holder, vmApp<IpcModel>().dataList)
    }

    fun showData(holder: DslViewHolder, dataList: List<IpcData>?) {
        dataList?.apply {
            holder.tv(R.id.text_view)?.text = span {
                append("共:${size()}")
                appendln()
                append(connect("\n") {
                    it.text
                })
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.unbindService(binderServiceConnection)
    }

}