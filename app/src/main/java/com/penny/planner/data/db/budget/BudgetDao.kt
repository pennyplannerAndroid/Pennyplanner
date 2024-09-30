package com.penny.planner.data.db.budget

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addBudgetItem(entity: BudgetEntity)

}