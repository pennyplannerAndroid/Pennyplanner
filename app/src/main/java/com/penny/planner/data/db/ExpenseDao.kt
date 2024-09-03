package com.penny.planner.data.db

import androidx.room.Dao
import androidx.room.Query
import com.penny.planner.models.expenses.ExpenseItemModel

@Dao
interface ExpenseDao {

    @Query("Select * From expense_table Order by time DESC")
    fun getAllExpenses() : List<ExpenseItemModel>

}