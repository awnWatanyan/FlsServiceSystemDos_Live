package com.aeon.flsservicesystem.user_manager


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DeviceData (
    @PrimaryKey var imei: String
)