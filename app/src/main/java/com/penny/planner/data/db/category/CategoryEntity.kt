package com.penny.planner.data.db.category

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.CATEGORY_TABLE)
data class CategoryEntity(
    @PrimaryKey var name: String = "",
    var searchKey: String = name.lowercase(),
    var icon: String = Utils.DEFAULT_ICON
)
