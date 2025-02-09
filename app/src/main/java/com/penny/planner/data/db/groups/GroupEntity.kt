package com.penny.planner.data.db.groups

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.GROUP_TABLE)
data class GroupEntity(
    @PrimaryKey val groupId: String = "",
    var status: Int = 1,
    var name: String = "",
    var members: List<String> = listOf(),
    var profileImage: String = "",
    var creatorId: String = "",
    var lastUpdate: Timestamp = Utils.getDefaultTimestamp(),  // only local
    var monthlyBudget: Double = 0.0,
    var safeToSpendLimit: Int = 80,
    var localImagePath: String = "", // only local
    var isPending: Boolean = false, // only local
    var hasPendingMembers: Boolean = false // only local
) {
    fun toFireBaseModel() =
        mapOf(
            Pair("status", status),
            Pair("groupId", groupId),
            Pair("name", name),
            Pair("members", members),
            Pair("creatorId", creatorId),
            Pair("monthlyBudget", monthlyBudget),
            Pair("profileImage", profileImage),
            Pair("safeToSpendLimit", safeToSpendLimit)
        )
}