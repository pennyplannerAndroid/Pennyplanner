package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.models.NameIconPairWithKeyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val repository: CategoryAndEmojiRepository
): ViewModel() {

    private var selectedCategory: CategoryEntity? = null
    private var selectedSubCategory: SubCategoryEntity? = null
    private var categoryEditable = false
    private var subCategoryEditable = false
    var limit = ""
    var addCategoryToDb = false

    fun setSelectedCategory(item: CategoryEntity?) {
        selectedCategory = item
    }

    fun setSelectedSubCategory(item: SubCategoryEntity?) {
        selectedSubCategory = item
    }

    fun getSelectedCategory() = selectedCategory

    fun getSelectedSubCategory() = selectedSubCategory

    fun deleteSelectedCategory() {
        selectedCategory = null
    }

    fun deleteSelectedSubCategory() {
        selectedSubCategory = null
    }

    fun setCategoryEditable(value: Boolean) {
        categoryEditable = value
    }

    fun getCategoryEditable() = categoryEditable

    fun setSubCategoryEditable(value: Boolean) {
        subCategoryEditable = value
    }

    fun getSubCategoryEditable() = subCategoryEditable

    fun getAllEmojis(): List<String> = repository.getAllEmoji()

    fun getAllRecommendedCategories(): List<NameIconPairWithKeyModel> = repository.getAllRecommendedCategories()

    fun getAllRecommendedSubCategories(categoryName: String) : List<NameIconPairWithKeyModel> = repository.getRecommendedSubCategory(categoryName)

    suspend fun getAllSavedCategories() = repository.getAllSavedCategories()

    suspend fun getAllSavedSubCategories(categoryName: String): List<SubCategoryEntity> = repository.getAllSavedSubCategories(categoryName)

    fun addCategory(entity: CategoryEntity) {
        viewModelScope.launch {
            repository.addCategory(entity, limit)
        }
    }

    fun addSubCategory(entity: SubCategoryEntity) {
        viewModelScope.launch {
            repository.addSubCategory(entity)
        }
    }

}