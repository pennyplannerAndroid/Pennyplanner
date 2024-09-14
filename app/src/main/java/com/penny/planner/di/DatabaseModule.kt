package com.penny.planner.di

import android.content.Context
import androidx.room.Room
import com.penny.planner.data.db.category.CategoryDatabase
import com.penny.planner.data.db.expense.ExpenseDatabase
import com.penny.planner.data.db.subcategory.SubCategoryDatabase
import com.penny.planner.helpers.Utils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun bindCategoryDatabase(@ApplicationContext application: Context) =
        Room.databaseBuilder(application, CategoryDatabase::class.java, Utils.CATEGORY_TABLE).build()

    @Singleton
    @Provides
    fun bindCategoryDao(db: CategoryDatabase) = db.categoryDao()

    @Singleton
    @Provides
    fun bindSubCategoryDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, SubCategoryDatabase::class.java, Utils.SUB_CATEGORY_TABLE).build()

    @Singleton
    @Provides
    fun bindSubCategoryDao(db: SubCategoryDatabase) = db.getSubCategoryDao()

    @Singleton
    @Provides
    fun bindExpenseDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, ExpenseDatabase::class.java, Utils.EXPENSE_TABLE).build()

    @Singleton
    @Provides
    fun bindExpenseDao(db: ExpenseDatabase) = db.expenseDao()

}