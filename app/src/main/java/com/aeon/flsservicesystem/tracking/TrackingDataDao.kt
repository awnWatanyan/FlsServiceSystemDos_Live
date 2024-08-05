package com.aeon.flsservicesystem.tracking

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TrackingDataDao {
    @Query("SELECT * FROM trackingData")
    fun getAll(): List<TrackingData>

    @Query("SELECT * FROM trackingData WHERE trackingId IN (:trackingId)")
    fun loadAllByIds(trackingId: String): List<TrackingData>

    @Query("SELECT * FROM trackingData WHERE updateFlag = 1")
    fun loadAllNotSend(): List<TrackingData>

    @Query("SELECT * FROM trackingData WHERE created_at = :createAt")
    fun checkDupCreateAt(createAt : Int): TrackingData?

    @Insert
    fun insertAll(vararg tracking: TrackingData)

    @Update
    fun updateSend(vararg tracking: TrackingData)

    @Query("DELETE FROM trackingData WHERE update_at < :cutoffTime")
    suspend fun deleteOldData(cutoffTime: Int): Int

    @Query("DELETE FROM trackingData WHERE updateFlag = 0")
    fun deleteSendData()

    @Delete
    fun delete(tracking: TrackingData)

    @Query("DELETE FROM trackingData")
    fun deleteAll()
}