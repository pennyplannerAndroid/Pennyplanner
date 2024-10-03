package com.penny.planner.data.db.budget

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.BUDGET_TABLE)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val name: String,
    var limit: String,
    val groupId: String = ""
)