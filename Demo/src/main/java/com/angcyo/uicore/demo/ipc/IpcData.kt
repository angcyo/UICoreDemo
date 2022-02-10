package com.angcyo.uicore.demo.ipc

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2022/02/10
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
@Parcelize
data class IpcData(
    val time: Long,
    val text: String
) : Parcelable