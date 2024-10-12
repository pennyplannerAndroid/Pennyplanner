package com.penny.planner.data.db.expense

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao {

    @Query("Select * From expense_table Order by time DESC")
    fun getAllExpenses() : LiveData<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(list: List<ExpenseEntity>)

    @Update
    suspend fun update(entity: ExpenseEntity)

    @Query("Select * From expense_table WHERE groupId = :groupId Order by time DESC")
    fun getAllExpenses(groupId: String) : LiveData<List<ExpenseEntity>>

    @Query("SELECT COUNT(*) FROM expense_table WHERE groupId = :groupId")
    suspend fun isExpenseAvailable(groupId: String): Int

}