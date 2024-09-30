package com.penny.planner.data.db.budget

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [BudgetEntity::class], version = 1)
abstract class BudgetDatabase: RoomDatabase() {
    abstract fun getBudgetDao(): BudgetDao
}