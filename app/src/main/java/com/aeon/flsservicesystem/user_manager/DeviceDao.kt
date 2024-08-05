package com.aeon.flsservicesystem.user_manager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


@Dao
interface DeviceDao {
    @Query("SELECT * FROM deviceData")
    fun getAll(): List<DeviceData>

    @Query("SELECT * FROM deviceData WHERE imei IN (:device)")
    fun loadAllByIds(device: String): List<DeviceData>

    @Insert
    fun insertAll(vararg users: DeviceData)

    @Delete
    fun delete(device: DeviceData)

    @Query("DELETE FROM DeviceData")
    fun deleteAll()
}