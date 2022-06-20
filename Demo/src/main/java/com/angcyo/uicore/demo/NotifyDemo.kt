package com.angcyo.uicore.demo

import android.database.ContentObserver
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import com.angcyo.item.DslTextInfoItem
import com.angcyo.item.style.itemInfoText
import com.angcyo.library.ex.dpi
import com.angcyo.library.ex.isImageMimeType
import com.angcyo.library.ex.nowTimeString
import com.angcyo.library.ex.query
import com.angcyo.loader.DslLoader
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.AppImageItem
import com.angcyo.uicore.dslitem.AppNotifyItem
import com.angcyo.widget.span.span

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/13
 */
class NotifyDemo : AppDslFragment() {

    val contentObserver = object : ContentObserver(null) {
        override fun onChange(selfChange: Boolean) {
            _adapter.render {
                DslTextInfoItem()() {
                    itemInfoText = "onChange:$selfChange"
                    itemDarkText = nowTimeString()
                }
            }
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            val mediaBean = uri?.query()

            _adapter.render {
                DslTextInfoItem()() {
                    itemTopInsert = 10 * dpi
                    itemInfoText = span {
                        append("onChange:$selfChange\n$uri")
                        uri?.run {
                            appendln()
                            append(this.scheme)
                            append(" ")
                            append(this.authority)
                            append(" ")
                            append(this.encodedAuthority)
                            appendln()
                            mediaBean?.run {
                                append(this.toString())
                            }
                        }
                    }
                    itemDarkText = nowTimeString()
                }
            }

            mediaBean?.run {
                if (this.mimeType.isImageMimeType()) {
                    _adapter.render {
                        AppImageItem()() {
                            itemTopInsert = 10 * dpi
                            imageUrl = this@run.localPath
                        }
                    }
                }
            }
        }

        override fun deliverSelfNotifications(): Boolean {
            _adapter.render {
                DslTextInfoItem()() {
                    itemInfoText = "deliverSelfNotifications"
                    itemDarkText = nowTimeString()
                }
            }
            return super.deliverSelfNotifications()
        }
    }

    init {
        enableSoftInput = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fContext().contentResolver.run {
            //能够监听sd卡上文件的改变, 但是拿不到具体的文件uri
            registerContentObserver(
                MediaStore.Files.getContentUri(DslLoader.VOLUME_EXTERNAL),
                true,
                contentObserver
            )
            //能够监听图片的变化, 截屏, 拍照等
            registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                contentObserver
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        _adapter.render {
            AppNotifyItem()()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fContext().contentResolver.unregisterContentObserver(contentObserver)
    }
}