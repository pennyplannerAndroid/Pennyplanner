package com.penny.planner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.penny.planner.models.expenses.ExpenseItemModel

@Database(entities = [ExpenseItemModel::class], version = 1)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}