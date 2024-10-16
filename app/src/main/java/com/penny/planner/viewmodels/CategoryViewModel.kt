package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.data.repositories.interfaces.BudgetRepository
import com.penny.planner.data.repositories.interfaces.CategoryAndEmojiRepository
import com.penny.planner.data.repositories.interfaces.FirebaseBackgroundSyncRepository
import com.penny.planner.models.NameIconPairWithKeyModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryAndEmojiRepository: CategoryAndEmojiRepository,
    private val budgetRepository: BudgetRepository,
    private val backgroundSyncRepository: FirebaseBackgroundSyncRepository
): ViewModel() {

    private var selectedCategory: CategoryEntity? = null
    private var selectedSubCategory: SubCategoryEntity? = null
    private var categoryEditable = false
    private var subCategoryEditable = false
    var limit = ""
    var addCategoryToDb = false
    var addBudget = true

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

    fun getAllEmojis(): List<String> = categoryAndEmojiRepository.getAllEmoji()

    fun getAllRecommendedCategories(): List<NameIconPairWithKeyModel> = categoryAndEmojiRepository.getAllRecommendedCategories()

    fun getAllRecommendedSubCategories(categoryName: String) : List<NameIconPairWithKeyModel> = categoryAndEmojiRepository.getRecommendedSubCategory(categoryName)

    suspend fun getAllSavedCategories() = categoryAndEmojiRepository.getAllSavedCategories()

    suspend fun getAllSavedSubCategories(categoryName: String): List<SubCategoryEntity> = categoryAndEmojiRepository.getAllSavedSubCategories(categoryName)

    private fun addCategory(entity: CategoryEntity) {
        viewModelScope.launch {
            categoryAndEmojiRepository.addCategory(entity)
        }
    }

    private fun addSubCategory(entity: SubCategoryEntity) {
        viewModelScope.launch {
            backgroundSyncRepository.newSubCategoryAdded(entity)
            categoryAndEmojiRepository.addSubCategory(entity)
        }
    }

    private fun addBudget(entity: CategoryEntity, groupId: String) {
        viewModelScope.launch {
            budgetRepository.createBudgetLocally(
                category = entity.name,
                icon = entity.icon,
                spendLimit = limit.toDouble(),
                entityId = groupId
            )
        }
    }

    fun addCategoryAndBudgetToDb(groupId: String) {
        if (addCategoryToDb)
            addCategory(selectedCategory!!)
        if (selectedSubCategory != null)
            addSubCategory(selectedSubCategory!!)
        if (addBudget)
            addBudget(selectedCategory!!, groupId)
        resetLocalValues()
    }

    private fun resetLocalValues() {
        deleteSelectedSubCategory()
        deleteSelectedCategory()
        limit = ""
        addCategoryToDb = false
        addBudget = true
    }

    suspend fun doesBudgetExists(entityId: String, category: String) =
        budgetRepository.isBudgetAvailable(entityId = entityId, category = category)

}