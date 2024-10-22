package com.penny.planner.data.db.monthlyexpenses

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.MONTHLY_EXPENSE_TABLE)
data class MonthlyExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var entityID: String = "",
    var month: String = "",
    var expense: Double = 0.0
)