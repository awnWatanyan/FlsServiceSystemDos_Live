package com.aeon.flsservicesystem.user_manager

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey var uid: String,
    @ColumnInfo(name = "device_name") val deviceName: String?,
    @ColumnInfo(name = "token") val token: String?
)