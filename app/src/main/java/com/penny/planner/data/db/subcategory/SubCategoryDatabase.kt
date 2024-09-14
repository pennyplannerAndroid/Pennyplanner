package com.penny.planner.data.db.subcategory

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SubCategoryEntity::class], version = 1)
abstract class SubCategoryDatabase: RoomDatabase() {
    abstract fun getSubCategoryDao(): SubCategoryDao
}