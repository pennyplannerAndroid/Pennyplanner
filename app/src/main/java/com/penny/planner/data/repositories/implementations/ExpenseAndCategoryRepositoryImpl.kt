package com.penny.planner.data.repositories.implementations

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.penny.planner.data.db.category.CategoryDao
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseDao
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.subcategory.SubCategoryDao
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.data.repositories.interfaces.ExpenseAndCategoryRepository
import javax.inject.Inject

class ExpenseAndCategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val subCategoryDao: SubCategoryDao,
    private val expenseDao: ExpenseDao
) : ExpenseAndCategoryRepository {

    private val auth = FirebaseAuth.getInstance()

    override suspend fun getAllExpenses(): LiveData<List<ExpenseEntity>> = expenseDao.getAllExpenses()

    override suspend fun getAllCategories(): LiveData<List<CategoryEntity>> = categoryDao.getAllCategories()

    override suspend fun getAllSubCategories(categoryName: String): List<String> = subCategoryDao.getAllSubCategories(categoryName)

    override suspend fun addExpense(entity: ExpenseEntity) {
        entity.expensorId = auth.currentUser?.uid ?: ""
        expenseDao.insert(entity)
    }

    override suspend fun addCategory(entity: CategoryEntity) {
        categoryDao.insert(entity)
    }

    override suspend fun addSubCategory(entity: SubCategoryEntity) {
        subCategoryDao.addSubCategory(entity)
    }

}