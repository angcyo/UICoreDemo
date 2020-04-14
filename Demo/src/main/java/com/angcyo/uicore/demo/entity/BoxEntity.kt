package com.angcyo.uicore.demo.entity

import com.angcyo.library.ex.nowTimeString
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 *
 * Email:angcyo@126.com
 * @author angcyo
 * @date 2020/04/14
 * Copyright (c) 2020 ShenZhen Wayto Ltd. All rights reserved.
 */
@Entity
data class BoxEntity(
    @Id
    var entityId: Long = 0L,

    var entityAddTime: String = nowTimeString()
)