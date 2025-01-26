package com.penny.planner.data.db.monthlyexpenses

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MonthlyExpenseDao {

    @Insert
    suspend fun addEntity(monthlyExpenseEntity: MonthlyExpenseEntity)

    @Update
    suspend fun updateEntity(monthlyExpenseEntity: MonthlyExpenseEntity)

    @Query("SELECT * FROM monthly_expense_table WHERE entityID = :entityId AND month = :month")
    suspend fun getMonthlyExpenseEntity(entityId: String, month: String): MonthlyExpenseEntity?

    @Query("DELETE FROM monthly_expense_table WHERE entityID = :groupID")
    suspend fun removeExpenseDataForGroup(groupID: String)

}