package com.penny.planner.data.db.expense

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.EXPENSE_TABLE)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String,
    val content: String,
    val entityType: Int = 1, // 0 - Message, 1- Expense
    var expensorId: String = "",
    val groupId: String = "",
    val icon: String,
    val paymentType: String,
    val price: Double,
    val subCategory: String,
    val time: Long = System.currentTimeMillis(),
    val uploadedOnServer: Boolean = false
)