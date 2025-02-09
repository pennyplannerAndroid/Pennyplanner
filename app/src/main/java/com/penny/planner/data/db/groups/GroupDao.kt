package com.penny.planner.data.db.groups

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupListDisplayModel

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

    @Query("SELECT * FROM groupListDisplayModel WHERE month = :month")
    fun getAllGroupListForDisplay(month: String):  LiveData<List<GroupListDisplayModel>>

    @Query("SELECT EXISTS(SELECT 1 FROM ${Utils.GROUP_TABLE} WHERE groupId = :groupId)")
    suspend fun doesGroupExists(groupId: String): Boolean

    @Query("DELETE FROM ${Utils.GROUP_TABLE} WHERE groupId= :groupId")
    suspend fun delete(groupId: String)

    @Query("UPDATE ${Utils.GROUP_TABLE} SET hasPendingMembers = :status WHERE groupId = :groupId")
    suspend fun updateEntityPendingMemberStatus(groupId: String, status: Boolean)

    @Query("UPDATE ${Utils.GROUP_TABLE} SET localImagePath = :path WHERE groupId = :groupId")
    suspend fun updatePicturePath(groupId: String, path: String)
}