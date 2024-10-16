package com.penny.planner.data.db.expense

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.penny.planner.data.db.friends.UsersEntity
import com.penny.planner.helpers.FirebaseTimestampConverter
import com.penny.planner.models.GroupDisplayModel

@TypeConverters(FirebaseTimestampConverter::class)
@Database(entities = [ExpenseEntity::class, UsersEntity::class], views = [GroupDisplayModel::class], version = 1)
abstract class ExpenseDatabase : RoomDatabase() {
    abstract fun expenseDao(): ExpenseDao
}