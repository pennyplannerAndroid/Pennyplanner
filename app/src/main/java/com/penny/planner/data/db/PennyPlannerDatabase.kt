package com.penny.planner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.penny.planner.data.db.budget.BudgetDao
import com.penny.planner.data.db.budget.BudgetEntity
import com.penny.planner.data.db.category.CategoryDao
import com.penny.planner.data.db.category.CategoryEntity
import com.penny.planner.data.db.expense.ExpenseDao
import com.penny.planner.data.db.expense.ExpenseEntity
import com.penny.planner.data.db.friends.UsersDao
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.data.db.groups.GroupDao
import com.penny.planner.data.db.groups.GroupEntity
import com.penny.planner.data.db.subcategory.SubCategoryDao
import com.penny.planner.data.db.subcategory.SubCategoryEntity
import com.penny.planner.helpers.ArrayListConverter
import com.penny.planner.helpers.FirebaseTimestampConverter
import com.penny.planner.models.GroupDisplayModel

@TypeConverters(ArrayListConverter::class, FirebaseTimestampConverter::class)
@Database(
    entities = [BudgetEntity::class, CategoryEntity::class,
        ExpenseEntity::class, UsersEntity::class,
        GroupEntity::class, SubCategoryEntity::class],
    views = [GroupDisplayModel::class],
    version = 1
)
abstract class PennyPlannerDatabase : RoomDatabase() {
    abstract fun getBudgetDao(): BudgetDao
    abstract fun categoryDao(): CategoryDao
    abstract fun usersDao(): UsersDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun getGroupDao(): GroupDao
    abstract fun getSubCategoryDao(): SubCategoryDao
}