package com.angcyo.uicore.demo

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.angcyo.base.dslAHelper
import com.angcyo.base.dslFHelper
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.core.component.fileSelector
import com.angcyo.dsladapter.renderItem
import com.angcyo.tbs.open
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.base.string

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/03/01
 */
class TbsWebDemo : AppDslFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        renderDslAdapter {
            renderItem {
                itemLayoutId = R.layout.demo_tbs_web

                itemBindOverride = { itemHolder, _, _, _ ->
                    itemHolder.click(R.id.open_url) {
                        dslAHelper {
                            open {
                                uri = Uri.parse(itemHolder.tv(R.id.edit_text).string())
                            }
                        }
                    }

                    itemHolder.click(R.id.open_url2) {
                        dslAHelper {
                            open {
                                uri = Uri.parse(itemHolder.tv(R.id.edit_text2).string())
                            }
                        }
                    }

                    itemHolder.click(R.id.open_url3) {
                        dslAHelper {
                            open {
                                uri = Uri.parse(itemHolder.tv(R.id.edit_text3).string())
                            }
                        }
                    }

                    itemHolder.click(R.id.local_file_chooser) {
                        dslAHelper {
                            open {
                                uri = Uri.parse("file:///android_asset/webpage/fileChooser.html")
                            }
                        }
                    }

                    itemHolder.click(R.id.local_file_play) {
                        dslAHelper {
                            open {
                                uri = Uri.parse("file:///android_asset/webpage/fullscreenVideo.html")
                            }
                        }
                    }

                    itemHolder.click(R.id.open_file) {
                        dslFHelper {
                            fileSelector {

                            }
                        }
                    }

                    itemHolder.hawkInstallAndRestore("tbs_")
                }
            }
        }
    }
}