package com.angcyo.uicore.demo

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.RadioGroup
import com.angcyo.dialog.*
import com.angcyo.dsladapter.DslAdapterItem
import com.angcyo.library.L
import com.angcyo.library.ex.getColor
import com.angcyo.library.toast
import com.angcyo.uicore.base.AppDslFragment
import com.angcyo.widget.DslViewHolder
import com.angcyo.widget.base.string

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

                onItemBindOverride = this@DialogDemo::_bindDialogDemoItem
            }
            DslAdapterItem()() {
                itemLayoutId = R.layout.item_popup_demo_layout

                onItemBindOverride = this@DialogDemo::_bindPopupDemoItem
            }
        }
    }

    var dialogType = DslDialogConfig.DIALOG_TYPE_APPCOMPAT

    fun _bindDialogDemoItem(
        holder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
    ) {
        holder.v<RadioGroup>(R.id.flow_style)?.setOnCheckedChangeListener { _, checkedId ->
            dialogType = when (checkedId) {
                R.id.style_alert -> DslDialogConfig.DIALOG_TYPE_ALERT_DIALOG
                R.id.style_sheet -> DslDialogConfig.DIALOG_TYPE_BOTTOM_SHEET_DIALOG
                else -> DslDialogConfig.DIALOG_TYPE_APPCOMPAT
            }
        }

        holder.click(R.id.normal_dialog) {
            fContext().normalDialog {
                _defaultConfig(holder, this)
            }
        }

        holder.click(R.id.normal_ios_dialog) {
            fContext().normalIosDialog {
                _defaultConfig(holder, this)
            }
        }

//        holder.click(R.id.item_dialog) {
//            itemsDialog {
//                dialogTitle = "标题标题标题标题标题"
//
//                items = mutableListOf(
//                    "Item1",
//                    "Item2",
//                    "Item3",
//                    "Item4",
//                    "Item5",
//                    "Item6",
//                    "Item7",
//                    "Item8",
//                    "Item9",
//                    "Item10"
//                )
//
//                onItemClick = { _, _, item ->
//                    toast.show(item as CharSequence)
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
//        holder.click(R.id.item_dialog_full) {
//            itemsDialog {
//                dialogWidth = -1
//                //指定dialogHeight 可以解决状态栏变黑, 但是~高度的计算会受到影响...
//                dialogHeight = RUtils.getScreenHeight(activity)
//                dialogBgDrawable = ColorDrawable(Color.TRANSPARENT)
//                dialogTitle = "标题标题标题标题标题(全屏)"
//                windowFlags = intArrayOf(
//                    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
//                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
//                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
//                )
//
//                items = mutableListOf(
//                    "Item1",
//                    "Item2",
//                    "Item3",
//                    "Item4",
//                    "Item5",
//                    "Item6",
//                    "Item7",
//                    "Item8",
//                    "Item9",
//                    "Item10"
//                )
//
//                onItemClick = { _, _, item ->
//                    toast.show(item as CharSequence)
//                    false
//                }
//
//                _defaultConfig(holder, this)
//
//                dialogInit = { dialog, dialogViewHolder ->
//                    ActivityHelper.enableLayoutFullScreen(dialog.window, true)
//
//                    dialogViewHolder.itemView.apply {
//                        fitsSystemWindows = false
//                        setBackgroundColor(Color.GREEN)
//                        setPadding(0, 0, 0, 0)
//                        layoutParams = WindowManager.LayoutParams(-1, -2)
//                    }
//                }
//            }
//        }
//
//        holder.click(R.id.menu_dialog) {
//            menuDialog {
//                dialogTitle = "你要干啥?"
//
//                items = mutableListOf("Item1", "Item2", "Item3")
//
//                onItemClick = { _, _, item ->
//                    toast.show(item as CharSequence)
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
//        holder.click(R.id.menu_ico_dialog) {
//            menuDialog {
//                //dialogTitle = "你要干啥?"
//
//                items = mutableListOf("Item1", "Item2", "Item3")
//                itemIcons =
//                    mutableListOf(R.drawable.ic_delete_photo, R.drawable.ic_delete_photo)
//
//                onItemClick = { _, _, item ->
//                    toast.show(item as CharSequence)
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
//        holder.click(R.id.menu_ico2_dialog) {
//            menuDialog {
//                //dialogTitle = "你要干啥?"
//
//                items = mutableListOf("Item1", "Item2", "Item3")
//                itemIcons =
//                    mutableListOf(R.drawable.ic_delete_photo, R.drawable.ic_delete_photo)
//                itemTextGravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
//
//                onItemClick = { _, _, item ->
//                    toast.show(item as CharSequence)
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
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
//
//        holder.click(R.id.menu_choice_dialog) {
//            singleChoiceDialog {
//                dialogTitle = "小分队出发"
//
//                items = mutableListOf("Item1", "Item2", "Item3")
//
//                onChoiceItemList = { _, indexList ->
//                    toast.show(indexList.toString())
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
//        holder.click(R.id.menu_multi_dialog) {
//            fContext().multiChoiceDialog {
//                dialogTitle = "弟弟"
//
//                items = mutableListOf("Item1", "Item2", "Item3")
//
//                defaultSelectorIndexList = mutableListOf(0, 2)
//
//                onChoiceItemList = { _, indexList ->
//                    toast.show(indexList.toString())
//                    false
//                }
//
//                _defaultConfig(holder, this)
//            }
//        }
//
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
    }

    fun _defaultConfig(viewHolder: DslViewHolder, config: DslDialogConfig) {
        config.apply {
            dialogTitle = viewHolder.tv(R.id.title_edit)?.string()
            dialogMessage = viewHolder.tv(R.id.message_edit)?.string()

            cancelable = viewHolder.cb(R.id.cancel_cb)?.isChecked ?: true
            canceledOnTouchOutside = viewHolder.cb(R.id.cancel_outside_cb)?.isChecked ?: true

            if (this is BaseDialogConfig) {
                dialogType = this@DialogDemo.dialogType
            }
        }
    }

    fun _bindPopupDemoItem(
        holder: DslViewHolder,
        itemPosition: Int,
        adapterItem: DslAdapterItem
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
