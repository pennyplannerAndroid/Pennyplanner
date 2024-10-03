package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity

interface CategoryAndEmojiRepository {
    fun getAllEmoji(): List<String>

    fun checkServerAndUpdateCategory()
    fun getAllRecommendedCategories(): Map<String, String>
    fun getRecommendedSubCategory(category: String): Map<String, String>

    suspend fun getAllSavedCategories(): LiveData<List<CategoryEntity>>
    suspend fun getAllSavedSubCategories(categoryName: String): List<SubCategoryEntity>

    suspend fun addCategory(entity: CategoryEntity, limit: String)
    suspend fun addSubCategory(entity: SubCategoryEntity)

}