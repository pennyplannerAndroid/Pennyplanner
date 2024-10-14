package com.penny.planner.data.db.friends

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.FRIEND_TABLE)
data class UsersEntity(
    @PrimaryKey val id: String = "",
    val email: String = "",
    val name: String = "",
    val profileImageURL: String = ""
)