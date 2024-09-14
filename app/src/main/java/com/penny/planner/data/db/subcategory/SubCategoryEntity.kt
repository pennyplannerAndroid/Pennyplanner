package com.penny.planner.data.db.subcategory

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.penny.planner.helpers.Utils

@Entity(tableName = Utils.SUB_CATEGORY_TABLE)
data class SubCategoryEntity(
    @PrimaryKey var name: String = "",
    var category: String = ""
)