package com.penny.planner.data.db.expense

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils
import com.penny.planner.helpers.enums.PaymentType

@Entity(tableName = Utils.EXPENSE_TABLE)
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: Int = 1, // 0 - Message, 1- Expense
    val group: Boolean = false,
    val groupId: String = "",
    var expensorId: String = "",
    val content: String,
    val category: String,
    val subCategory: String,
    val price: String,
    val time: Long = System.currentTimeMillis(),
    val paymentType: String = PaymentType.CASH.toString(),
    val icon: String
)