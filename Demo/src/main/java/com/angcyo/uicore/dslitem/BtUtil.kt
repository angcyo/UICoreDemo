package com.angcyo.uicore.dslitem

import android.bluetooth.BluetoothClass
import com.angcyo.uicore.demo.R

/**
 * @author <a href="mailto:angcyo@126.com">angcyo</a>
 * @since 2022/03/12
 */
object BtUtil {

    const val PROFILE_HEADSET = 0
    const val PROFILE_A2DP = 1
    const val PROFILE_OPP = 2
    const val PROFILE_HID = 3
    const val PROFILE_PANU = 4
    const val PROFILE_NAP = 5
    const val PROFILE_A2DP_SINK = 6

    fun getDeviceTypeResource(bluetoothClass: BluetoothClass?): Int {
        return if (bluetoothClass == null) {
            R.drawable.icon_bluetooth
        } else when (bluetoothClass.majorDeviceClass) {
            BluetoothClass.Device.Major.COMPUTER -> R.drawable.icon_computer
            BluetoothClass.Device.Major.PHONE -> R.drawable.icon_phone
            BluetoothClass.Device.Major.PERIPHERAL -> R.drawable.icon_bluetooth
            BluetoothClass.Device.Major.IMAGING -> R.drawable.icon_bluetooth
            else -> when {
                doesClassMatch(bluetoothClass, PROFILE_HEADSET) -> R.drawable.icon_headset
                doesClassMatch(bluetoothClass, PROFILE_A2DP) -> R.drawable.icon_headphones
                else -> R.drawable.icon_bluetooth
            }
        }
    }

    fun doesClassMatch(bluetoothClass: BluetoothClass, profile: Int): Boolean {
        return if (profile == PROFILE_A2DP) {
            if (bluetoothClass.hasService(BluetoothClass.Service.RENDER)) {
                return true
            }
            when (bluetoothClass.deviceClass) {
                BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO, BluetoothClass.Device.AUDIO_VIDEO_HEADPHONES, BluetoothClass.Device.AUDIO_VIDEO_LOUDSPEAKER, BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO -> true
                else -> false
            }
        } else if (profile == PROFILE_A2DP_SINK) {
            if (bluetoothClass.hasService(BluetoothClass.Service.CAPTURE)) {
                return true
            }
            when (bluetoothClass.deviceClass) {
                BluetoothClass.Device.AUDIO_VIDEO_HIFI_AUDIO, BluetoothClass.Device.AUDIO_VIDEO_SET_TOP_BOX, BluetoothClass.Device.AUDIO_VIDEO_VCR -> true
                else -> false
            }
        } else if (profile == PROFILE_HEADSET) {
            // The render service class is required by the spec for HFP, so is a
            // pretty good signal
            if (bluetoothClass.hasService(BluetoothClass.Service.RENDER)) {
                return true
            }
            when (bluetoothClass.deviceClass) {
                BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE, BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET, BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO -> true
                else -> false
            }
        } else if (profile == PROFILE_OPP) {
            if (bluetoothClass.hasService(BluetoothClass.Service.OBJECT_TRANSFER)) {
                return true
            }
            when (bluetoothClass.deviceClass) {
                BluetoothClass.Device.COMPUTER_UNCATEGORIZED, BluetoothClass.Device.COMPUTER_DESKTOP, BluetoothClass.Device.COMPUTER_SERVER, BluetoothClass.Device.COMPUTER_LAPTOP, BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA, BluetoothClass.Device.COMPUTER_PALM_SIZE_PC_PDA, BluetoothClass.Device.COMPUTER_WEARABLE, BluetoothClass.Device.PHONE_UNCATEGORIZED, BluetoothClass.Device.PHONE_CELLULAR, BluetoothClass.Device.PHONE_CORDLESS, BluetoothClass.Device.PHONE_SMART, BluetoothClass.Device.PHONE_MODEM_OR_GATEWAY, BluetoothClass.Device.PHONE_ISDN -> true
                else -> false
            }
        } else if (profile == PROFILE_HID) {
            bluetoothClass.deviceClass and BluetoothClass.Device.Major.PERIPHERAL == BluetoothClass.Device.Major.PERIPHERAL
        } else if (profile == PROFILE_PANU || profile == PROFILE_NAP) {
            // No good way to distinguish between the two, based on class bits.
            if (bluetoothClass.hasService(BluetoothClass.Service.NETWORKING)) {
                true
            } else bluetoothClass.deviceClass and BluetoothClass.Device.Major.NETWORKING == BluetoothClass.Device.Major.NETWORKING
        } else {
            false
        }
    }
}