package com.penny.planner.data.db.budget

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.BUDGET_TABLE)
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true) var id: Int = 0,
    val category: String = "",
    var spendLimit: Double = 0.0,
    val entityId: String = "",
    var alertAdded: Boolean = false,
    var alertLimit: Int = 80,
    var icon: String = Utils.DEFAULT_ICON,
    var uploadedOnServer: Boolean = false
) {
    fun toFireBaseEntity() =
        mapOf(
            Pair("category", category),
            Pair("spendLimit", spendLimit),
            Pair("alertAdded", alertAdded),
            Pair("entityId", entityId),
            Pair("alertLimit", alertLimit),
            Pair("icon", icon)
        )
}