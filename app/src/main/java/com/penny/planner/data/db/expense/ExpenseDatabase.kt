package com.penny.planner.data.db.expense

import androidx.room.Database
import androidx.room.RoomDatabase
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.models.GroupDisplayModel

@Database(entities = [ExpenseEntity::class, UsersEntity::class], views = [GroupDisplayModel::class], version = 1)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}