package com.penny.planner.data.db.friends

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.penny.planner.helpers.Utils

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UsersEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(entity: List<UsersEntity>)

    @Update
    suspend fun update(entity: UsersEntity)

    @Query("SELECT * FROM ${Utils.FRIEND_TABLE} WHERE email In (:emails)")
    suspend fun getUsersByEmailList(emails: List<String>): List<UsersEntity>
}