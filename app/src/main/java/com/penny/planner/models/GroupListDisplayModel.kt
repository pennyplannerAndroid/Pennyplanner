package com.penny.planner.models

import androidx.room.DatabaseView
import com.google.firebase.Timestamp
import com.penny.planner.helpers.Utils

@DatabaseView(
    """
        SELECT u.groupId AS groupId, u.name AS name, u.creatorId AS creatorId,
        u.members AS members, u.localImagePath AS profileImage, u.lastUpdate AS lastUpdate, 
        u.monthlyBudget AS monthlyBudget, u.safeToSpendLimit AS safeToSpendLimit, u.isPending AS pending,
        t.month AS month, t.expense AS expense
        FROM group_table u
        INNER JOIN monthly_expense_table t ON u.groupId = t.entityID Order by lastUpdate DESC
    """,
    viewName = "groupListDisplayModel"
)
data class GroupListDisplayModel(
    var pending: Boolean = false,
    val groupId: String = "",
    val name: String = "",
    var members: List<String> = listOf(),
    var profileImage: String = "",
    var creatorId: String = "",
    var lastUpdate: Timestamp = Utils.getDefaultTimestamp(),
    var monthlyBudget: Double = 0.0,
    var safeToSpendLimit: Int = 80,
    var month: String = "",
    var expense: Double = 0.0
)