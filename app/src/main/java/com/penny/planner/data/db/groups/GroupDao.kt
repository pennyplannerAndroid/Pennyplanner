package com.penny.planner.data.db.groups

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.penny.planner.helpers.Utils

@Dao
interface GroupDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addGroup(entity: GroupEntity)

    @Query("SELECT * FROM ${Utils.GROUP_TABLE}")
    fun getAllGroups(): LiveData<List<GroupEntity>>

    @Query("SELECT * FROM group_table WHERE groupId = :groupId")
    suspend fun getGroupByGroupId(groupId: String): GroupEntity

    @Query("SELECT * FROM ${Utils.GROUP_TABLE}")
    fun getAllExistingGroupsFromDb(): List<GroupEntity>

    @Update
    suspend fun updateEntity(entity: GroupEntity)
}