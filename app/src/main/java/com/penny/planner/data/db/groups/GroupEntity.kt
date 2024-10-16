package com.penny.planner.data.db.groups

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.GROUP_TABLE)
data class GroupEntity(
    @PrimaryKey val groupId: String = "",
    val name: String = "",
    var members: List<String> = listOf(),
    var profileUrl: String = "",
    var creatorId: String = "",
    var lastUpdate: Timestamp = Utils.getDefaultTimestamp(),
    var totalSpendLimit: Double = 0.0
) {
    fun toFireBaseModel() =
        mapOf(
            Pair("groupId", groupId),
            Pair("name", name),
            Pair("members", members),
            Pair("creatorId", creatorId),
            Pair("totalSpendLimit", totalSpendLimit),
            Pair("profileUrl", profileUrl)
        )
}