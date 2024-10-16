package com.penny.planner.data.db.expense

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.penny.planner.models.GroupDisplayModel

@Dao
interface ExpenseDao {

    @Query("Select * From expense_table Where entityType = 1 Order by time DESC")
    fun getAllExpenses() : LiveData<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<ExpenseEntity>)

    @Update
    suspend fun update(entity: ExpenseEntity)

    @Query("Select * From expense_table WHERE groupId = :groupId Order by time ASC")
    fun getAllExpenses(groupId: String) : LiveData<List<ExpenseEntity>>

    @Query("SELECT * FROM groupDisplayModel WHERE groupId = :groupId")
    fun getExpenseListForDisplay(groupId: String):  LiveData<List<GroupDisplayModel>>

}