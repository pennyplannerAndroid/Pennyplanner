package com.penny.planner.data.db.expense

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.penny.planner.helpers.Utils
import com.penny.planner.models.GroupDisplayModel

@Dao
interface ExpenseDao {

    @Query("Select * From groupDisplayModel Where entityType = 1 AND groupId = :groupId AND time BETWEEN :start AND :end")
    fun getAllExpensesExceptMessages(groupId: String, start: Long, end: Long) : List<GroupDisplayModel>

    @Query("Select * From expense_table Where entityType = 1 AND expensorId = :personalId Order by time DESC LIMIT ${Utils.HOME_PAGE_EXPENSE_DISPLAY_COUNT}")
    fun getExpensesForDisplayAtHomePage(personalId: String) : LiveData<List<ExpenseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ExpenseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertList(list: List<ExpenseEntity>)

    @Query("SELECT EXISTS(SELECT 1 FROM ${Utils.EXPENSE_TABLE} WHERE id = :expenseId)")
    suspend fun doesExpenseExists(expenseId: String): Boolean

    @Update
    suspend fun update(entity: ExpenseEntity)

    @Query("Select * From expense_table WHERE groupId = :groupId Order by time ASC")
    fun getAllExpenses(groupId: String) : LiveData<List<ExpenseEntity>>

    @Query("SELECT * FROM groupDisplayModel WHERE groupId = :groupId")
    fun getExpenseListForDisplay(groupId: String):  LiveData<List<GroupDisplayModel>>

}