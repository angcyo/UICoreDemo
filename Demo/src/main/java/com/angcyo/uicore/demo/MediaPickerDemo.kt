package com.angcyo.uicore.demo

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.angcyo.dsladapter.DslAdapter
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.getAllItemData
import com.angcyo.dsladapter.loadSingleData
import com.angcyo.library.component.DslIntent
import com.angcyo.library.component.dslIntentShare
import com.angcyo.library.ex._color
import com.angcyo.library.ex.dpi
import com.angcyo.library.utils.resultString
import com.angcyo.library.model.LoaderMedia
import com.angcyo.pager.dslPager
import com.angcyo.picker.DslPicker
import com.angcyo.picker.dslPicker
import com.angcyo.picker.dslitem.DslPickerImageItem
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppMediaPickerItem
import com.angcyo.widget.base.setHeight
import com.angcyo.widget.recycler.ScrollHelper
import com.angcyo.widget.recycler.resetLayoutManager
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/29
 */

class MediaPickerDemo : AppDslFragment() {

    override fun onInitDslLayout(recyclerView: RecyclerView, dslAdapter: DslAdapter) {
        super.onInitDslLayout(recyclerView, dslAdapter)
        recyclerView.resetLayoutManager("GV4")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderDslAdapter {
            changeHeaderItems {
                it.add(AppMediaPickerItem().apply {
                    itemSpanCount = -1
                    onStartPicker = { loaderConfig ->
                        dataItems.forEach { item ->
                            if (item is DslPickerImageItem) {
                                item.loaderMedia?.apply {
                                    loaderConfig.selectorMediaList.add(this)
                                }
                            }
                        }
                        dslPicker(loaderConfig)
                    }
                    itemBindOverride = { itemHolder, _, _, _ ->
                        //拍照
                        itemHolder.click(R.id.do_take_photo) {
                            DslPicker.takePhoto(requireActivity()) {
                                //渲染结果
                                renderDslAdapter {
                                    loadSingleData(
                                        mutableListOf(
                                            LoaderMedia(
                                                localUri = it,
                                                mimeType = "image/jpeg"
                                            )
                                        ),
                                        initOrCreateDslItem = this@MediaPickerDemo::initOrCreateItem
                                    )
                                }
                            }
                        }
                        //录像
                        itemHolder.click(R.id.do_take_video) {
                            DslPicker.takeVideo(requireActivity()) {
                                renderDslAdapter {
                                    loadSingleData(
                                        mutableListOf(
                                            LoaderMedia(
                                                localUri = it,
                                                mimeType = "video/mp4"
                                            )
                                        ),
                                        initOrCreateDslItem = this@MediaPickerDemo::initOrCreateItem
                                    )
                                }
                            }
                        }
                        //分享文本
                        itemHolder.click(R.id.do_share) {
                            dslIntentShare(fContext()) {
                                shareTitle = "shareTitle...."
                                shareText = "shareText...."
                            }
                            DslIntent.openUrl(fContext(), "https://www.angcyo.com")
                        }
                        //邮件分享
                        itemHolder.click(R.id.do_share_email) {
                            dslIntentShare(fContext()) {
                                shareTitle = "shareTitle...."
                                shareText = "shareText...."
                                shareEmail = "angcyo@126.com"
                                shareEmailSubject = "邮件主题"
                            }
                        }
                    }
                })
                it.add(DslAdapterItem().apply {
                    itemSpanCount = -1
                    itemTag = "result"
                    itemLayoutId = R.layout.lib_text_layout
                    itemBindOverride = { itemHolder, _, _, _ ->
                        itemHolder.tv(R.id.lib_text_view)?.text = itemData as? CharSequence
                    }
                })
            }

            //占位item
            changeFooterItems {
                it.add(DslAdapterItem().apply {
                    itemSpanCount = -1
                    itemLayoutId = R.layout.lib_empty_item
                    itemBindOverride = { itemHolder, _, _, _ ->
                        itemHolder.itemView.setHeight(100 * dpi)
                    }
                })
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        DslPicker.onActivityResult(requestCode, resultCode, data)?.apply {
            renderDslAdapter {
                loadSingleData(
                    this@apply,
                    initOrCreateDslItem = this@MediaPickerDemo::initOrCreateItem
                )

                onDispatchUpdatesAfterOnce = {
                    _recycler.scrollHelper.lockPositionByDraw {
                        scrollType = ScrollHelper.SCROLL_TYPE_BOTTOM
                        lockDuration = 360
                    }
                }
            }
        }

        _adapter["result"]?.apply {
            itemData = span {
                append("requestCode:")
                append("$requestCode") {
                    foregroundColor = _color(R.color.colorPrimaryDark)
                }
                appendln()

                append("resultCode:")
                append("$resultCode ${resultCode.resultString()}") {
                    foregroundColor = _color(R.color.colorPrimaryDark)
                }
                appendln()

                append("data:")
                append("$data") {
                    foregroundColor = _color(R.color.colorPrimaryDark)
                }
                appendln()


                DslPicker.onActivityResult(requestCode, resultCode, data)?.run {
                    append(this.toString())
                }
            }

            updateAdapterItem()
        }
    }

    fun initOrCreateItem(oldItem: DslPickerImageItem?, data: Any): DslPickerImageItem {
        return oldItem ?: DslPickerImageItem().apply {
            checkModel = false
            itemClick = {
                dslPager {
                    fromRecyclerView = _recycler
                    onPositionConvert = {
                        it + _adapter.headerItems.size
                    }
                    loaderMediaList.addAll(_adapter.getAllItemData())
                    startPosition = loaderMediaList.indexOf(loaderMedia)
                }
            }
        }
    }
}