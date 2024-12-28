package com.penny.planner.data.db.friends

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.penny.planner.helpers.Utils

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(entity: UsersEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(entity: List<UsersEntity>)

    @Update
    suspend fun update(entity: UsersEntity)

    @Query("SELECT * FROM ${Utils.FRIEND_TABLE} WHERE email In (:emails)")
    suspend fun getUsersByEmailList(emails: List<String>): List<UsersEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM ${Utils.FRIEND_TABLE} WHERE email = :email)")
    suspend fun doesFriendExists(email: String): Boolean

    @Query("SELECT * FROM ${Utils.FRIEND_TABLE}")
    suspend fun getAllFriends(): List<UsersEntity>

    @Query("SELECT * FROM ${Utils.FRIEND_TABLE} where email = :email")
    suspend fun findFriend(email: String): UsersEntity
}