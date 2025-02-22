package com.penny.planner.data.db.expense

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.EXPENSE_TABLE)
data class ExpenseEntity(
    @PrimaryKey var id: String = "",
    val category: String = "",
    val content: String = "",
    val entityType: Int = 1, // 0 - Message, 1- Expense
    var expensorId: String = "",
    var groupId: String = "",
    val icon: String = "",
    val paymentType: String = "",
    val price: Double = 0.0,
    val subCategory: String = "",
    var time: Timestamp = Utils.getCurrentTimeStamp(),
    var uploadedOnServer: Boolean = false,
    var isSentTransaction: Boolean = true
) {

    fun toFireBaseEntity() =
        mapOf(
            Pair("id", id),
            Pair("category", category),
            Pair("content", content),
            Pair("entityType", entityType),
            Pair("expensorId", expensorId),
            Pair("groupId", groupId),
            Pair("icon", icon),
            Pair("paymentType", paymentType),
            Pair("price", price),
            Pair("subCategory", subCategory),
            Pair("time", FieldValue.serverTimestamp())
    )
}