package com.penny.planner.data.repositories

import androidx.lifecycle.LiveData
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.models.GroupModel
import com.penny.planner.models.UserModel

interface UserAndExpenseRepository {
    suspend fun newGroup(group: GroupModel, byteArray: ByteArray?): Result<Boolean>
    fun getUserName() : String
    fun getEmail() : String
    suspend fun findUser(email: String): Result<UserModel>
    suspend fun addCategory(entity: CategoryEntity)
    suspend fun getAllCategories(): LiveData<List<CategoryEntity>>
    suspend fun getAllSubCategories(categoryName: String): List<String>
    suspend fun getAllExpenses(): LiveData<List<ExpenseEntity>>
    suspend fun addExpense(entity: ExpenseEntity)
    suspend fun addSubCategory(entity: SubCategoryEntity)

}