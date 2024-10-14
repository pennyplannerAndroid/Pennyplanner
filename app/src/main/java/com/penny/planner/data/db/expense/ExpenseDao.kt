package com.penny.planner.data.db.expense

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupDisplayModel

@Dao
interface ExpenseDao {

    @Query("Select * From expense_table Where entityType = 1 Order by time DESC")
    fun getAllExpenses() : LiveData<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(list: List<ExpenseEntity>)

    @Update
    suspend fun update(entity: ExpenseEntity)

    @Query("Select * From expense_table WHERE groupId = :groupId Order by time ASC")
    fun getAllExpenses(groupId: String) : LiveData<List<ExpenseEntity>>

    @Query("SELECT COUNT(*) FROM expense_table WHERE groupId = :groupId")
    suspend fun isExpenseAvailable(groupId: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: UsersEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(entity: List<UsersEntity>)

    @Update
    suspend fun update(entity: UsersEntity)

    @Query("SELECT * FROM ${Utils.FRIEND_TABLE} WHERE email In (:emails)")
    suspend fun getUsersByEmailList(emails: List<String>): List<UsersEntity>

    @Query("SELECT * FROM groupDisplayModel WHERE groupId = :groupId")
    fun getExpenseListForDisplay(groupId: String):  LiveData<List<GroupDisplayModel>>

}