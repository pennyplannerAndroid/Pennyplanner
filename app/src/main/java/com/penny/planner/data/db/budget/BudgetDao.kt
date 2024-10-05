package com.penny.planner.data.db.budget

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addBudgetItem(entity: BudgetEntity)

    @Query("Select EXISTS (SELECT 1 From budget_table Where category == :category AND entityId = :entityId)")
    suspend fun isBudgetAvailable(entityId: String, category: String): Boolean

}