package com.penny.planner.data.repositories.interfaces

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity

interface ExpenseAndCategoryRepository {
    suspend fun addCategory(entity: CategoryEntity)
    suspend fun addSubCategory(entity: SubCategoryEntity)
    suspend fun addExpense(entity: ExpenseEntity)

    suspend fun getAllCategories(): LiveData<List<CategoryEntity>>
    suspend fun getAllSubCategories(categoryName: String): List<String>
    suspend fun getAllExpenses(): LiveData<List<ExpenseEntity>>
}