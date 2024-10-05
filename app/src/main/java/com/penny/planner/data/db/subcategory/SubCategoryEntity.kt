package com.penny.planner.data.db.subcategory

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.SUB_CATEGORY_TABLE)
data class SubCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var name: String = "",
    var category: String = "",
    var searchKey: String = name.lowercase(),
    var icon: String = Utils.DEFAULT_ICON
)