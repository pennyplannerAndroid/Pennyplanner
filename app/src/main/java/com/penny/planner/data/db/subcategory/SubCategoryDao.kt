package com.penny.planner.data.db.subcategory

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.penny.planner.helpers.Utils

@Dao
interface SubCategoryDao {

    @Query("SELECT * FROM ${Utils.SUB_CATEGORY_TABLE} WHERE category == :categoryName")
    suspend fun getAllSubCategories(categoryName: String): List<SubCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSubCategory(entity: SubCategoryEntity)

    @Query("SELECT * FROM ${Utils.SUB_CATEGORY_TABLE} WHERE category == :categoryName AND name ==:subCategoryName")
    suspend fun getSubCategory(categoryName: String, subCategoryName: String): List<SubCategoryEntity>

    @Query("SELECT name FROM subcategory_table WHERE category == :categoryName")
    suspend fun getAllSubCategoryName(categoryName: String): List<String>

}