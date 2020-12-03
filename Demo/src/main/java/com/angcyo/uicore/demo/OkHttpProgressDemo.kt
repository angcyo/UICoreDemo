package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.dsladapter.singleItem
import com.angcyo.glide.loadImage
import com.angcyo.http.progress.downloadProgress
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget._img
import com.angcyo.widget.base.anim
import com.angcyo.widget.progress.HaloProgressBar
import com.bumptech.glide.load.engine.DiskCacheStrategy

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/12/03
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
class OkHttpProgressDemo : AppDslFragment() {

    val urlList = listOf(
        "https://ae01.alicdn.com/kf/U76a18e0d315e407a8daf3d91de033e31i.jpg",
        "https://ae01.alicdn.com/kf/U97dda0f4080140bb99911d798328f56dJ.jpg",
        "https://ae01.alicdn.com/kf/Ue16c54cac6574a06a0c1afdad979b007W.jpg",
        "https://ae01.alicdn.com/kf/Uec00959acd9c4d0aa900d5fb8ea481931.jpg",
        "https://ae01.alicdn.com/kf/U9a21a2f4b83c4030b87c840bc07105e5A.jpg",
        "https://ae01.alicdn.com/kf/U8353ae5c8aaf48c8b810fdac9c6d2e6dp.jpg",
        "https://ae01.alicdn.com/kf/U8f29046315a345b488a91f19e0691d7bx.jpg",
        "https://ae01.alicdn.com/kf/U16c8b999987a4671bf872d061cb63100y.jpg",
        "https://ae01.alicdn.com/kf/U263dd43ef1254f73b0dddac9562dc9bcB.jpg",
        "https://ae01.alicdn.com/kf/U3061240eed18487ab39fd4a36712ddc3y.jpg",
        "https://ae01.alicdn.com/kf/U9b128e54dc8e4c80a6ceb94b441cb55aO.jpg",
        "https://ae01.alicdn.com/kf/Ua867fc827ce646efa6718e273d543a783.jpg",
        "https://ae01.alicdn.com/kf/Ua5e3b85ad5584245b06b6fbb785df2cdS.jpg",
        "https://ae01.alicdn.com/kf/U430df684f8df4f4198035fda7c354adcd.jpg",
        "https://ae01.alicdn.com/kf/Uf2f395c0501b425bbdd27be44bd51e8eM.jpg",
    )

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)
        renderDslAdapter {
            singleItem(R.layout.demo_okhttp_progress_layout) { itemHolder, itemPosition, adapterItem, payloads ->
                itemHolder.v<HaloProgressBar>(R.id.halo_progress_bar)?.apply {
                    startHaloAnimator()

                    //直接完成
                    itemHolder.throttleClick(R.id.finish_button) {
                        startHaloFinishAnimator()
                    }

                    //进度动画
                    itemHolder.throttleClick(R.id.progress_button) {
                        anim(0f, 1f) {
                            onAnimatorConfig = {
                                it.duration = 5_000
                            }
                            onAnimatorUpdateValue = { value, fraction ->
                                progress = (fraction * 100).toInt()
                            }
                        }
                        startHaloAnimator()
                    }

                    //加载url
                    itemHolder.throttleClick(R.id.load_button) {
                        val url = urlList.random()
                        url.downloadProgress { progressInfo, exception ->
                            progress = progressInfo?.percent ?: progress
                            startHaloAnimator()
                        }
                        itemHolder._img(R.id.lib_image_view)?.apply {
                            loadImage(url) {
                                reset()
                                skipMemoryCache = true
                                diskCacheStrategy = DiskCacheStrategy.NONE
                            }
                        }
                    }

                }
            }
        }
    }
}