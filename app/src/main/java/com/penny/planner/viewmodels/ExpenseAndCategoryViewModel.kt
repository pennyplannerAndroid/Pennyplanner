package com.penny.planner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.data.repositories.interfaces.ExpenseAndCategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExpenseAndCategoryViewModel @Inject constructor(
    private val expenseAndCategoryRepository: ExpenseAndCategoryRepository
): ViewModel() {
    suspend fun getAllCategories() = expenseAndCategoryRepository.getAllCategories()

    suspend fun getSubCategories(categoryName: String): List<String> = expenseAndCategoryRepository.getAllSubCategories(categoryName)

    fun addCategory(entity: CategoryEntity) {
        viewModelScope.launch {
            expenseAndCategoryRepository.addCategory(entity)

        }
    }

    fun addSubCategory(entity: SubCategoryEntity) {
        viewModelScope.launch {
            expenseAndCategoryRepository.addSubCategory(entity)
        }
    }

    fun addExpense(entity: ExpenseEntity) {
        viewModelScope.launch {
            expenseAndCategoryRepository.addExpense(entity)
        }
    }

    suspend fun getAllExpense() = expenseAndCategoryRepository.getAllExpenses()
}