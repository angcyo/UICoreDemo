package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.widget.RadioGroup
import com.angcyo.component.hawkInstallAndRestore
import com.angcyo.dialog.*
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.dsladapter.ItemSelectorHelper
import com.angcyo.library.L
import com.angcyo.library.ex._color
import com.angcyo.library.ex._drawable
import com.angcyo.library.ex.getColor
import com.angcyo.library.toast
import com.angcyo.library.toastQQ
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.uicore.dslitem.tx
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.string
import com.angcyo.widget.progress.DslSeekBar
import com.angcyo.widget.span.span
import kotlin.random.Random.Default.nextInt

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/02/01
 */
class DialogDemo : AppDslFragment() {
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderDslAdapter {
            DslAdapterItem()() {
                itemLayoutId = R.layout.item_dialog_demo_layout

                itemBindOverride = this@DialogDemo::_bindDialogDemoItem
            }
            DslAdapterItem()() {
                itemLayoutId = R.layout.item_popup_demo_layout

                itemBindOverride = this@DialogDemo::_bindPopupDemoItem
            }
        }
    }

    var dialogType = DslDialogConfig.DIALOG_TYPE_APPCOMPAT

    fun _bindDialogDemoItem(
        holder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        holder.v<RadioGroup>(R.id.flow_style)?.setOnCheckedChangeListener { _, checkedId ->
            dialogType = when (checkedId) {
                R.id.style_dialog -> DslDialogConfig.DIALOG_TYPE_DIALOG
                R.id.style_alert -> DslDialogConfig.DIALOG_TYPE_ALERT_DIALOG
                R.id.style_sheet -> DslDialogConfig.DIALOG_TYPE_BOTTOM_SHEET_DIALOG
                else -> DslDialogConfig.DIALOG_TYPE_APPCOMPAT
            }
        }

        holder.click(R.id.normal_dialog) {
            fContext().normalDialog {
                _defaultConfig(holder, this)
                navigationBarColor = _color(R.color.lib_white)
            }
        }

        holder.click(R.id.normal_ios_dialog) {
            fContext().normalIosDialog {
                _defaultConfig(holder, this)
                navigationBarColor = _color(R.color.lib_white)
            }
        }

        holder.click(R.id.item_dialog) {
            fContext().itemsDialog {
                _initItemDialog(false)
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.item_dialog_icon) {
            fContext().itemsDialog {
                _initItemDialog(true)
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.menu_dialog) {
            fContext().itemsDialog {
                _initItemDialog(false, Gravity.LEFT or Gravity.CENTER_VERTICAL)
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.menu_ico_dialog) {
            fContext().itemsDialog {
                _initItemDialog(true, Gravity.LEFT or Gravity.CENTER_VERTICAL)
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.menu_ico2_dialog) {
            fContext().itemsDialog {
                addDialogItem {
                    itemText = tx()
                    itemLeftDrawable = _drawable(R.drawable.lib_ic_info)
                }
                addDialogItem {
                    itemText = tx()
                    itemLeftDrawable = _drawable(R.drawable.lib_ic_error)
                }
                _initItemDialog(true, Gravity.RIGHT or Gravity.CENTER_VERTICAL)
                _defaultConfig(holder, this)
            }
        }
//
//        holder.click(R.id.wheel_dialog) {
//            wheelDialog {
//                dialogTitle = "今晚谁陪朕?"
//
//                wheelItems = mutableListOf("Item1", "Item2", "Item3")
//
//                wheelCyclic = false
//
//                defaultIndex = 1
//
//                onWheelItemSelector = { _, _, item ->
//                    toast.show(item as CharSequence)
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }

        holder.click(R.id.menu_choice_dialog) {
            fContext().itemsDialog {
                dialogSelectorModel = ItemSelectorHelper.MODEL_SINGLE
                _initItemDialog(true, Gravity.LEFT or Gravity.CENTER_VERTICAL)
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.menu_multi_dialog) {
            fContext().itemsDialog {
                dialogSelectorModel = ItemSelectorHelper.MODEL_MULTI
                _initItemDialog(false, Gravity.CENTER)
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.input_single_dialog) {
            fContext().inputDialog {
                showSoftInput = true

                hintInputString = "客官输入点东西吧..."

                onInputResult = { dialog, inputText ->
                    toast(inputText)
                    false
                }

                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.input_multi_dialog) {
            fContext().multiInputDialog {
                dialogTitle = "请输入"

                onInputResult = { dialog, inputText ->
                    toast(inputText)
                    false
                }

                _defaultConfig(holder, this)
            }
        }
//
//        holder.click(R.id.grid_dialog) {
//            gridDialog {
//                appendItem {
//                    gridItemIcon = R.drawable.ic_building_collect
//                    gridItemBgDrawable =
//                        RDrawable.get(requireContext()).circle("#3796F6".toColor()).get()
//                    gridItemText = "走 访"
//                }
//                appendItem {
//                    gridItemIcon = R.drawable.ic_building_collect
//                    gridItemBgDrawable =
//                        RDrawable.get(requireContext()).circle("#00BA8A".toColor()).get()
//                    gridItemText = "添加人口"
//                }
//                appendItem {
//                    gridItemIcon = R.drawable.ic_building_collect
//                    gridItemBgDrawable =
//                        RDrawable.get(requireContext()).circle("#F5BA00".toColor()).get()
//                    gridItemText = "注销人口"
//                }
//                appendItem {
//                    gridItemIcon = R.drawable.ic_building_collect
//                    gridItemBgDrawable =
//                        RDrawable.get(requireContext()).circle("#00BA8A".toColor()).get()
//                    gridItemText = "房屋信息"
//                }
//                appendItem {
//                    gridItemIcon = R.drawable.ic_building_collect
//                    gridItemBgDrawable =
//                        RDrawable.get(requireContext()).circle("#3796F6".toColor()).get()
//                    gridItemText = "房屋相册"
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
//        holder.click(R.id.all_dialog) {
//            dateDialog {
//                dialogTitle = "日期时间"
//                type = booleanArrayOf(true, true, true, true, true, true)
//                onDateSelectListener = { dialog, date ->
//                    toast.show(WheelTime.dateFormat.format(date))
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
//        holder.click(R.id.date_dialog) {
//            dateDialog {
//                dialogTitle = "出生日期"
//                onDateSelectListener = { dialog, date ->
//                    toast.show(WheelTime.dateFormat.format(date))
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
//        holder.click(R.id.time_dialog) {
//            dateDialog {
//                dialogTitle = "时间选择"
//                type = booleanArrayOf(false, false, false, true, true, true)
//                onDateSelectListener = { dialog, date ->
//                    toast.show(WheelTime.dateFormat.format(date))
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
//        var index = 0
//        holder.click(R.id.option_dialog_single) {
//            optionDialog {
//                dialogTitle = "多级选项选择Single"
//                singleOption = true
//
//                if (index++ > 1) {
//                    optionList = mutableListOf("1级e")
//                }
//
//                onLoadOptionList = { options, level, callback, _ ->
//                    callback(mutableListOf("1级a", "1级b", "1级e"))
//                }
//                onCheckOptionEnd = { options, level ->
//                    true
//                }
//                onOptionResult = { _, optionList ->
//                    toast(RUtils.connect(optionList))
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//        holder.click(R.id.option_dialog) {
//            optionDialog {
//                dialogTitle = "多级选项选择"
//                onLoadOptionList = { options, level, callback, _ ->
//                    callback(loadOptionList(level))
//                }
//                onCheckOptionEnd = { options, level ->
//                    options.size == 4
//                }
//                onOptionResult = { _, optionList ->
//                    toast(RUtils.connect(optionList))
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//        holder.click(R.id.option_dialog2) {
//            optionDialog {
//                dialogTitle = "多级选项选择(半默认)"
//                optionList = mutableListOf("1级a", "2级b")
//                onLoadOptionList = { options, level, callback, _ ->
//                    callback(loadOptionList(level))
//                }
//                onCheckOptionEnd = { options, level ->
//                    options.size == 4
//                }
//                onOptionResult = { _, optionList ->
//                    toast(RUtils.connect(optionList))
//                    false
//                }
//
//                _defaultConfig(holder, this)
//
//                anySelector = true
//            }
//        }
//        holder.click(R.id.option_dialog3) {
//            optionDialog {
//                dialogTitle = "多级选项选择(全默认)"
//                optionList = mutableListOf("1级a", "2级b", "3级c", "4级d")
//                onLoadOptionList = { options, level, callback, _ ->
//                    callback(loadOptionList(level))
//                }
//                onCheckOptionEnd = { options, level ->
//                    options.size == 4
//                }
//                onOptionResult = { _, optionList ->
//                    toast(RUtils.connect(optionList))
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
//        //日历接收
//        val calendarResult = { _: Dialog, calendarList: MutableList<Calendar> ->
//            toast(buildString {
//                append("始:")
//                CalendarDialogConfig.ymd(this, calendarList[0])
//                appendln()
//                append("止:")
//                CalendarDialogConfig.ymd(this, calendarList[1])
//            })
//            false
//        }
//        holder.click(R.id.calendar_dialog) {
//            calendarDialog {
//                dialogTitle = "日历选择"
//                onCalendarResult = calendarResult
//                _defaultConfig(holder, this)
//            }
//        }
//
//        holder.click(R.id.calendar_dialog1) {
//            calendarDialog {
//                dialogTitle = "日历选择对话框(带默认1)"
//                setCalendarRange(2018, 2020)
//
//                calendarList = mutableListOf(RCalendarView.today())
//
//                onCalendarResult = calendarResult
//                _defaultConfig(holder, this)
//            }
//        }
//
//        holder.click(R.id.calendar_dialog2) {
//            calendarDialog {
//                dialogTitle = "日历选择对话框(带默认2)"
//
//                //设置日历选择范围
//                setCalendarRange(2018, 2020, 4, 8)
//
//                //设置日历默认选择范围
//                calendarList = mutableListOf(Calendar(2019, 2, 1), Calendar(2019, 5, 1))
//
//                onCalendarResult = calendarResult
//                _defaultConfig(holder, this)
//            }
//        }

        holder.hawkInstallAndRestore("dialog_")
    }

    fun BaseRecyclerDialogConfig._initItemDialog(
        ico: Boolean = false,
        gravity: Int = Gravity.CENTER
    ) {
        for (i in 0..nextInt(2, 28)) {
            addDialogItem {
                itemTextGravity = gravity
                itemText = span {
                    if (ico) {
                        drawable {
                            backgroundDrawable = when (i % 4) {
                                1 -> _drawable(R.drawable.lib_ic_info)
                                2 -> _drawable(R.drawable.lib_ic_error)
                                3 -> _drawable(R.drawable.lib_ic_waring)
                                else -> _drawable(R.drawable.lib_ic_succeed)
                            }
                        }
                    }
                    append(" ${tx()} $i")
                }
            }
        }
    }

    fun _defaultConfig(viewHolder: DslViewHolder, config: DslDialogConfig) {
        config.apply {
            dialogTitle = viewHolder.tv(R.id.title_edit)?.string()
            dialogMessage = viewHolder.tv(R.id.message_edit)?.string()

            cancelable = viewHolder.isChecked(R.id.close_cb)
            canceledOnTouchOutside = viewHolder.isChecked(R.id.cancel_outside_cb)

            if (viewHolder.isChecked(R.id.amount_cb)) {
                config.amount =
                    (viewHolder.v<DslSeekBar>(R.id.amount_progress_bar)?.progressValue ?: 0) / 100f
            }

            dialogType = this@DialogDemo.dialogType

            if (viewHolder.isChecked(R.id.title_cb)) {
                dialogTitle = null
            }

            if (viewHolder.isChecked(R.id.message_cb)) {
                dialogMessage = null
            }

            if (viewHolder.isChecked(R.id.cancel_cb)) {
                if (this is BaseRecyclerDialogConfig) {
                    dialogBottomCancelItem = null
                }
            }

            if (this is BaseRecyclerDialogConfig) {
                onDialogResult = { _, indexList ->
                    toastQQ("返回:$indexList", R.drawable.lib_ic_info)
                }
            }
        }
    }

    fun _bindPopupDemoItem(
        holder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem,
        payloads: List<Any>
    ) {
        val titleBarLayout = titleControl()!!.itemView

        //popup
        holder.click(R.id.bottom) {
            fContext().popupWindow(it) {
                layoutId = R.layout.item_dialog_demo_layout
                background = ColorDrawable(Color.RED)
                exactlyHeight = true
                onDismiss = {
                    L.i("...dismiss...")
                    toast("...dismiss...")
                    false
                }
                xoff = 200
                yoff = 200
                _defaultConfig(holder, this)
            }
        }
        holder.click(R.id.normal_popup_style) {
            fContext().popupWindow(it) {
                layoutId = R.layout.item_dialog_demo_layout
                background = ColorDrawable(Color.RED)
                onDismiss = {
                    L.i("...dismiss...")
                    toast("...dismiss...")
                    false
                }
                popupStyleAttr = R.style.LibPopupWindowStyle
                animationStyle = -1
                _defaultConfig(holder, this)
            }
        }
        holder.click(R.id.bottom_popup) {
            fContext().popupWindow(it) {
                layoutId = R.layout.item_dialog_demo_layout
                background = ColorDrawable(Color.RED)
                exactlyHeight = true
                onDismiss = {
                    L.i("...dismiss...")
                    toast("...dismiss...")
                    false
                }
                xoff = 200
                yoff = 200
                _defaultConfig(holder, this)
            }
        }
        holder.click(R.id.normal_popup) {
            fContext().popupWindow(it) {
                layoutId = R.layout.item_dialog_demo_layout
                background = ColorDrawable(Color.RED)
                animationStyle = -1
                onDismiss = {
                    L.i("...dismiss...")
                    toast("...dismiss...")
                    false
                }
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.width_full_popup) {
            fContext().popupWindow(it) {
                layoutId = R.layout.item_dialog_demo_layout
                background = ColorDrawable(Color.RED)
                width = -1
                onDismiss = {
                    L.i("...dismiss...")
                    toast("...dismiss...")
                    false
                }
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.full_popup) {
            fContext().popupWindow(it) {
                layoutId = R.layout.item_dialog_demo_layout
                background = ColorDrawable(Color.RED)
                width = -1
                height = -1
                onDismiss = {
                    L.i("...dismiss...")
                    toast("...dismiss...")
                    false
                }
                onInitLayout = { popupWindow, popupViewHolder ->

                }
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.full_popup2) {
            fContext().popupWindow(it) {
                layoutId = R.layout.item_dialog_demo_layout
                width = -1
                background = ColorDrawable(getColor(R.color.transparent40))
                exactlyHeight = true
                onDismiss = {
                    L.i("...dismiss...")
                    toast("...dismiss...")
                    false
                }
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.full_popup_title) {
            fContext().popupWindow(titleBarLayout) {
                layoutId = R.layout.item_dialog_demo_layout
                width = -1
                background = ColorDrawable(getColor(R.color.transparent40))
                exactlyHeight = true
                onDismiss = {
                    L.i("...dismiss...")
                    toast("...dismiss...")
                    false
                }

                onInitLayout = { popupWindow, popupViewHolder ->
                    popupViewHolder.view(R.id.flow_style)?.setBackgroundColor(Color.RED)
                    popupViewHolder.view(R.id.flow_1)?.setBackgroundColor(Color.RED)
                }
                _defaultConfig(holder, this)
            }
        }

        //FullPopupConfig
        holder.click(R.id.full_popup_config) {
            fContext().fullPopupWindow(titleBarLayout) {
                layoutId = R.layout.item_popup_demo_layout
                _defaultConfig(holder, this)
            }
        }
    }

    fun _defaultConfig(viewHolder: DslViewHolder, config: PopupConfig) {
        config.apply {
            showWithActivity = viewHolder.cb(R.id.with_activity_cb)?.isChecked ?: false
            outsideTouchable = viewHolder.cb(R.id.cancel_outside_cb)?.isChecked ?: false

            if (_vh.cb(R.id.amount_cb)?.isChecked == true) {
                config.amount =
                    (_vh.v<DslSeekBar>(R.id.amount_progress_bar)?.progressValue ?: 0) / 100f
            }
        }
    }
}

//fun DslDialog.show(dialogConfig: BaseDialogConfig): Dialog {
//    configDslDialog(dialogConfig)
//    return when (dialogConfig.dialogType) {
//        BaseDialogConfig.DIALOG_TYPE_ALERT_DIALOG -> showAlertDialog()
//        BaseDialogConfig.DIALOG_TYPE_BOTTOM_SHEET_DIALOG -> showAndConfigDialog(
//            BottomSheetDialog(
//                context
//            )
//        )
//        else -> showCompatDialog()
//    }
//}
