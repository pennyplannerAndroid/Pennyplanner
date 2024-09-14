package com.penny.planner.data.db.subcategory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.penny.planner.helpers.Utils

@Dao
interface SubCategoryDao {

    @Query("SELECT name FROM ${Utils.SUB_CATEGORY_TABLE} WHERE category == :categoryName")
    fun getAllSubCategories(categoryName: String): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSubCategory(entity: SubCategoryEntity)

}