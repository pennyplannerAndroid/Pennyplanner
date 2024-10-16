package com.penny.planner.di

import android.content.Context
import androidx.room.Room
import com.penny.planner.data.db.PennyPlannerDatabase
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
    fun bindPennyPlannerDatabase(@ApplicationContext application: Context) =
        Room.databaseBuilder(application, PennyPlannerDatabase::class.java, Utils.PENNY_DATABASE).build()

    @Singleton
    @Provides
    fun bindBudgetDao(db: PennyPlannerDatabase) = db.getBudgetDao()

    @Singleton
    @Provides
    fun bindCategoryDao(db: PennyPlannerDatabase) = db.categoryDao()

    @Singleton
    @Provides
    fun bindUsersDao(db: PennyPlannerDatabase) = db.usersDao()

    @Singleton
    @Provides
    fun bindExpenseDao(db: PennyPlannerDatabase) = db.expenseDao()

    @Singleton
    @Provides
    fun bindGroupDao(db: PennyPlannerDatabase) = db.getGroupDao()

    @Singleton
    @Provides
    fun bindSubCategoryDao(db: PennyPlannerDatabase) = db.getSubCategoryDao()

}