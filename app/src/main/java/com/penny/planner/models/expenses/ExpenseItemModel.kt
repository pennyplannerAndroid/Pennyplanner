package com.penny.planner.models.expenses

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class ExpenseItemModel(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expensorId: String,
    val name: String,
    val category: String,
    val subCategory: String,
    val price: String,
    val time: String,
    val paymentType: String
    )

enum class PaymentType {
    CASH, CARD, UPI
}
