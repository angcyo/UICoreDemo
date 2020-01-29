package com.angcyo.uicore.demo

import android.os.Bundle
import androidx.lifecycle.MutableLiveData
import com.angcyo.base.back
import com.angcyo.base.dslFHelper
import com.angcyo.coroutine.sleep
import com.angcyo.dsladapter.dslItem
import com.angcyo.item.DslTextInfoItem
import com.angcyo.library.L
import com.angcyo.library.ex.fullTime
import com.angcyo.library.ex.nowTime
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.viewmodel.ScopeViewModel
import com.angcyo.viewmodel.vma
import com.angcyo.widget.base.setInputText

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/16
 */

class ViewModelDemo : AppDslFragment() {
    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            dslItem(R.layout.item_view_model_layout) {
                itemTag = "view_model"
                onItemBindOverride = { itemHolder, _, _ ->
                    itemHolder.et(R.id.edit_text)?.setInputText(itemData?.toString())

                    itemHolder.click(R.id.button) {
                        dslFHelper {
                            show(ViewModelLoadDemo::class.java)
                        }
                    }
                }
            }

            DslTextInfoItem()() {
                itemInfoText = "同一个[FragmentActivity]内共享[ViewModel]"
            }
            DslTextInfoItem()() {
                itemInfoText = "调整到其他[Fragment]依旧可以监听[observe]属性改变."
            }
        }
    }

    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)

        val demoViewModel: DemoViewModel = vma()

        demoViewModel.demoData.observe {
            L.i("数据监听:$it")
            _adapter["view_model"]?.run {
                itemData = "$itemData\n\n$it"
                updateAdapterItem()
            }
        }

        demoViewModel.demoData.observe {
            L.i("数据监听2:$it")
        }
    }
}

class ViewModelLoadDemo : AppDslFragment() {
    override fun onFragmentShow(bundle: Bundle?) {
        super.onFragmentShow(bundle)

        val demoViewModel: DemoViewModel = vma()

        renderDslAdapter {
            DslTextInfoItem()() {
                itemInfoText = "准备模拟请求..."
            }
        }

        _vh.postDelay(1000) {
            renderDslAdapter {
                if (demoViewModel.demoData.value == null) {
                    DslTextInfoItem()() {
                        itemInfoText = "请求中..."
                    }
                } else {
                    DslTextInfoItem()() {
                        itemInfoText = "缓存:${demoViewModel.demoData.value}"
                    }
                }
            }

            demoViewModel.loadData()
        }

        demoViewModel.demoData.observe {
            L.i("数据回调:$it")

            renderDslAdapter {
                DslTextInfoItem()() {
                    itemInfoText = "请求结束!"
                }

                DslTextInfoItem()() {
                    itemInfoText = "->$it"
                }
            }

            _vh.postDelay(3_000) {
                back()
            }
        }
    }
}

class DemoViewModel : ScopeViewModel() {
    val demoData: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun loadData() {
        launch {
            L.i("模拟请求网络...")
            sleep(1000)
            L.i("请求结束.")

            demoData.value = nowTime().fullTime()
        }
    }
}