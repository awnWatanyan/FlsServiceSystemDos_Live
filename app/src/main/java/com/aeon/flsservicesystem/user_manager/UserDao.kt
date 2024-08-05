package com.aeon.flsservicesystem.user_manager

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: String): List<User>

    @Insert
    fun insertAll(vararg users: User)

    @Update
    fun updateUsers(vararg users: User)

    @Delete
    fun delete(user: User)

    @Query("DELETE FROM user")
    fun deleteAll()
}