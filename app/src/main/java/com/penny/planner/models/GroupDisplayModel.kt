package com.penny.planner.models

import androidx.room.DatabaseView
import com.google.firebase.Timestamp
import com.penny.planner.helpers.Utils

@DatabaseView("""
        SELECT t.category AS category, t.content AS content, t.entityType AS entityType,
        t.icon AS icon, t.paymentType AS paymentType, t.price AS price, t.groupId AS groupId,
        t.subCategory AS subCategory, t.time AS time, t.isSentTransaction AS isSentTransaction,
        u.name AS senderName, u.email AS senderEmail, u.profileImageURL AS senderImage, u.localImagePath AS localImagePath
        FROM friend_table u 
        INNER JOIN expense_table t ON u.id = t.expensorId
    """,
    viewName = "groupDisplayModel"
)
data class GroupDisplayModel(
    val category: String = "",
    val content: String = "",
    val entityType: Int = 1, // 0 - Message, 1- Expense
    val icon: String = "",
    val paymentType: String = "",
    val price: Double = 0.0,
    val subCategory: String = "",
    val groupId: String = "",
    val time: Timestamp = Utils.getDefaultTimestamp(),
    var isSentTransaction: Boolean = true,
    var senderName: String = "",
    var senderEmail: String = "",
    var senderImage: String = "",
    val localImagePath: String = ""
)