package com.penny.planner.data.db.groups

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.GROUP_TABLE)
data class GroupEntity(
    @PrimaryKey val groupId: String = "",
    val name: String = "",
    var members: List<String> = listOf(),
    var profileUrl: String? = "",
    var creatorId: String? = "",
    var version: Long = 0,
    var lastUpdate: Long = 0,
    var totalSpendLimit: Double = 0.0
)