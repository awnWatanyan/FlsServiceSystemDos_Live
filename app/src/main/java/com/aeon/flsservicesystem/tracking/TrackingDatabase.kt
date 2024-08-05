package com.aeon.flsservicesystem.tracking

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TrackingData::class], version = 1)
abstract class TrackingDatabase : RoomDatabase(){
    abstract fun trackingDao(): TrackingDataDao
}