package com.aeon.flsservicesystem.tracking

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index(value = ["created_at"], unique = true)]
)
data class TrackingData (
    @PrimaryKey(autoGenerate = true) var trackingId: Int=0,
    @ColumnInfo("staff_code") val staffCode: String? = null,
    @ColumnInfo("device_imei") val deviceImei: String?= null,
    @ColumnInfo("latitude") val latitude: Double,
    @ColumnInfo("longitude") val longitude: Double,
    @ColumnInfo("battery") val battery: Int,
    @ColumnInfo("provider") val provider: String?= null,
    @ColumnInfo("distance") val distance: String?= null,
    @ColumnInfo("note") val note: String?= null,
    @ColumnInfo("status") val status: String?= null,
    @ColumnInfo("speed") val speed: Float?= null,
    @ColumnInfo("created_at") val createdAt: Int,
    @ColumnInfo("update_at") var update_at: Int,
    @ColumnInfo("updateFlag") var updateFlag: Int
)