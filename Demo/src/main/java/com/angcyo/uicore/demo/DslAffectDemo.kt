package com.angcyo.uicore.demo

import android.os.Bundle
import com.angcyo.core.component.DslAffect
import com.angcyo.drawable.dpi
import com.angcyo.item.DslBaseInfoItem
import com.angcyo.uicore.base.AppDslFragment

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/01/10
 */
class DslAffectDemo : AppDslFragment() {

    var dslAffect = DslAffect()

    override fun initBaseView(savedInstanceState: Bundle?) {
        super.initBaseView(savedInstanceState)

        rootControl().append(R.layout.layout_affect_demo)

        dslAffect.addAffect(DslAffect.AFFECT_EMPTY, R.layout.affect_empty)
        dslAffect.addAffect(DslAffect.AFFECT_LOADING, R.layout.affect_loading)
        dslAffect.install(contentControl()?.itemView)

        _vh.click(R.id.affect_content) {
            dslAffect.showAffect(DslAffect.AFFECT_CONTENT)
        }

        _vh.click(R.id.affect_empty) {
            dslAffect.showAffect(DslAffect.AFFECT_EMPTY)
        }

        _vh.click(R.id.affect_loading) {
            dslAffect.showAffect(DslAffect.AFFECT_LOADING)
        }

        renderDslAdapter {
            for (i in 0..100) {
                DslBaseInfoItem()() {
                    itemInfoText = "Text...$i"
                    itemTopInsert = 1 * dpi
                }
            }
        }
    }
}